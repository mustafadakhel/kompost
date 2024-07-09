package com.mustafadakhel.kompost.sample.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.mustafadakhel.kompost.android.lifecycle.viewModel.lazyViewModel
import com.mustafadakhel.kompost.sample.presentation.viewModel.MainViewModelWithSavedStateHandle

class MainActivity : ComponentActivity() {
    private val viewModelWithSavedStateHandle by lazyViewModel<MainViewModelWithSavedStateHandle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModelWithSavedStateHandle.log()
    }

    companion object
}
