package com.dakhel.kompost.sample

import android.app.Application
import com.dakhel.kompost.application.ApplicationFarm
import com.dakhel.kompost.application.createApplicationFarm
import com.dakhel.kompost.lifecycle.activity.ApplicationRootActivitiesFarm
import com.dakhel.kompost.lifecycle.activity.createRootActivitiesFarm
import com.dakhel.kompost.lifecycle.viewModel.createViewModelsFarm
import com.dakhel.kompost.produce
import com.dakhel.kompost.supply

class KompostSampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        kompostSampleApplicationFarm()
    }

}

private fun Application.kompostSampleApplicationFarm() = createApplicationFarm {
    activityDependencyInApplicationFarm()
    activitiesScope()
    produceMiscDependencies()
}

private fun ApplicationFarm.activityDependencyInApplicationFarm() {
    produce { SomeActivityDependency() }
}

private fun ApplicationFarm.activitiesScope() = createRootActivitiesFarm {
    produce<Dependency>("SomeDependency") { SomeDependency(supply()) }
    produce<Dependency>("SomeDependency2") { SomeDependency2(supply()) }
    viewModels()
}

private fun ApplicationFarm.produceMiscDependencies() {
    produce(produce = ::SomeSingletonDependency)
    val instantDependency = SomeInstantDependency()
    produce(produce = { instantDependency })
}

private fun ApplicationRootActivitiesFarm.viewModels() = createViewModelsFarm {
    produce {
        MainViewModel(
            supply("SomeDependency"),
            supply("SomeDependency2"),
            supply()
        )
    }
    produceViewModelWithSavedState { savedStateHandle ->
        MainViewModelWithSavedStateHandle(
            supply("SomeDependency"),
            supply("SomeDependency2"),
            supply(),
            savedStateHandle
        )
    }
}
