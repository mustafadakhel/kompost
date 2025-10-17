package com.mustafadakhel.kompost.core

import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class ProducerTests {

    private val defaultProducer: DefaultProducer = DefaultProducer("TestFarm", globalFarm()).also {
        globalFarm().produce("TestFarm") { it }
    }

    @BeforeTest
    fun setup() {
        resetGlobalFarm()
    }

    @Test
    fun `Farm is created successfully`() {
        assertNotNull(defaultProducer, "Farm should be created")
    }

    @Test
    fun `Farm stores and supplies dependencies correctly`() {
        val dependencyMock: SomeDependency = mockk()

        defaultProducer.produce { dependencyMock }

        val suppliedDependency: SomeDependency = defaultProducer.supply()

        assertNotNull(suppliedDependency, "Dependency should be supplied")
    }

    @Test
    fun `Farm throws when supplying unregistered dependency`() {
        assertFailsWith<NoSuchSeedException>(
            "NoSuchSeedException should be thrown when supplying unregistered dependency"
        ) { defaultProducer.supply<GlobalDependency>() }
    }

    @Test
    fun `Farm throws when producing duplicate dependencies`() {
        val dependencyMock: SomeDependency = mockk()

        defaultProducer.produce { dependencyMock }
        assertFailsWith<DuplicateProduceException>(
            "DuplicateProduceException should be thrown when producing duplicate dependencies"
        ) { defaultProducer.produce { dependencyMock } }
    }

    @Test
    fun `Child farm supplies dependency from Farm when not found locally`() {
        val globalDependency: GlobalDependency = mockk()
        defaultProducer.produce { globalDependency }

        val childProducer = DefaultProducer("ChildFarm", defaultProducer)

        val suppliedDependency: GlobalDependency = childProducer.supply()

        assertNotNull(
            suppliedDependency,
            "Child farm should supply dependency from Farm"
        )
    }

    @Test
    fun `Farm destroys individual dependencies correctly`() {
        val dependencyKey = ProduceKey(SomeDependency::class)
        val dependencyMock: SomeDependency = mockk()

        defaultProducer.produce(dependencyKey) { dependencyMock }
        defaultProducer.destroy(dependencyKey)

        val exception = assertFailsWith<NoSuchSeedException> {
            defaultProducer.supply<SomeDependency>()
        }

        assertNotNull(
            exception,
            "NoSuchSeedException should be thrown after dependency is destroyed"
        )
    }

    @Test
    fun `Farm destroys all dependencies correctly`() {
        defaultProducer.produce { mockk<SomeDependency>() }
        defaultProducer.produce(tag = "Another") { mockk<AnotherDependency>() }

        defaultProducer.destroyAllCrops()

        assertFailsWith<NoSuchSeedException>(
            "NoSuchSeedException should be thrown after all dependencies are destroyed"
        ) { defaultProducer.supply<SomeDependency>() }
        assertFailsWith<NoSuchSeedException>(
            "NoSuchSeedException should be thrown after all dependencies are destroyed"
        ) { defaultProducer.supply<AnotherDependency>(tag = "Another") }
    }

    @Test
    fun `Farm supplies tagged dependencies correctly`() {
        val someDependency: SomeDependency = mockk()
        val anotherDependency: AnotherDependency = mockk()

        defaultProducer.produce(tag = "SomeTag") { someDependency }
        defaultProducer.produce(tag = "AnotherTag") { anotherDependency }

        val suppliedSomeDependency: SomeDependency = defaultProducer.supply(tag = "SomeTag")
        val suppliedAnotherDependency: AnotherDependency =
            defaultProducer.supply(tag = "AnotherTag")

        assertSame(
            someDependency,
            suppliedSomeDependency,
            "Tagged dependency (SomeTag) should be supplied correctly"
        )
        assertSame(
            anotherDependency,
            suppliedAnotherDependency,
            "Tagged dependency (AnotherTag) should be supplied correctly"
        )
    }

    @Test
    fun `Farm supplies dependencies with constructor parameters correctly`() {
        val expectedParam = "expectedParam"
        defaultProducer.produce { DependencyWithParams(expectedParam) }

        val dependency: DependencyWithParams = defaultProducer.supply()

        assertSame(
            expectedParam,
            dependency.param,
            "Dependency parameter should match the expected value"
        )
    }

    @Test
    fun `Farm detects direct circular dependencies`() {
        // Create a circular dependency: A depends on A
        defaultProducer.produce<SomeDependency> {
            defaultProducer.supply<SomeDependency>()
        }

        val exception = assertFailsWith<CircularDependencyException>(
            "CircularDependencyException should be thrown for direct circular dependency"
        ) {
            defaultProducer.supply<SomeDependency>()
        }

        assertNotNull(exception, "Exception should be thrown")
        assertNotNull(exception.message, "Exception should have a message")
    }

    @Test
    fun `Farm detects indirect circular dependencies`() {
        // Create a circular dependency chain: A -> B -> A
        defaultProducer.produce<SomeDependency> {
            defaultProducer.supply<AnotherDependency>()
            mockk<SomeDependency>()
        }
        defaultProducer.produce<AnotherDependency> {
            defaultProducer.supply<SomeDependency>()
            mockk<AnotherDependency>()
        }

        val exception = assertFailsWith<CircularDependencyException>(
            "CircularDependencyException should be thrown for indirect circular dependency"
        ) {
            defaultProducer.supply<SomeDependency>()
        }

        assertNotNull(exception, "Exception should be thrown")
        assertNotNull(exception.message, "Exception should have a message")
    }

    @Test
    fun `supplyOrNull returns null for missing dependency`() {
        val result = defaultProducer.supplyOrNull<SomeDependency>()

        assertSame(null, result, "supplyOrNull should return null for missing dependency")
    }

    @Test
    fun `supplyOrNull returns dependency when found`() {
        val dependency: SomeDependency = mockk()
        defaultProducer.produce { dependency }

        val result = defaultProducer.supplyOrNull<SomeDependency>()

        assertSame(dependency, result, "supplyOrNull should return the dependency when found")
    }

    @Test
    fun `supplyOrDefault returns default for missing dependency`() {
        val defaultDependency: SomeDependency = mockk()

        val result = defaultProducer.supplyOrDefault(defaultValue = defaultDependency)

        assertSame(
            defaultDependency,
            result,
            "supplyOrDefault should return default value for missing dependency"
        )
    }

    @Test
    fun `supplyOrDefault returns dependency when found`() {
        val dependency: SomeDependency = mockk()
        val defaultDependency: SomeDependency = mockk()
        defaultProducer.produce { dependency }

        val result = defaultProducer.supplyOrDefault(defaultValue = defaultDependency)

        assertSame(
            dependency,
            result,
            "supplyOrDefault should return the found dependency, not the default"
        )
    }

    @Test
    fun `supplyOrElse computes fallback for missing dependency`() {
        val fallbackDependency: SomeDependency = mockk()
        var fallbackCalled = false

        val result = defaultProducer.supplyOrElse<SomeDependency> {
            fallbackCalled = true
            fallbackDependency
        }

        assertSame(
            fallbackDependency,
            result,
            "supplyOrElse should return fallback value for missing dependency"
        )
        assertSame(true, fallbackCalled, "Fallback lambda should be called")
    }

    @Test
    fun `supplyOrElse returns dependency without calling fallback when found`() {
        val dependency: SomeDependency = mockk()
        var fallbackCalled = false
        defaultProducer.produce { dependency }

        val result = defaultProducer.supplyOrElse<SomeDependency> {
            fallbackCalled = true
            mockk()
        }

        assertSame(
            dependency,
            result,
            "supplyOrElse should return the found dependency"
        )
        assertSame(false, fallbackCalled, "Fallback lambda should not be called when dependency is found")
    }

    @Test
    fun `getAllKeys returns all registered keys`() {
        defaultProducer.produce<SomeDependency> { mockk() }
        defaultProducer.produce<AnotherDependency>(tag = "tagged") { mockk() }

        val keys = defaultProducer.getAllKeys()

        assertSame(2, keys.size, "Should have 2 registered keys")
        assertSame(
            true,
            keys.any { it.getClassName().contains("SomeDependency") },
            "Should contain SomeDependency key"
        )
        assertSame(
            true,
            keys.any { it.getClassName().contains("AnotherDependency") && it.getTag() == "tagged" },
            "Should contain tagged AnotherDependency key"
        )
    }

    @Test
    fun `getAllKeys returns empty set when no dependencies registered`() {
        val keys = defaultProducer.getAllKeys()

        assertSame(0, keys.size, "Should return empty set when no dependencies registered")
    }

    @Test
    fun `getAllKeysIncludingParents returns keys from producer and parents`() {
        val parentDependency: GlobalDependency = mockk()
        defaultProducer.produce { parentDependency }

        val childProducer = DefaultProducer("ChildFarm", defaultProducer)
        childProducer.produce<SomeDependency> { mockk() }

        val keys = childProducer.getAllKeysIncludingParents()

        assertSame(
            true,
            keys.size >= 2,
            "Should have at least 2 keys (child + parent)"
        )
        assertSame(
            true,
            keys.any { it.getClassName().contains("SomeDependency") },
            "Should contain SomeDependency from child"
        )
        assertSame(
            true,
            keys.any { it.getClassName().contains("GlobalDependency") },
            "Should contain GlobalDependency from parent"
        )
    }

    @Test
    fun `getDependencyGraph returns graph structure`() {
        defaultProducer.produce<GlobalDependency> { mockk() }

        val childProducer = DefaultProducer("ChildFarm", defaultProducer)
        childProducer.produce<SomeDependency> { mockk() }

        val graph = childProducer.getDependencyGraph()

        assertSame(
            true,
            graph.containsKey("ChildFarm"),
            "Graph should contain child farm"
        )
        assertSame(
            true,
            graph.containsKey("TestFarm"),
            "Graph should contain parent farm"
        )
        assertSame(
            true,
            graph["ChildFarm"]?.any { it.getClassName().contains("SomeDependency") } == true,
            "ChildFarm should have SomeDependency"
        )
        assertSame(
            true,
            graph["TestFarm"]?.any { it.getClassName().contains("GlobalDependency") } == true,
            "TestFarm should have GlobalDependency"
        )
    }

    @Test
    fun `contains with reified type works correctly`() {
        defaultProducer.produce<SomeDependency> { mockk() }

        assertSame(true, defaultProducer.contains<SomeDependency>(), "Should contain SomeDependency")
        assertSame(
            false,
            defaultProducer.contains<AnotherDependency>(),
            "Should not contain AnotherDependency"
        )
    }

    @Test
    fun `contains with reified type and tag works correctly`() {
        defaultProducer.produce<SomeDependency>(tag = "special") { mockk() }

        assertSame(
            true,
            defaultProducer.contains<SomeDependency>(tag = "special"),
            "Should contain SomeDependency with tag 'special'"
        )
        assertSame(
            false,
            defaultProducer.contains<SomeDependency>(tag = "other"),
            "Should not contain SomeDependency with tag 'other'"
        )
        assertSame(
            false,
            defaultProducer.contains<SomeDependency>(),
            "Should not contain untagged SomeDependency"
        )
    }

}

