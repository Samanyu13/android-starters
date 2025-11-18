package com.example.stopwatch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stopwatch.DependencyProvider

class StopwatchViewModelFactory : ViewModelProvider.Factory {
    private val injector = DependencyProvider

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StopwatchViewModel::class.java)) {
            return StopwatchViewModel(
                repository = injector.repo,
                commander = injector.stopWatchCommander
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class :(")
    }
}