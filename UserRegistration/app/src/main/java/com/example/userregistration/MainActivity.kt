package com.example.userregistration

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.userregistration.ui.theme.UserRegistrationTheme
import com.example.userregistration.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UserRegistrationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        AppNavigation(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: AuthViewModel) {
    val navController = rememberNavController()

    val userState by viewModel.userState.collectAsState()

    LaunchedEffect(userState) {
        if (userState != null) {
            Log.i("MainActivity", "LaunchedEffect triggered $userState")
            navController.navigate("home") {
                // Clear the entire login flow so back button doesn't return here
                popUpTo("auth_flow") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "auth_graph") {
        // Group Login and Register into a nested graph
        navigation(startDestination = "login", route = "auth_graph") {
            // Login
            composable("login") { entry ->
                // Not using this viewmodel as it is scoped to auth_graph
                /*
                val viewModel: AuthViewModel = viewModel(
                    viewModelStoreOwner = remember(entry) {
                        navController.getBackStackEntry("auth_graph")
                    }
                )
                 */

                LoginScreen(
                    viewModel = viewModel,
                    onNavToRegister = { navController.navigate("register") },
                    onSuccess = {
                        navController.navigate("home") {
                            popUpTo("auth_graph") { inclusive = true }
                        }
                    }
                )
            }

            // Register
            composable("register") { entry ->
                RegisterScreen(viewModel, onNavToLogin = {
                    navController.navigate("login")
                }, onSuccess = {
                    navController.navigate("home") {
                        popUpTo("auth_graph") {
                            inclusive = true
                        }
                    }
                })
            }
        }

        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onLogout = {
                navController.navigate("auth_graph") {
                    popUpTo("home") { inclusive = true }
                }
            })
        }
    }
}