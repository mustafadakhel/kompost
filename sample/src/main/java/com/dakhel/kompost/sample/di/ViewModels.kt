package com.dakhel.kompost.sample.di

import com.dakhel.kompost.lifecycle.activity.ApplicationRootActivitiesFarm
import com.dakhel.kompost.lifecycle.viewModel.createViewModelsFarm
import com.dakhel.kompost.produce
import com.dakhel.kompost.sample.presentation.viewModel.MainViewModel
import com.dakhel.kompost.sample.presentation.viewModel.MainViewModelWithSavedStateHandle
import com.dakhel.kompost.supply

fun ApplicationRootActivitiesFarm.viewModels() = createViewModelsFarm {
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
