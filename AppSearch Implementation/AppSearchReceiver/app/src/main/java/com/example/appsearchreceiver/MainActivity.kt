package com.example.appsearchreceiver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appsearchreceiver.data.BaseItem
import com.example.appsearchreceiver.logic.ReceiverSearchManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = ReceiverSearchManager(applicationContext)

        setContent {
            var items by remember { mutableStateOf<List<BaseItem>>(emptyList()) }
            val scope = rememberCoroutineScope()

            // Helper function to pull the latest data
            fun refresh() {
                scope.launch { items = manager.querySharedData() }
            }

            // NEW: Launch the observer when the Activity starts
            LaunchedEffect(Unit) {
                refresh() // Initial fetch
                manager.observeChanges {
                    refresh() // Re-fetch data automatically when notified
                }
            }

            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Text("Live Search Results", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))

                LazyColumn {
                    items(items) { item ->
                        Card(
                            Modifier.fillMaxWidth().padding(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Cyan)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(item.title, fontWeight = FontWeight.Bold)
                                Text("ID: ${item.id} | Type: ${item.type}")
                            }
                        }
                    }
                }
            }
        }
    }
}