package com.example.shoppingapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(item: CartItem)

    @Delete()
    suspend fun removeFromCart(item: CartItem)

    @Insert
    suspend fun saveOrder(order: OrderHistory)

    @Query("DELETE from cart_items")
    suspend fun clearCart()

    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItem>>

    @Query("SELECT * FROM order_history ORDER BY timeStamp DESC")
    fun getHistory(): Flow<List<OrderHistory>>
}