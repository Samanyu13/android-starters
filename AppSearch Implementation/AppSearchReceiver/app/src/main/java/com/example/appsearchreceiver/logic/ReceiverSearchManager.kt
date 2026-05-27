package com.example.appsearchreceiver.logic

import android.annotation.SuppressLint
import android.content.Context
import androidx.appsearch.app.SearchSpec
import androidx.appsearch.observer.DocumentChangeInfo
import androidx.appsearch.observer.ObserverCallback
import androidx.appsearch.observer.ObserverSpec
import androidx.appsearch.observer.SchemaChangeInfo
import androidx.appsearch.platformstorage.PlatformStorage
import com.example.appsearchreceiver.data.BaseItem

class ReceiverSearchManager(private val context: Context) {

    // Get the Global Session
    private suspend fun getGlobalSession() = PlatformStorage.createGlobalSearchSessionAsync(
        PlatformStorage.GlobalSearchContext.Builder(context).build()
    ).get()

    suspend fun querySharedData(): List<BaseItem> {
        val session = getGlobalSession()
        val searchSpec = SearchSpec.Builder()
            .addFilterPackageNames("com.example.appsearchsender")
            .build()

        val results = session.search("", searchSpec).nextPageAsync.get()
        return results.map { it.genericDocument.toDocumentClass(BaseItem::class.java) }
    }

    // NEW: Register an observer that triggers whenever the Sender app changes data
    @SuppressLint("RequiresFeature")
    suspend fun observeChanges(onChanged: () -> Unit) {
        val session = getGlobalSession()

        val observerSpec = ObserverSpec.Builder()
            .addFilterSchemas("BaseItem") // Only watch this specific document type
            .build()

        session.registerObserverCallback(
            "com.example.appsearchsender",
            observerSpec,
            context.mainExecutor,
            object : ObserverCallback {
                override fun onSchemaChanged(changeInfo: SchemaChangeInfo) {
                    onChanged()
                }

                override fun onDocumentChanged(changeInfo: DocumentChangeInfo) {
                    onChanged()
                }
            }
        )
    }
}