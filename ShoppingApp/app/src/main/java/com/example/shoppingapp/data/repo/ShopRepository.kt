package com.example.shoppingapp.data.repo

import com.example.shoppingapp.data.local.CartItem
import com.example.shoppingapp.data.local.OrderHistory
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    fun getCartItems(): Flow<List<CartItem>>
    fun getOrderHistory(): Flow<List<OrderHistory>>
    suspend fun addToCart(item: CartItem)
    suspend fun removeFromCart(item: CartItem)
    suspend fun clearCart()
    suspend fun saveOrder(order: OrderHistory)
    suspend fun removeOrder(order: OrderHistory)
    suspend fun clearOrderHistory()
}