package com.dakhel.kompost

import android.app.Application
import com.dakhel.kompost.application.ApplicationFarmAlreadyExistsException
import com.dakhel.kompost.application.applicationFarmProduceKey
import com.dakhel.kompost.application.createApplicationFarm
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
