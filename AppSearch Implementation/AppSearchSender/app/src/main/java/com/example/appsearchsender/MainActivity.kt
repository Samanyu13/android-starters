package com.example.appsearchsender

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appsearchsender.data.BaseItem
import com.example.appsearchsender.logic.SenderSearchManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = SenderSearchManager(applicationContext)

        setContent {
            val scope = rememberCoroutineScope()
            val dataList = remember { listOf(
                BaseItem(id = "1", title = "Search Item A", type = "Type1"),
                BaseItem(id = "2", title = "Search Item B", type = "Type2")
            )}

            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = { dataList.forEach { scope.launch { manager.publishItem(it) } } }) {
                        Text("Publish All")
                    }
                    Button(onClick = { scope.launch { manager.deleteAll() } }) {
                        Text("Delete All")
                    }
                }

                LazyColumn {
                    items(dataList) { item ->
                        Card(Modifier.fillMaxWidth().padding(8.dp)) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(item.title, Modifier.weight(1f))
                                Button(onClick = { scope.launch { manager.publishItem(item) } }) { Text("Pub") }
                                Spacer(Modifier.width(8.dp))
                                Button(onClick = { scope.launch { manager.deleteItem(item.id) } }) { Text("Del") }
                            }
                        }
                    }
                }
            }
        }
    }
}