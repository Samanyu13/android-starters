package com.example.payment_sdk

interface PaymentProvider {
    val name: String
    val iconEmoji: String
    suspend fun processPayment(amount: Double, onResult: (PaymentResult) -> Unit)
}

sealed interface PaymentResult {
    data class Success(val transactionId: String) : PaymentResult
    data class Failed(val error: String) : PaymentResult
    object Cancelled : PaymentResult
}