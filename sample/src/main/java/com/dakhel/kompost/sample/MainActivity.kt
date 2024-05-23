package com.dakhel.kompost.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dakhel.kompost.lifecycle.activity.activitySupply
import com.dakhel.kompost.lifecycle.viewModel.lazyViewModel

class MainActivity : ComponentActivity() {
    private val someActivityDependency = activitySupply<SomeActivityDependency>()
    private val viewModel by lazyViewModel<MainViewModel>()
    private val viewModelWithSavedStateHandle by lazyViewModel<MainViewModelWithSavedStateHandle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        someActivityDependency
        viewModel.log()
        viewModelWithSavedStateHandle.log()
    }
}

class SomeSingletonDependency {
    init {
        Log.d("KompostDebug", "singleton $this created")
    }
}

interface Dependency {
    val someSingletonDependency: SomeSingletonDependency
}

class SomeDependency(
    override val someSingletonDependency: SomeSingletonDependency
) : Dependency {
    init {
        Log.d("KompostDebug", "dependency $this created")
        Log.d("KompostDebug", "singleton $someSingletonDependency used")
    }
}

class SomeDependency2(
    override val someSingletonDependency: SomeSingletonDependency
) : Dependency {
    init {
        Log.d("KompostDebug", "dependency $this created")
        Log.d("KompostDebug", "singleton $someSingletonDependency used")
    }
}

class SomeActivityDependency {
    init {
        Log.d("KompostDebug", "dependency $this created")
    }
}


class SomeInstantDependency {
    init {
        Log.d("KompostDebug", "dependency $this created")
    }
}

class MainViewModel(
    private val someDependency: Dependency,
    private val someDependency2: Dependency,
    private val someInstantDependency: SomeInstantDependency
) : ViewModel() {
    init {
        Log.d("KompostDebug", "view model initialized: $this")
    }

    fun log() {
        Log.d("KompostDebug", "logging from: $this")
        Log.d("KompostDebug", "dependency $someDependency used")
        Log.d("KompostDebug", "dependency $someDependency2 used")
        Log.d("KompostDebug", "dependency $someInstantDependency used")
    }

    override fun onCleared() {
        Log.d("KompostDebug", "view model cleared: $this")
        super.onCleared()
    }
}

class MainViewModelWithSavedStateHandle(
    private val someDependency: Dependency,
    private val someDependency2: Dependency,
    private val someInstantDependency: SomeInstantDependency,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    init {
        Log.d("KompostDebug", "view model initialized: $this")
        Log.d("KompostDebug", "saved state: $savedStateHandle")
        savedStateHandle.keys().forEach {
            Log.d("KompostDebug", "saved state key $it")
        }
    }

    fun log() {
        Log.d("KompostDebug", "logging from: $this")
        Log.d("KompostDebug", "dependency $someDependency used")
        Log.d("KompostDebug", "dependency $someDependency2 used")
        Log.d("KompostDebug", "dependency $someInstantDependency used")
    }

    override fun onCleared() {
        Log.d("KompostDebug", "view model cleared: $this")
        super.onCleared()
    }
}
