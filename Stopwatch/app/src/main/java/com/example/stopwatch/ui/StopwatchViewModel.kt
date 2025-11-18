package com.example.stopwatch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stopwatch.service.StopWatchCommander
import com.example.stopwatch.service.StopwatchRepository
import com.example.stopwatch.service.StopwatchUIState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class StopwatchViewModel(
    private val repository: StopwatchRepository,
    private val commander: StopWatchCommander
) : ViewModel() {

    val timerState: StateFlow<StopwatchUIState> = repository.timerState

    val formattedTime = timerState.map { state ->
        val elapsedTime = when (state) {
            StopwatchUIState.Idle -> 0L
            is StopwatchUIState.Paused -> state.elapsedTime
            is StopwatchUIState.Running -> state.elapsedTime
        }
        repository.formatTime(elapsedTime)
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        repository.formatTime(0L)
    )

    fun startTimer() {
        commander.startTimer()
    }

    fun pauseTimer() {
        commander.pauseTimer()
    }

    fun resetTimer() {
        commander.resetTimer()
    }
}