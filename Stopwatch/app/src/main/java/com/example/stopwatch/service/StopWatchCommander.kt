package com.example.stopwatch.service

import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.stopwatch.service.Constants.ACTION_PAUSE
import com.example.stopwatch.service.Constants.ACTION_RESET
import com.example.stopwatch.service.Constants.ACTION_START

interface StopWatchCommander {
    fun startTimer()
    fun pauseTimer()
    fun resetTimer()
}

class StopWatchCommanderImpl(
    private val app: Application
) : StopWatchCommander {
    override fun startTimer() {
        sendCommandToService(ACTION_START)
    }

    override fun pauseTimer() {
        sendCommandToService(ACTION_PAUSE)
    }

    override fun resetTimer() {
        sendCommandToService(ACTION_RESET)
    }

    private fun sendCommandToService(action: String) {
        val intent = Intent(
            app.applicationContext,
            StopwatchService::class.java
        ).apply {
            this.action = action
        }
        ContextCompat.startForegroundService(app.applicationContext, intent)
    }

}