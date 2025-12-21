package com.example.recyclerviewdoubleapi.ui

import com.example.recyclerviewdoubleapi.data.data.MatchId

// Define UI State sealed interface to encapsulate all possible states
sealed interface MatchListUiState {
    data object Loading : MatchListUiState
    data class Success(val matchIds: List<MatchId>) : MatchListUiState
    data class Error(val message: String) : MatchListUiState
}
