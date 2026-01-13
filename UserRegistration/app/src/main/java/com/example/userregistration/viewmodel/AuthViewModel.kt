package com.example.userregistration.viewmodel

import android.util.Log
import androidx.credentials.Credential
import androidx.lifecycle.ViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val _userState = MutableStateFlow(auth.currentUser)
    val userState: StateFlow<FirebaseUser?> = _userState


    private val _errorState = MutableStateFlow<String?>(null)
    val errorState = _errorState.asStateFlow()

    fun signInWithGoogle(credential: Credential) {
        val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnSuccessListener { result ->
                Log.d("[AUTH]", "signInWithGoogle - Login Success: ${result.user?.email}")
                _userState.value = auth.currentUser
                _errorState.value = null
            }
            .addOnFailureListener { exception ->
                Log.e("[AUTH]", "signInWithGoogle - Sign-in failed", exception)
                _errorState.value = exception.message
            }
    }

    fun registerWithEmail(email: String, psw: String) {
        auth.createUserWithEmailAndPassword(email, psw)
            .addOnSuccessListener { result ->
                Log.d("[AUTH]", "registerWithEmail - Registration Success: ${result.user?.email}")
                _userState.value = auth.currentUser
                _errorState.value = null
            }
            .addOnFailureListener { exception ->
                Log.e("[AUTH]", "registerWithEmail - Registration Failed", exception)
                _errorState.value = exception.message
            }
    }

    fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                _userState.value = auth.currentUser
                _errorState.value = null
                Log.d("[AUTH]", "loginWithEmail - Login Success: ${result.user?.email}")
            }
            .addOnFailureListener { exception ->
                Log.e("[AUTH]", "loginWithEmail -Login Failed", exception)

                val errorMessage = when (exception) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        "Invalid credentials. Please check your email and password."
                    }
                    is FirebaseAuthException -> {
                        "Authentication failed: ${exception.message}"
                    }
                    else -> {
                        "An unexpected error occurred. Please try again."
                    }
                }
                _errorState.value = errorMessage
            }
    }

    fun signOut() {
        auth.signOut()
        _userState.value = null
    }
}