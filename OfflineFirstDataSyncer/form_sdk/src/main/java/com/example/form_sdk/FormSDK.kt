package com.example.form_sdk

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.form_sdk.data.UserFormDB
import com.example.form_sdk.data.UserProfile
import com.example.form_sdk.ui.SDKFormScreen
import com.example.form_sdk.worker.SyncWorker
import kotlinx.coroutines.flow.Flow

object FormSDK {

    @Composable
    fun FormScreen(onComplete: () -> Unit) {
        SDKFormScreen(onComplete)
    }

    /**
     * Internal/Public API to trigger the background synchronization.
     * Uses WorkManager to ensure the task survives app kills and reboots.
     */
    fun scheduleSync(context: Context) {
        // Define Constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        // Create the Work Request
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                java.util.concurrent.TimeUnit.MILLISECONDS
            )
            .build()

        // Enqueue the work
        // Using UNIQUE work to prevent multiple workers from running at the same time
        WorkManager.getInstance(context).enqueueUniqueWork(
            "offline_form_sync_job",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    /**
     * Public API to observe all records (Synced and Unsynced).
     */
    fun getFormHistory(context: Context): Flow<List<UserProfile>> {
        return UserFormDB.getDatabase(context).formDao().getAllData()
    }
}