package com.happyminds.fcmtestapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happyminds.fcmtestapp.data.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StoreUiState(
    val fcmToken: String? = null,
    val subscriptionStatus: String = "Waiting...",
    val isPermissionGranted: Boolean = false
)

class StoreViewModel(
    private val repository: NotificationRepository = NotificationRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchToken()
        subscribeToDeals()
    }

    fun updatePermissionStatus(isGranted: Boolean) {
        _uiState.value = _uiState.value.copy(isPermissionGranted = isGranted)
    }

    private fun fetchToken() {
        viewModelScope.launch {
            val token = repository.getFCMToken()
            _uiState.value = _uiState.value.copy(fcmToken = token)
        }
    }

    private fun subscribeToDeals() {
        viewModelScope.launch {
            val success = repository.subscribeToTopic("deals")
            val status = if (success) "Subscribed to deals!" else "Subscription failed"
            _uiState.value = _uiState.value.copy(subscriptionStatus = status)
        }
    }
}
