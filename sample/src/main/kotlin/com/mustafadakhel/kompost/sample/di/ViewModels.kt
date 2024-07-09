package com.mustafadakhel.kompost.sample.di

import com.mustafadakhel.kompost.android.lifecycle.activity.ApplicationRootActivitiesFarm
import com.mustafadakhel.kompost.android.lifecycle.viewModel.createViewModelsFarm
import com.mustafadakhel.kompost.core.produce
import com.mustafadakhel.kompost.sample.presentation.viewModel.MainViewModel
import com.mustafadakhel.kompost.sample.presentation.viewModel.MainViewModelWithSavedStateHandle
import com.mustafadakhel.kompost.core.supply

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
