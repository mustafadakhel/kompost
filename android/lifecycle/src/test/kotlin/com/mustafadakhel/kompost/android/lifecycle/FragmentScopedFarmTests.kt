package com.mustafadakhel.kompost.android.lifecycle

import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafadakhel.kompost.core.ProduceKey
import com.mustafadakhel.kompost.android.lifecycle.fragment.ApplicationRootFragmentsFarm
import com.mustafadakhel.kompost.android.lifecycle.fragment.FragmentScopedFarm
import com.mustafadakhel.kompost.android.lifecycle.fragment.createFragmentScopedFarm
import com.mustafadakhel.kompost.android.lifecycle.fragment.fragmentScopedFarmOrNull
import com.mustafadakhel.kompost.android.lifecycle.fragment.getOrCreateFragmentScopedFarm
import com.mustafadakhel.kompost.core.produce
import com.mustafadakhel.kompost.core.resetGlobalFarm
import com.mustafadakhel.kompost.core.supply
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class FragmentScopedFarmTests {

    private val rootFragmentsFarm = mockk<ApplicationRootFragmentsFarm> {
        val fragmentsFarms = hashMapOf<String, FragmentScopedFarm>()
        every {
            produce(
                any<ProduceKey>(),
                any<() -> FragmentScopedFarm>()
            )
        } answers {
            val farm = secondArg<() -> FragmentScopedFarm>().invoke()
            fragmentsFarms[firstArg()] = farm
        }
        every { supply<FragmentScopedFarm>(any()) } answers {
            fragmentsFarms[firstArg()] ?: error("FragmentScopedFarm not found")
        }
        every { produce<SomeDependency>(produce = any()) } just Runs
        every { supply<SomeDependency>() } returns mockk()
        every {
            contains(any<ProduceKey>())
        } answers {
            fragmentsFarms.keys.find {
                it == firstArg<String>()
            } != null
        }
    }

    @Test
    fun `FragmentsFarm is created`() {
        val fragmentScenario = launchFragmentInContainer<Fragment>()
        fragmentScenario
            .moveToState(Lifecycle.State.CREATED)
            .moveToState(Lifecycle.State.STARTED)
            .moveToState(Lifecycle.State.RESUMED)

        val fragmentFarm = fragmentScenario.withFragmentNullable {
            createFragmentScopedFarm(rootFragmentsFarm)
        }
        assertNotNull(
            fragmentFarm,
            "FragmentScopedFarm should be created and linked to the fragment"
        )
    }

    @Test
    fun `Retrieving existing FragmentScopedFarm returns the same instance for the fragment`() {
        val fragmentScenario = launchFragmentInContainer<Fragment>()
        fragmentScenario
            .moveToState(Lifecycle.State.CREATED)
            .moveToState(Lifecycle.State.STARTED)
            .moveToState(Lifecycle.State.RESUMED)

        fragmentScenario.onFragment {
            val creation = it.createFragmentScopedFarm(rootFragmentsFarm)

            val retrieval = it.getOrCreateFragmentScopedFarm(rootFragmentsFarm)
            assertSame(
                creation,
                retrieval,
                "The same FragmentScopedFarm instance should be returned for the fragment"
            )
        }
    }

    @Test
    fun `Accessing FragmentScopedFarm before creation should return null`() {
        val fragmentScenario = launchFragmentInContainer<Fragment>()
        fragmentScenario
            .moveToState(Lifecycle.State.CREATED)
            .moveToState(Lifecycle.State.STARTED)
            .moveToState(Lifecycle.State.RESUMED)

        val fragmentFarm = fragmentScenario.withFragmentNullable {
            fragmentScopedFarmOrNull(rootFragmentsFarm)
        }

        assertNull(
            fragmentFarm,
            "FragmentScopedFarm was returned when it should not have been created yet"
        )
    }

    @Test
    fun `FragmentScopedFarm delegates to parent ApplicationFragmentScopedFarm when dependency not found locally`() {
        val fragmentScenario = launchFragmentInContainer<Fragment>()
        fragmentScenario
            .moveToState(Lifecycle.State.CREATED)
            .moveToState(Lifecycle.State.STARTED)
            .moveToState(Lifecycle.State.RESUMED)

        val fragmentFarm = fragmentScenario.withFragment {
            createFragmentScopedFarm(rootFragmentsFarm)
        }

        fragmentFarm.supply<SomeDependency>()

        verify { rootFragmentsFarm.supply<SomeDependency>() }
    }

    @AfterTest
    fun tearDown() {
        resetGlobalFarm()
    }
}
