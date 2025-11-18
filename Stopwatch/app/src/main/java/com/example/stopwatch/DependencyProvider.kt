package com.example.stopwatch

import android.app.Application
import com.example.stopwatch.service.StopWatchCommander
import com.example.stopwatch.service.StopWatchCommanderImpl
import com.example.stopwatch.service.StopwatchRepository
import com.example.stopwatch.service.StopwatchRepositoryImpl
import kotlin.properties.Delegates

object DependencyProvider {
    private var app: Application by Delegates.notNull()
    val repo: StopwatchRepository = StopwatchRepositoryImpl()
    val stopWatchCommander: StopWatchCommander by lazy {
        StopWatchCommanderImpl(app)
    }

    fun initApp(application: Application) {
        app = application
    }
}