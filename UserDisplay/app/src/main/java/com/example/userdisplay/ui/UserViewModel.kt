package com.example.userdisplay.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userdisplay.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repo: UserRepository = UserRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun fetchRandomUser() {
        if (_uiState.value == UserUiState.Loading) return

        _uiState.value = UserUiState.Loading
        viewModelScope.launch {
            val result = repo.fetchUser()
            result.onSuccess { user ->
                _uiState.value = UserUiState.Success(user)
            }
                .onFailure { error ->
                    _uiState.value = UserUiState.Error(error.message ?: "")
                }
        }

    }
}