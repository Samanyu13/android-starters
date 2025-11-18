package com.example.stopwatch.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

sealed interface StopwatchUIState {
    data object Idle: StopwatchUIState
    data class Running(val elapsedTime: Long): StopwatchUIState
    data class Paused(val elapsedTime: Long): StopwatchUIState
}

interface StopwatchRepository {
    val timerState: StateFlow<StopwatchUIState>
    fun updateTimerState(newState: StopwatchUIState)
    fun formatTime(ms: Long): String
}

class StopwatchRepositoryImpl: StopwatchRepository {

    private val _timerState = MutableStateFlow<StopwatchUIState>(StopwatchUIState.Idle)
    override val timerState = _timerState.asStateFlow()

    override fun updateTimerState(newState: StopwatchUIState) {
        _timerState.value = newState
    }

    override fun formatTime(ms: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(ms)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
        val millis = (ms % 1000) / 10

        return String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, millis)
    }
}