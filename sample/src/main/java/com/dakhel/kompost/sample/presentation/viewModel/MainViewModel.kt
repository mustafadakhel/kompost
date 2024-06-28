package com.dakhel.kompost.sample.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.dakhel.kompost.sample.domain.repository.Repository

class MainViewModel(
    private val repository: Repository,
) : ViewModel() {
    init {
        Log.d("KompostDebug", "view model initialized: $this")
    }

    fun log() {
        Log.d("KompostDebug", "logging from: $this")
    }

    override fun onCleared() {
        Log.d("KompostDebug", "view model cleared: $this")
        super.onCleared()
    }
}
