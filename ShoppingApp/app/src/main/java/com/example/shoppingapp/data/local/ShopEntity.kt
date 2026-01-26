package com.example.shoppingapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val id: Int,
    val name: String,
    val price: Double,
    val quantity: Int = 1
)

@Entity(tableName = "order_history")
data class OrderHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: String,
    val totalAmount: Double,
    val timeStamp: Long = System.currentTimeMillis()
)
