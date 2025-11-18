package com.example.stopwatch

import android.app.Application

class StopwatchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyProvider.initApp(this)
    }
}