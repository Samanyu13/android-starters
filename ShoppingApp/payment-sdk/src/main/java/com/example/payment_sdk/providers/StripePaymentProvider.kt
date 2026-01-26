package com.example.payment_sdk.providers

import com.example.payment_sdk.PaymentProvider
import com.example.payment_sdk.PaymentResult

class StripePaymentProvider : PaymentProvider {
    override val name = "Stripe"
    override val iconEmoji = "💳"
    override suspend fun processPayment(
        amount: Double,
        onResult: (PaymentResult) -> Unit
    ) {
        onResult(PaymentResult.Success("STRIPE_TXN_${System.currentTimeMillis()}"))
    }
}