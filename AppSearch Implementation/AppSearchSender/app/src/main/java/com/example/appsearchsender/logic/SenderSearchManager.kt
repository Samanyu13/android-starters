package com.example.appsearchsender.logic

import android.content.Context
import androidx.appsearch.app.PackageIdentifier
import androidx.appsearch.app.PutDocumentsRequest
import androidx.appsearch.app.RemoveByDocumentIdRequest
import androidx.appsearch.app.SetSchemaRequest
import androidx.appsearch.platformstorage.PlatformStorage
import com.example.appsearchsender.data.BaseItem

class SenderSearchManager(private val context: Context) {
    // Add Receiver's package and SHA-256 fingerprint
    private val RECEIVER_PACKAGE = "com.example.appsearchreceiver"
    private val RECEIVER_SHA256 = byteArrayOf(
        0xFF.toByte(), 0x5C.toByte(), 0xA8.toByte(), 0xED.toByte(),
        0xB7.toByte(), 0x00.toByte(), 0xA9.toByte(), 0xCB.toByte(),
        0x8D.toByte(), 0x89.toByte(), 0x69.toByte(), 0x08.toByte(),
        0xA0.toByte(), 0x24.toByte(), 0x81.toByte(), 0xD2.toByte(),
        0xFB.toByte(), 0xFE.toByte(), 0x79.toByte(), 0xA3.toByte(),
        0xCF.toByte(), 0x30.toByte(), 0x2B.toByte(), 0xC6.toByte(),
        0xF0.toByte(), 0xF1.toByte(), 0x1C.toByte(), 0x1F.toByte(),
        0x95.toByte(), 0x23.toByte(), 0x16.toByte(), 0xC0.toByte()
    )
    private suspend fun getSession() = PlatformStorage.createSearchSessionAsync(
        PlatformStorage.SearchContext.Builder(context, "sender_db").build()
    ).get()

    suspend fun publishItem(item: BaseItem) {
        val session = getSession()

        // Grant "Receiver" permission to see this data
        val schemaRequest = SetSchemaRequest.Builder()
            .addDocumentClasses(BaseItem::class.java)
            .setSchemaTypeVisibilityForPackage("BaseItem", true,
                PackageIdentifier(RECEIVER_PACKAGE, RECEIVER_SHA256)
            )
            .build()

        session.setSchemaAsync(schemaRequest).get()
        session.putAsync(PutDocumentsRequest.Builder().addDocuments(item).build()).get()
    }

    suspend fun deleteItem(id: String) {
        getSession().removeAsync(
            RemoveByDocumentIdRequest.Builder("shared_data").addIds(id).build()
        ).get()
    }

    suspend fun deleteAll() {
        // Force override the schema - wipe all data
        getSession().setSchemaAsync(
            SetSchemaRequest.Builder().setForceOverride(true).build()
        ).get()
    }
}