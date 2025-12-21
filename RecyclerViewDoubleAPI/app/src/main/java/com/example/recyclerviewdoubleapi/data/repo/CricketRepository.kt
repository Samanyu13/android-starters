package com.example.recyclerviewdoubleapi.data.repo

import android.util.Log
import com.example.recyclerviewdoubleapi.data.data.MatchCardData
import com.example.recyclerviewdoubleapi.data.data.MatchId
import com.example.recyclerviewdoubleapi.data.datasource.CricketApi

class CricketRepository(private val api: CricketApi) {

    suspend fun loadMatchIds(): Result<List<MatchId>> {
        return try {
            // Direct call to the suspend API function
            val matchIds = api.listMatchesToday()
            Result.success(matchIds)
        } catch (e: Exception) {
            Log.e("CricketRepository", "Failed to load match IDs", e)
            Result.failure(e)
        }
    }

    /**
     * ONLY loads the detailed data for a single match ID. This is called from the ViewHolder.
     */
    suspend fun getMatchCardData(matchId: String): Result<MatchCardData> {
        return try {
            // Direct call to the suspend API function
            val detail = api.getMatchDetail(matchId)

            val card = MatchCardData(
                id = detail.matchId,
                homeTeam = detail.homeTeamDisplayName,
                awayTeam = detail.awayTeamDisplayName,
                score = detail.latestScore
            )

            Result.success(card)
        } catch (e: Exception) {
            // Catches any exception thrown by the API (like a simulated 404 or network error)
            Log.e("CricketRepository", "Failed to load match detail for $matchId", e)
            Result.failure(e)
        }
    }
}