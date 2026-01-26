package com.example.shoppingapp.ui.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.material3.Card
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shoppingapp.data.local.CartItem
import com.example.shoppingapp.ui.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: ShopViewModel = hiltViewModel(),
    onPaymentSuccess: () -> Unit
) {
    val items by viewModel.cartItems.collectAsState()
    val total by viewModel.totalAmount.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart") },
                actions = {
                    if (items.isNotEmpty()) {
                        Box(modifier = Modifier.padding(end = 8.dp)) {
                            FilledTonalIconButton(
                                onClick = { showConfirmDialog = true },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                        alpha = 0.7f
                                    ),
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteSweep,
                                    contentDescription = "Clear All",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (items.isEmpty()) {
                // Empty State UI
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🛒", fontSize = 64.sp)
                        Text("Your cart is empty", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                CartListContent(Modifier.weight(1f), viewModel, items, total, onPaymentSuccess)
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Clear Cart?") },
            text = { Text("Are you sure you want to remove all items from your cart?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearCart()
                    showConfirmDialog = false
                }) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CartListContent(
    modifier: Modifier,
    viewModel: ShopViewModel,
    items: List<CartItem>,
    total: Double,
    onPaymentSuccess: () -> Unit
) {

    var showPaymentSheet by remember { mutableStateOf(false) }
    LazyColumn(modifier = modifier) {
        items(items, key = { it.id }) { item ->
            CartItemRow(
                item = item,
                onIncrement = { viewModel.updateQuantity(item.id, true) },
                onDecrement = { viewModel.updateQuantity(item.id, false) }
            )
        }
    }

    if (items.isNotEmpty()) {
        Button(
            onClick = { showPaymentSheet = true },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Buy Now ($${"%.2f".format(total)})") }
    }

//        if (showPaymentSheet) {
//            // PLUG AND PLAY SDK CALL
//            PaymentSDK.RequestPayment(total, "ORDER_${System.currentTimeMillis()}") { result ->
//                showPaymentSheet = false
//                if (result is PaymentResult.Success) {
//                    viewModel.onPaymentSuccess(result.transactionId, total)
//                    onPaymentSuccess()
//                }
//            }
//        }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.3f
            )
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${item.price} each",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(modifier = Modifier.width(110.dp)) {
                QuantityStepper(
                    quantity = item.quantity,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement
                )
            }
        }
    }
}