package com.example.shoppingapp.ui.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shoppingapp.data.local.CartItem
import com.example.shoppingapp.ui.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: ShopViewModel = hiltViewModel(),
    onProceedForPayment: (Double) -> Unit
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
                EmptyCartView()
            } else {
                CartListContent(
                    modifier = Modifier.weight(1f),
                    viewModel = viewModel,
                    items = items,
                    total = total,
                    onProceedForPayment = { onProceedForPayment(total) }
                )
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
    onProceedForPayment: (Double) -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    CartItemRow(
                        item = item,
                        onIncrement = { viewModel.updateQuantity(item.id, true) },
                        onDecrement = { viewModel.updateQuantity(item.id, false) }
                    )
                }
            }

            if (items.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = { onProceedForPayment(total) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isProcessing
                    ) {
                        Text(
                            text = "Buy Now ($${"%.2f".format(total)})",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        if (isProcessing) {
            PaymentProcessingOverlay()
        }
    }
}

@Composable
fun EmptyCartView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🛒", fontSize = 64.sp)
            Text("Your cart is empty", style = MaterialTheme.typography.bodyLarge)
        }
    }
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

@Composable
fun PaymentProcessingOverlay() {
    Surface(
        color = Color.Black.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text(
                "Verifying Transaction...",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}