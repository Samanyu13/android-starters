package com.example.shoppingapp.ui.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shoppingapp.ui.ShopViewModel

@Composable
fun MainShoppingApp(viewModel: ShopViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val cartCount by viewModel.cartCount.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Tab 1: Products
                NavigationBarItem(
                    selected = currentRoute == "products",
                    onClick = { navController.navigate("products") },
                    icon = { Icon(Icons.Default.Storefront, "Shop") },
                    label = { Text("Shop") })

                // Tab 2: Cart
                NavigationBarItem(
                    selected = currentRoute == "cart",
                    onClick = { navController.navigate("cart") },
                    icon = {
                        BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                            Icon(Icons.Default.ShoppingCart, "Cart")
                        }
                    },
                    label = { Text("Cart") })

                // Tab 3: History
                NavigationBarItem(
                    selected = currentRoute == "history",
                    onClick = { navController.navigate("history") },
                    icon = { Icon(Icons.Default.History, "History") },
                    label = { Text("History") })
            }
        }) { padding ->
        NavHost(
            navController = navController,
            startDestination = "products",
            modifier = Modifier.padding(padding)
        ) {
            composable("products") { ProductListScreen() }
            composable("cart") {
                CartScreen(onProceedForPayment = { amount ->
                    navController.navigate("payment_selection/$amount")
                })
            }
            composable("history") { HistoryScreen() }
            composable("payment_selection/{total}") { backStackEntry ->
                val total = backStackEntry.arguments?.getString("total")?.toDouble() ?: 0.0
                PaymentSelectionScreen(
                    total = total,
                    onPaymentSuccess = { navController.navigate("history") },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}