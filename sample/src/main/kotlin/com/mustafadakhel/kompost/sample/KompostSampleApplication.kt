package com.mustafadakhel.kompost.sample

import android.app.Application
import com.mustafadakhel.kompost.android.application.createApplicationFarm
import com.mustafadakhel.kompost.sample.di.activities
import com.mustafadakhel.kompost.sample.di.dataSources
import com.mustafadakhel.kompost.sample.di.misc
import com.mustafadakhel.kompost.sample.di.repositories

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
