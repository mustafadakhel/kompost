package com.dakhel.kompost.lifecycle

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dakhel.kompost.application.ApplicationFarm
import com.dakhel.kompost.lifecycle.activity.ApplicationRootActivitiesFarm
import com.dakhel.kompost.lifecycle.activity.activitySupply
import com.dakhel.kompost.lifecycle.activity.createRootActivitiesFarm
import com.dakhel.kompost.lifecycle.activity.getOrCreateActivitiesFarm
import com.dakhel.kompost.lifecycle.activity.rootActivitiesFarm
import com.dakhel.kompost.lifecycle.activity.rootActivitiesFarmOrNull
import com.dakhel.kompost.lifecycle.activity.rootActivitiesFarmProduceKey
import com.dakhel.kompost.produce
import com.dakhel.kompost.resetGlobalFarm
import com.dakhel.kompost.supply
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
import org.robolectric.Robolectric.buildActivity
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class RootActivitiesFarmTests {

    private val applicationFarm = mockk<ApplicationFarm>(relaxed = true) {
        var applicationRootActivitiesFarm: ApplicationRootActivitiesFarm? = null
        every {
            produce<ApplicationRootActivitiesFarm>(
                rootActivitiesFarmProduceKey,
                captureLambda()
            )
        } answers {
            val farm = lambda<() -> ApplicationRootActivitiesFarm>().invoke()
            applicationRootActivitiesFarm = farm
        }
        every { supply<ApplicationRootActivitiesFarm>(rootActivitiesFarmProduceKey) } answers {
            applicationRootActivitiesFarm!!
        }
        every { produce<SomeDependency>(produce = any()) } just Runs
        every { supply<SomeDependency>() } returns mockk()
        every { contains(rootActivitiesFarmProduceKey) } answers { applicationRootActivitiesFarm != null }
    }

    @Test
    fun `ActivitiesFarm is created`() {
        val activitiesFarm = applicationFarm.createRootActivitiesFarm()
        assertNotNull(activitiesFarm, "ActivitiesFarm should be created and linked to the activity")
    }

    @Test
    fun `Retrieving existing ActivitiesFarm returns the same instance for the activity`() {
        val creation = applicationFarm.createRootActivitiesFarm()

        val retrieval = applicationFarm.getOrCreateActivitiesFarm()

        assertSame(
            creation,
            retrieval,
            "The same ActivitiesFarm instance should be returned for the activity"
        )
    }

    @Test
    fun `ActivitiesFarm should return different instances for different activities`() {
        val activityController = buildActivity(ComponentActivity::class.java)
        val activityController2 = buildActivity(ComponentActivity::class.java)
        val activity = activityController.get()
        val activity2 = activityController2.get()

        val rootFarm = applicationFarm.createRootActivitiesFarm {
            produce<SomeDependency> { mockk() }
        }

        mockkStatic(ComponentActivity::rootActivitiesFarm.declaringKotlinFile.qualifiedName!!)
        every { any<ComponentActivity>().rootActivitiesFarm() } returns rootFarm

        activityController
            .create()
            .start()
            .resume()

        val someDependency = activity.activitySupply<SomeDependency>()

        activityController2
            .create()
            .start()
            .resume()

        val newSomeDependency =
            activity2.activitySupply<SomeDependency>()

        assertNotSame(
            someDependency,
            newSomeDependency,
            "The dependency should be different for different activities"
        )
    }

    @Test
    fun `Accessing ActivitiesFarm before creation should return null`() {
        assertNull(
            applicationFarm.rootActivitiesFarmOrNull(),
            "ActivitiesFarm was returned when it should not have been created yet"
        )
    }

    @Test
    fun `ActivitiesFarm delegates to parent ApplicationFarm when dependency not found locally`() {
        val activitiesFarm = applicationFarm.createRootActivitiesFarm()

        activitiesFarm.supply<SomeDependency>()

        verify { applicationFarm.supply<SomeDependency>() }
    }

    @AfterTest
    fun tearDown() {
        resetGlobalFarm()
    }
}
