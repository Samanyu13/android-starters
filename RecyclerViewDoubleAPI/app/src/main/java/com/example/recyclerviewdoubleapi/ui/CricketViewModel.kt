package com.example.recyclerviewdoubleapi.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recyclerviewdoubleapi.data.data.MatchCardData
import com.example.recyclerviewdoubleapi.data.datasource.MockCricketApi
import com.example.recyclerviewdoubleapi.data.repo.CricketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CricketViewModel(private val repository: CricketRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<MatchListUiState>(MatchListUiState.Loading)
    val uiState: StateFlow<MatchListUiState> = _uiState.asStateFlow()

    init {
        Log.i("Samanyu", "viewmodel init")

        loadMatchIds()
    }

    fun loadMatchIds() {
        viewModelScope.launch {
            _uiState.value = MatchListUiState.Loading
            val result = repository.loadMatchIds()

            result.fold(
                onSuccess = { ids ->
                    Log.i("Samanyu", "loadMatchIds - result:success $ids")
                    _uiState.value = MatchListUiState.Success(ids)
                },
                onFailure = { e ->
                    Log.i("Samanyu", "loadMatchIds - result:failure ${e.toString()}")
                    _uiState.value = MatchListUiState.Error(e.message.toString())
                },
            )
        }
    }

    suspend fun fetchMatchInfo(id: String): Result<MatchCardData> {
        Log.i("Samanyu", "fetchMatchInfo - $id")
        return repository.getMatchCardData(id)
    }

    class Factory() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CricketViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CricketViewModel(CricketRepository(MockCricketApi())) as T
            }
            throw IllegalArgumentException("Unknow viewmodel class")
        }
    }
}