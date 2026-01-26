package com.example.shoppingapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.payment_sdk.PaymentResult
import com.example.shoppingapp.data.local.CartItem
import com.example.shoppingapp.data.local.OrderHistory
import com.example.shoppingapp.data.repo.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val repository: ShopRepository
) : ViewModel() {
    val cartItems: StateFlow<List<CartItem>> = repository.getCartItems().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    val cartCount: StateFlow<Int> = cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )

    val orderHistory: StateFlow<List<OrderHistory>> = repository.getOrderHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalAmount: StateFlow<Double> = cartItems
        .map { items -> items.sumOf { it.price * it.quantity } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0.0
        )


    fun addToCart(id: Int, name: String, price: Double) {
        viewModelScope.launch {
            val newItem = CartItem(
                id = id,
                name = name,
                price = price
            )
            repository.addToCart(newItem)
        }
    }

    fun updateQuantity(id: Int, increase: Boolean) {
        viewModelScope.launch {
            val currentItem = cartItems.value.find { it.id == id }
            currentItem?.let {
                val newQuantity = if (increase) it.quantity + 1 else it.quantity - 1
                if (newQuantity <= 0) {
                    repository.removeFromCart(it)
                } else {
                    repository.addToCart(it.copy(quantity = newQuantity))
                }
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun onPaymentSuccess(paymentResult: PaymentResult.Success, amount: Double) {
        viewModelScope.launch {
            val summary = cartItems.value.joinToString(separator = ", ")
            val historyEntry = OrderHistory(
                totalAmount = amount,
                summary = summary,
                transactionId = paymentResult.transactionId
            )
            repository.saveOrder(historyEntry)
            repository.clearCart()
        }
    }

    fun removeOrder(orderHistory: OrderHistory) {
        viewModelScope.launch {
            repository.removeOrder(orderHistory)
        }
    }

    fun clearOrderHistory() {
        viewModelScope.launch {
            repository.clearOrderHistory()
        }
    }
}