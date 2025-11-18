package com.example.stopwatch.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.stopwatch.MainActivity
import com.example.stopwatch.service.Constants.ACTION_CANCEL
import com.example.stopwatch.service.Constants.ACTION_PAUSE
import com.example.stopwatch.service.Constants.ACTION_RESET
import com.example.stopwatch.service.Constants.ACTION_START
import com.example.stopwatch.service.Constants.NOTIFICATION_CHANNEL_ID
import com.example.stopwatch.service.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.stopwatch.service.Constants.NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class StopwatchService : Service() {

    private var isServiceRunning = false
    private var timerMs = 0L
    private val repo: StopwatchRepository by lazy {
        StopwatchRepositoryImpl()
    }
    private var timerJob: Job? = null

    private val isTimerActive: Boolean
        get() = repo.timerState.value is StopwatchUIState.Running

    override fun onBind(p0: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isServiceRunning) {
            startForeground(NOTIFICATION_ID, createNotification(repo.timerState.value))
            isServiceRunning = true
            //repo.updateTimerState(repo.timerState.value)
        }

        intent?.action?.let { action ->
            when (action) {
                ACTION_START -> startTimer()
                ACTION_PAUSE -> pauseTimer()
                ACTION_RESET, ACTION_CANCEL -> resetTimer()
            }
        }

        return START_STICKY
    }

    private fun createNotification(state: StopwatchUIState): Notification {
        createNotificationChannel()

        val elapsedTime = when (state) {
            StopwatchUIState.Idle -> 0L
            is StopwatchUIState.Paused -> state.elapsedTime
            is StopwatchUIState.Running -> state.elapsedTime
        }
        val isRunning = state is StopwatchUIState.Running

        // Intent to open the main activity when the notification is tapped
        val pendingIntent = PendingIntent.getActivity(
            this, 1, Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Action button (Pause/Resume)
        val actionIntent = Intent(this, StopwatchService::class.java).apply {
            action = if (isRunning) ACTION_PAUSE else ACTION_START
        }
        val actionPendingIntent = PendingIntent.getService(
            this, 1, actionIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val buttonText = if (isRunning) "Pause" else "Resume"
        val icon = if (isRunning) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play

        // Action button (Cancel)
        val cancelIntent = Intent(this, StopwatchService::class.java).apply { action = ACTION_CANCEL }
        val cancelPendingIntent = PendingIntent.getService(
            this, 2, cancelIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Stopwatch")
            .setContentText(repo.formatTime(elapsedTime))
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .addAction(icon, buttonText, actionPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel", cancelPendingIntent)
            .build()

    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun startTimer() {
        if (isTimerActive) return

        val currentState = repo.timerState.value
        timerMs =   when (currentState) {
            is StopwatchUIState.Paused -> currentState.elapsedTime
            else -> timerMs
        }
        repo.updateTimerState(StopwatchUIState.Running(timerMs))

        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(10)
                timerMs += 10
                repo.updateTimerState(StopwatchUIState.Running(timerMs))
                updateNotification()
            }
        }
        updateNotification()
    }

    private fun updateNotification() {
        val notification = createNotification(repo.timerState.value)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        repo.updateTimerState(StopwatchUIState.Paused(timerMs))
        updateNotification()
    }

    private fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        timerMs = 0L
        repo.updateTimerState(StopwatchUIState.Idle)
        updateNotification()
        // Stops the service if - ACTION_CANCEL (from notification) or ACTION_RESET (from UI)
        stopSelf()
    }
}