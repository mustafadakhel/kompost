package com.dakhel.kompost.sample.presentation.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dakhel.kompost.sample.domain.repository.Repository

class MainViewModelWithSavedStateHandle(
    private val repository: Repository,
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
    }

    override fun onCleared() {
        Log.d("KompostDebug", "view model cleared: $this")
        super.onCleared()
    }
}
