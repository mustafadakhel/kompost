package com.dakhel.kompost.sample

import android.app.Application
import com.dakhel.kompost.application.createApplicationFarm
import com.dakhel.kompost.sample.di.activities
import com.dakhel.kompost.sample.di.dataSources
import com.dakhel.kompost.sample.di.misc
import com.dakhel.kompost.sample.di.repositories

class KompostSampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        kompostSampleApplicationFarm()
    }

}

private fun Application.kompostSampleApplicationFarm() = createApplicationFarm {
    activities()
    dataSources()
    repositories()
    misc()
}
