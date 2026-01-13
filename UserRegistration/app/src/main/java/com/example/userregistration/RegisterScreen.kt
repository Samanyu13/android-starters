package com.example.userregistration

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.userregistration.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(viewModel: AuthViewModel, onNavToLogin: () -> Unit, onSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var uiError by remember { mutableStateOf<String?>(null) }
    val viewModelError by viewModel.errorState.collectAsStateWithLifecycle()
    // Determine the final error message to display. Prioritize UI errors.
    val finalErrorMessage = uiError ?: viewModelError

    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        Text("Register New User", style = MaterialTheme.typography.headlineLarge)
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })

        // Error message block
        finalErrorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = finalErrorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            scope.launch {
                try {
                    viewModel.registerWithEmail(email, password)
                    uiError = null
                } catch (e: Exception) {
                    Log.e("RegisterScreen", "onButtonClick: - ${e.message}")
                }
            }
        }) { Text("Register") }
        TextButton(onClick = onNavToLogin) { Text("Already have an account? Login") }
    }
}