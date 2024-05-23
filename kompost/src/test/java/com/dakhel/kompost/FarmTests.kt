package com.dakhel.kompost

import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class FarmTests {

    private val farm: Farm = Farm("TestFarm", globalFarm()).also {
        globalFarm().produce("TestFarm") { it }
    }

    @BeforeTest
    fun setup() {
        resetGlobalFarm()
    }

    @Test
    fun `Farm is created successfully`() {
        assertNotNull(farm, "Farm should be created")
    }

    @Test
    fun `Farm stores and supplies dependencies correctly`() {
        val dependencyMock: SomeDependency = mockk()

        farm.produce { dependencyMock }

        val suppliedDependency: SomeDependency = farm.supply()

        assertNotNull(suppliedDependency, "Dependency should be supplied")
    }

    @Test
    fun `Farm throws when supplying unregistered dependency`() {
        assertFailsWith<NoSuchSeedException>(
            "NoSuchSeedException should be thrown when supplying unregistered dependency"
        ) { farm.supply<GlobalDependency>() }
    }

    @Test
    fun `Farm throws when producing duplicate dependencies`() {
        val dependencyMock: SomeDependency = mockk()

        farm.produce { dependencyMock }
        assertFailsWith<DuplicateProduceException>(
            "DuplicateProduceException should be thrown when producing duplicate dependencies"
        ) { farm.produce { dependencyMock } }
    }

    @Test
    fun `Child farm supplies dependency from Farm when not found locally`() {
        val globalDependency: GlobalDependency = mockk()
        farm.produce { globalDependency }

        val childFarm = Farm("ChildFarm", farm)

        val suppliedDependency: GlobalDependency = childFarm.supply()

        assertNotNull(
            suppliedDependency,
            "Child farm should supply dependency from Farm"
        )
    }

    @Test
    fun `Farm destroys individual dependencies correctly`() {
        val dependencyKey = ProduceKey(SomeDependency::class)
        val dependencyMock: SomeDependency = mockk()

        farm.produce(dependencyKey) { dependencyMock }
        farm.destroy(dependencyKey)

        val exception = assertFailsWith<NoSuchSeedException> {
            farm.supply<SomeDependency>()
        }

        assertNotNull(
            exception,
            "NoSuchSeedException should be thrown after dependency is destroyed"
        )
    }

    @Test
    fun `Farm destroys all dependencies correctly`() {
        farm.produce { mockk<SomeDependency>() }
        farm.produce(tag = "Another") { mockk<AnotherDependency>() }

        farm.destroyAllCrops()

        assertFailsWith<NoSuchSeedException>(
            "NoSuchSeedException should be thrown after all dependencies are destroyed"
        ) { farm.supply<SomeDependency>() }
        assertFailsWith<NoSuchSeedException>(
            "NoSuchSeedException should be thrown after all dependencies are destroyed"
        ) { farm.supply<AnotherDependency>(tag = "Another") }
    }

    @Test
    fun `Farm supplies tagged dependencies correctly`() {
        val someDependency: SomeDependency = mockk()
        val anotherDependency: AnotherDependency = mockk()

        farm.produce(tag = "SomeTag") { someDependency }
        farm.produce(tag = "AnotherTag") { anotherDependency }

        val suppliedSomeDependency: SomeDependency = farm.supply(tag = "SomeTag")
        val suppliedAnotherDependency: AnotherDependency =
            farm.supply(tag = "AnotherTag")

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
        farm.produce { DependencyWithParams(expectedParam) }

        val dependency: DependencyWithParams = farm.supply()

        assertSame(
            expectedParam,
            dependency.param,
            "Dependency parameter should match the expected value"
        )
    }

}

