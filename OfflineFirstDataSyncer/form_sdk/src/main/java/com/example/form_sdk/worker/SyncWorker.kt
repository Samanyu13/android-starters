package com.example.form_sdk.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.form_sdk.data.UserFormDB
import kotlinx.coroutines.delay

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val database = UserFormDB.Companion.getDatabase(applicationContext)
        val dao = database.formDao()
        val pendingItems = dao.getUnsyncedData()

        if (pendingItems.isEmpty()) return Result.success()

        return try {
            pendingItems.forEach { item ->
                // Simulation of uploading data to server
                delay(3000)

                dao.updateSyncStatus(item.id, true)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("[SyncWorker]", "Error in syncing: ${e.message}", e)
            Result.retry()
        }
    }
}