package com.example.shoppingapp.ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shoppingapp.ui.ShopViewModel

@Composable
fun HistoryScreen(viewModel: ShopViewModel = hiltViewModel()) {
    val orders by viewModel.orderHistory.collectAsState()

    LazyColumn(Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        item { Text("Order History", style = MaterialTheme.typography.headlineMedium) }
        items(orders) { order ->
            Card(Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Order: ${order.orderId}", style = MaterialTheme.typography.labelSmall)
                    Text("Amount: $${order.totalAmount}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}