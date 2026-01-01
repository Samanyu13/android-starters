package com.example.fragmenthandler.viemodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedViewModel: ViewModel() {
    val _sharedText = MutableStateFlow("")
    val sharedText = _sharedText.asStateFlow()

    fun setSharedText(text: String) {
        _sharedText.value = text
    }
}