package com.mustafadakhel.kompost.android.lifecycle

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafadakhel.kompost.core.ProduceKey
import com.mustafadakhel.kompost.android.lifecycle.activity.ActivityScopedFarm
import com.mustafadakhel.kompost.android.lifecycle.activity.ApplicationRootActivitiesFarm
import com.mustafadakhel.kompost.android.lifecycle.activity.activityScopedFarmOrNull
import com.mustafadakhel.kompost.android.lifecycle.activity.createActivityScopedFarm
import com.mustafadakhel.kompost.android.lifecycle.activity.getOrCreateActivityScopedFarm
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
import org.robolectric.Robolectric.buildActivity
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ActivityScopedFarmTests {

    private val rootActivitiesFarm = mockk<ApplicationRootActivitiesFarm> {
        val activitiesFarms = hashMapOf<String, ActivityScopedFarm>()
        every {
            produce(
                any<ProduceKey>(),
                any<() -> ActivityScopedFarm>()
            )
        } answers {
            val farm = secondArg<() -> ActivityScopedFarm>().invoke()
            activitiesFarms[firstArg()] = farm
        }
        every { supply<ActivityScopedFarm>(any()) } answers {
            activitiesFarms[firstArg()] ?: error("ActivityScopedFarm not found")
        }
        every { produce<SomeDependency>(produce = any()) } just Runs
        every { supply<SomeDependency>() } returns mockk()
        every {
            contains(any<ProduceKey>())
        } answers {
            activitiesFarms.keys.find {
                it == firstArg<String>()
            } != null
        }
    }

    @Test
    fun `ActivitiesFarm is created`() {
        val activityController = buildActivity(ComponentActivity::class.java)
        activityController
            .create()
            .start()
            .resume()

        val activityFarm = activityController.get().createActivityScopedFarm(rootActivitiesFarm)
        assertNotNull(
            activityFarm,
            "ActivityScopedFarm should be created and linked to the activity"
        )
    }

    @Test
    fun `Retrieving existing ActivityScopedFarm returns the same instance for the activity`() {
        val activityController = buildActivity(ComponentActivity::class.java)
        activityController
            .create()
            .start()
            .resume()

        activityController.get().let {
            val creation = it.createActivityScopedFarm(rootActivitiesFarm)

            val retrieval = it.getOrCreateActivityScopedFarm(rootActivitiesFarm)
            assertSame(
                creation,
                retrieval,
                "The same ActivityScopedFarm instance should be returned for the activity"
            )
        }
    }

    @Test
    fun `Accessing ActivityScopedFarm before creation should return null`() {
        val activityController = buildActivity(ComponentActivity::class.java)
        activityController
            .create()
            .start()
            .resume()

        val activityFarm = activityController.get().activityScopedFarmOrNull(rootActivitiesFarm)

        assertNull(
            activityFarm,
            "ActivityScopedFarm was returned when it should not have been created yet"
        )
    }

    @Test
    fun `ActivityScopedFarm delegates to parent ApplicationActivityScopedFarm when dependency not found locally`() {
        val activityController = buildActivity(ComponentActivity::class.java)
        activityController
            .create()
            .start()
            .resume()

        val activityFarm = activityController.get().createActivityScopedFarm(rootActivitiesFarm)

        activityFarm.supply<SomeDependency>()

        verify { rootActivitiesFarm.supply<SomeDependency>() }
    }

    @AfterTest
    fun tearDown() {
        resetGlobalFarm()
    }
}
