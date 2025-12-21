package com.example.recyclerviewdoubleapi.data.datasource

import com.example.recyclerviewdoubleapi.data.data.MatchDetail
import com.example.recyclerviewdoubleapi.data.data.MatchId
import com.example.recyclerviewdoubleapi.utils.Constants.TOTAL_MATCH_COUNT
import kotlinx.coroutines.delay

interface CricketApi {
    suspend fun listMatchesToday(): List<MatchId>
    suspend fun getMatchDetail(matchId: String): MatchDetail
}

class MockCricketApi : CricketApi {
    private val teamNames = listOf(
        "India", "Australia", "England", "South Africa", "New Zealand",
        "Pakistan", "Sri Lanka", "Bangladesh", "West Indies", "Afghanistan",
        "Ireland", "Zimbabwe", "Scotland", "Netherlands", "Nepal"
    )

    // Cached list of all 1000 IDs
    private val cachedMatchIds: List<MatchId> by lazy {
        (1..TOTAL_MATCH_COUNT).map { MatchId("M${String.format("%04d", it)}") }
    }

    /**
     * Simulates fetching the list of IDs from the network.
     */
    override suspend fun listMatchesToday(): List<MatchId> {
        delay(500) // Simulate network delay
        return cachedMatchIds
    }

    /**
     * Simulates fetching the detailed data for a single match ID.
     */
    override suspend fun getMatchDetail(matchId: String): MatchDetail {
        // Simulate variable network delay (longer to observe lazy fetching)
        delay(500 + (1..1000).random().toLong())

        // Randomly select two unique teams
        val shuffledTeams = teamNames.shuffled()
        val homeTeam = shuffledTeams[0]
        val awayTeam = shuffledTeams[1]

        // Generate random score components for variety
        val runs = (80..400).random()
        val wickets = (0..9).random()
        val overs = (10..50).random()

        val latestScore = "$runs/$wickets ($overs.${(0..5).random()} ov)"

        return MatchDetail(
            matchId = matchId,
            homeTeamDisplayName = homeTeam,
            awayTeamDisplayName = awayTeam,
            latestScore = latestScore
        )
        // If we wanted to simulate an error, we would throw an exception here.
        // E.g., if (matchId == "M0004") throw HttpException(404)
    }

}