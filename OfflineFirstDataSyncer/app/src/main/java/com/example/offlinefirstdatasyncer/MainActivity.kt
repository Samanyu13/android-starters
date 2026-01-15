package com.example.offlinefirstdatasyncer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.form_sdk.FormSDK
import com.example.form_sdk.data.UserProfile
import com.example.offlinefirstdatasyncer.ui.theme.OfflineFirstDataSyncerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OfflineFirstDataSyncerTheme {
                val context = LocalContext.current
                var isSdkOpen by remember { mutableStateOf(false) }

                val history by FormSDK.getFormHistory(context)
                    .collectAsStateWithLifecycle(initialValue = emptyList())

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isSdkOpen) {
                        // Show SDK Composable
                        FormSDK.FormScreen(onComplete = {
                            isSdkOpen = false
                        })
                    } else {
                        // Show Main App Content
                        MainContent(
                            history = history,
                            padding = innerPadding,
                            onOpenSDK = { isSdkOpen = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent(
    history: List<UserProfile>,
    padding: PaddingValues,
    onOpenSDK: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Button(
            onClick = onOpenSDK,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open SDK Form")
        }

        Text(
            "Sync History",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn {
            items(history) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(item.name, modifier = Modifier.weight(1f))
                    Text(if (item.isSynced) "✅ Synced" else "☁️ Pending")
                }
            }
        }
    }
}