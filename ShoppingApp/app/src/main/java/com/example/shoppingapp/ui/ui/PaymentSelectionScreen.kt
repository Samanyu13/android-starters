package com.example.shoppingapp.ui.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.payment_sdk.PaymentProvider
import com.example.payment_sdk.PaymentResult
import com.example.payment_sdk.providers.PaypalPaymentProvider
import com.example.payment_sdk.providers.StripePaymentProvider
import com.example.shoppingapp.ui.ShopViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSelectionScreen(
    total: Double,
    viewModel: ShopViewModel = hiltViewModel(),
    onPaymentSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val providers = listOf(StripePaymentProvider(), PaypalPaymentProvider())
    var isProcessing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Order Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Amount", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "$${"%.2f".format(total)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Select Payment Method",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Payment Options List
            providers.forEach { provider ->
                PaymentOptionRow(
                    provider = provider,
                    isEnabled = !isProcessing,
                    onClick = {
                        isProcessing = true

                        CoroutineScope(Dispatchers.Main).launch {
                            provider.processPayment(total) { result ->
                                isProcessing = false
                                when (result) {
                                    is PaymentResult.Success -> {
                                        viewModel.onPaymentSuccess(result, total)
                                        onPaymentSuccess()
                                    }

                                    is PaymentResult.Failed -> {
                                        Toast.makeText(context, result.error, Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                    PaymentResult.Cancelled -> {
                                        Toast.makeText(
                                            context,
                                            "Payment got cancelled for some reason!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isProcessing) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator()
                Text("Connecting to Secure Gateway...", modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
fun PaymentOptionRow(provider: PaymentProvider, isEnabled: Boolean, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(provider.iconEmoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Pay with ${provider.name}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}