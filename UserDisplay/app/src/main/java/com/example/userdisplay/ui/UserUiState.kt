package com.example.userdisplay.ui

import com.example.userdisplay.data.UserData

sealed interface UserUiState {
    data class Success(val user: UserData): UserUiState
    data class Error(val message: String): UserUiState
    data object Initial: UserUiState
    data object Loading: UserUiState
}