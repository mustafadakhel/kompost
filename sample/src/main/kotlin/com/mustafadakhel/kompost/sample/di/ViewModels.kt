package com.mustafadakhel.kompost.sample.di

import com.mustafadakhel.kompost.core.produce
import com.mustafadakhel.kompost.core.supply
import com.mustafadakhel.kompost.lifecycle.activity.RootActivitiesFarm
import com.mustafadakhel.kompost.lifecycle.viewModel.createViewModelsFarm
import com.mustafadakhel.kompost.sample.presentation.viewModel.MainViewModel
import com.mustafadakhel.kompost.sample.presentation.viewModel.MainViewModelWithSavedStateHandle

fun RootActivitiesFarm.viewModels() = createViewModelsFarm {
    produce {
        MainViewModel(supply())
    }
    produceViewModelWithSavedState { savedStateHandle ->
        MainViewModelWithSavedStateHandle(
            supply(),
            savedStateHandle
        )
    }
}
