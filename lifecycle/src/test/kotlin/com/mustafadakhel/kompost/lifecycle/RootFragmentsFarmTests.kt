package com.mustafadakhel.kompost.lifecycle

import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafadakhel.kompost.lifecycle.activity.RootActivitiesFarm
import com.mustafadakhel.kompost.lifecycle.fragment.ApplicationRootFragmentsFarm
import com.mustafadakhel.kompost.lifecycle.fragment.createRootFragmentsFarm
import com.mustafadakhel.kompost.lifecycle.fragment.fragmentSupply
import com.mustafadakhel.kompost.lifecycle.fragment.getOrCreateFragmentsFarm
import com.mustafadakhel.kompost.lifecycle.fragment.rootFragmentsFarm
import com.mustafadakhel.kompost.lifecycle.fragment.rootFragmentsFarmOrNull
import com.mustafadakhel.kompost.lifecycle.fragment.rootFragmentsFarmProduceKey
import com.mustafadakhel.kompost.core.produce
import com.mustafadakhel.kompost.core.resetGlobalFarm
import com.mustafadakhel.kompost.core.supply
import io.mockk.Runs
import io.mockk.declaringKotlinFile
import io.mockk.every
import io.mockk.invoke
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RootFragmentsFarmTests {

    private val rootActivitiesFarm = mockk<RootActivitiesFarm>(relaxed = true) {
        var applicationRootFragmentsFarm: ApplicationRootFragmentsFarm? = null
        every {
            produce<ApplicationRootFragmentsFarm>(
                rootFragmentsFarmProduceKey,
                captureLambda()
            )
        } answers {
            val farm = lambda<() -> ApplicationRootFragmentsFarm>().invoke()
            applicationRootFragmentsFarm = farm
        }
        every { supply<ApplicationRootFragmentsFarm>(rootFragmentsFarmProduceKey) } answers {
            applicationRootFragmentsFarm!!
        }
        every { produce<SomeDependency>(produce = any()) } just Runs
        every { supply<SomeDependency>() } returns mockk()
        every { contains(rootFragmentsFarmProduceKey) } answers { applicationRootFragmentsFarm != null }
    }

    @Test
    fun `FragmentsFarm is created`() {
        val fragmentsFarm = rootActivitiesFarm.createRootFragmentsFarm()
        assertNotNull(fragmentsFarm, "FragmentsFarm should be created and linked to the fragment")
    }

    @Test
    fun `Retrieving existing FragmentsFarm returns the same instance for the fragment`() {
        val creation = rootActivitiesFarm.createRootFragmentsFarm()

        val retrieval = rootActivitiesFarm.getOrCreateFragmentsFarm()

        assertSame(
            creation,
            retrieval,
            "The same FragmentsFarm instance should be returned for the fragment"
        )
    }

    @Test
    fun `FragmentsFarm should return different instances for different fragments`() {
        val fragmentScenario = launchFragmentInContainer<Fragment>()
        val fragmentScenario2 = launchFragmentInContainer<Fragment>()

        val rootFarm = rootActivitiesFarm.createRootFragmentsFarm {
            produce<SomeDependency> { mockk() }
        }


        mockkStatic(Fragment::rootFragmentsFarm.declaringKotlinFile.qualifiedName!!)
        every { any<Fragment>().rootFragmentsFarm() } returns rootFarm

        fragmentScenario
            .moveToState(Lifecycle.State.CREATED)
            .moveToState(Lifecycle.State.STARTED)
            .moveToState(Lifecycle.State.RESUMED)

        val someDependency = fragmentScenario.withFragmentNullable {
            fragmentSupply<SomeDependency>()
        }

        fragmentScenario2
            .moveToState(Lifecycle.State.CREATED)
            .moveToState(Lifecycle.State.STARTED)
            .moveToState(Lifecycle.State.RESUMED)

        val newSomeDependency = fragmentScenario2.withFragmentNullable {
            fragmentSupply<SomeDependency>()
        }

        assertNotSame(
            someDependency,
            newSomeDependency,
            "The dependencies should be different instances for different fragments"
        )
    }

    @Test
    fun `Accessing FragmentsFarm before creation should return null`() {
        assertNull(
            rootActivitiesFarm.rootFragmentsFarmOrNull(),
            "FragmentsFarm was returned when it should not have been created yet"
        )
    }

    @Test
    fun `FragmentsFarm delegates to parent RootActivitiesFarm when dependency not found locally`() {
        val fragmentsFarm = rootActivitiesFarm.createRootFragmentsFarm()

        fragmentsFarm.supply<SomeDependency>()

        verify { rootActivitiesFarm.supply<SomeDependency>() }
    }

    @AfterTest
    fun tearDown() {
        resetGlobalFarm()
    }
}
