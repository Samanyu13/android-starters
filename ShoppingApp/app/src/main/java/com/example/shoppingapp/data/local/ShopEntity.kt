package com.example.shoppingapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val id: Int,
    val name: String,
    val price: Double,
    val quantity: Int = 1
) {
    override fun toString(): String {
        return "$name (x$quantity)"
    }
}

@Entity(tableName = "order_history")
data class OrderHistory(
    @PrimaryKey(autoGenerate = true) val orderId: Int = 0,
    val transactionId: String, // From Payment providers
    val totalAmount: Double,
    val summary: String, // Comma-separated list of items
    val timeStamp: Long = System.currentTimeMillis()
)
