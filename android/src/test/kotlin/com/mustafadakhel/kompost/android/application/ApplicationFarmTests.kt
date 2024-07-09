package com.mustafadakhel.kompost.android.application

import android.app.Application
import com.mustafadakhel.kompost.android.application.ApplicationFarmAlreadyExistsException
import com.mustafadakhel.kompost.android.application.applicationFarmProduceKey
import com.mustafadakhel.kompost.android.application.createApplicationFarm
import com.mustafadakhel.kompost.core.globalFarm
import com.mustafadakhel.kompost.core.resetGlobalFarm
import io.mockk.mockk
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApplicationFarmTests {

    private val application: Application = mockk(relaxed = true)

    @BeforeTest
    fun setup() {
        resetGlobalFarm()
    }

    @Test
    fun `ApplicationFarm is created and added to global farm successfully`() {
        val appFarm = application.createApplicationFarm()
        assertNotNull(appFarm, "ApplicationFarm should be created")
        assertTrue(
            globalFarm().contains(application.applicationFarmProduceKey),
            "Global farm should contain the ApplicationFarm"
        )
    }

    @Test
    fun `Creating ApplicationFarm when non-existent initializes successfully`() {
        val appFarm = application.createApplicationFarm()
        assertNotNull(appFarm, "ApplicationFarm should be initialized when non-existent")
    }

    @Test
    fun `Attempting to create ApplicationFarm again throws`() {
        application.createApplicationFarm()

        assertFailsWith<ApplicationFarmAlreadyExistsException>(
            "Creating ApplicationFarm when it already exists should throw"
        ) {
            application.createApplicationFarm()
        }
    }

}
