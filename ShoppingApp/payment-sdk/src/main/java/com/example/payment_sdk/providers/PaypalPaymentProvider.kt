package com.example.payment_sdk.providers

import com.example.payment_sdk.PaymentProvider
import com.example.payment_sdk.PaymentResult
import kotlinx.coroutines.delay

class PaypalPaymentProvider : PaymentProvider {
    override val name = "PayPal"
    override val iconEmoji = "🅿️"

    override suspend fun processPayment(amount: Double, onResult: (PaymentResult) -> Unit) {
        delay(2000) // Simulating network handshake

        val isSuccess = true

        if (isSuccess) {
            onResult(PaymentResult.Success("PAYPAL-ID-${System.currentTimeMillis()}"))
        } else {
            onResult(PaymentResult.Failed("User account balance insufficient"))
        }
    }
}