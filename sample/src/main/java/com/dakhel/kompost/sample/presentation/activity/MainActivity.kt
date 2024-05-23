package com.dakhel.kompost.sample.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.dakhel.kompost.lifecycle.viewModel.lazyViewModel
import com.dakhel.kompost.sample.presentation.viewModel.MainViewModelWithSavedStateHandle

class MainActivity : ComponentActivity() {
    private val viewModelWithSavedStateHandle by lazyViewModel<MainViewModelWithSavedStateHandle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelWithSavedStateHandle.log()
    }
}
