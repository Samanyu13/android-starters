package com.example.userregistration

import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.util.Patterns
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.userregistration.viewmodel.AuthViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(viewModel: AuthViewModel, onNavToRegister: () -> Unit, onSuccess: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var uiError by remember { mutableStateOf<String?>(null) }
    val viewModelError by viewModel.errorState.collectAsStateWithLifecycle()
    // Determine the final error message to display. Prioritize UI errors.
    val finalErrorMessage = uiError ?: viewModelError
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        TextField(value = email, onValueChange = {
            email = it
            uiError = null
        }, label = { Text("Email") })
        TextField(value = password, onValueChange = {
            password = it
            uiError = null
        }, label = { Text("Password") })

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

        // Login Button
        Button(onClick = {
            val trimmedEmail = email.trim()
            when {
                trimmedEmail.isEmpty() -> {
                    uiError = "Email cannot be empty."
                }

                password.isEmpty() -> {
                    uiError = "Password cannot be empty."
                }

                !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                    uiError = "Please enter a valid email address."
                }

                else -> {
                    scope.launch {
                        try {
                            viewModel.loginWithEmail(trimmedEmail, password)
                            uiError = null
                        } catch (e: Exception) {
                            Log.e("LoginScreen", "Login: - ${e.message}")
                        }
                    }
                }
            }
        }) { Text("Login") }

        // Google Login Button
        Button(onClick = {
            scope.launch {
                val cm = CredentialManager.create(context)
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(
                        GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
                            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                            .build()
                    )
                    .build()
                try {
                    val result = cm.getCredential(context, request)
                    viewModel.signInWithGoogle(result.credential)
                } catch (e: NoCredentialException) {
                    // "Not signed in to Google" case
                    Log.e("LoginScreen", "Google Login: NoCredentialException - ${e.message}")
                    val intent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                        putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                    }
                    context.startActivity(intent)
                }
            }
        }) { Text("Google Login") }

        TextButton(onClick = onNavToRegister) { Text("Create an account") }
    }
}