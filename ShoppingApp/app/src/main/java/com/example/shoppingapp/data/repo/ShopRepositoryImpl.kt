package com.example.shoppingapp.data.repo

import com.example.shoppingapp.data.local.CartItem
import com.example.shoppingapp.data.local.OrderHistory
import com.example.shoppingapp.data.local.ShopDao

class ShopRepositoryImpl(private val dao: ShopDao) : ShopRepository {
    override fun getCartItems() = dao.getCartItems()

    override fun getOrderHistory() = dao.getHistory()

    override suspend fun addToCart(item: CartItem) {
        dao.addToCart(item)
    }

    override suspend fun removeFromCart(item: CartItem) {
        dao.removeFromCart(item)
    }

    override suspend fun clearCart() {
        dao.clearCart()
    }

    override suspend fun saveOrder(order: OrderHistory) {
        dao.saveOrder(order)
    }

    override suspend fun removeOrder(order: OrderHistory) {
        dao.removeOrder(order)
    }

    override suspend fun clearOrderHistory() {
        dao.clearOrderHistory()
    }
}