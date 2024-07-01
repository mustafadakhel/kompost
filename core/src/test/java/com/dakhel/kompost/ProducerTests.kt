package com.dakhel.kompost

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

}

