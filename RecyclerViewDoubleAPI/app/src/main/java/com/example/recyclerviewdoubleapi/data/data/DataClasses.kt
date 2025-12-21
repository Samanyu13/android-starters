package com.example.recyclerviewdoubleapi.data.data

// --- 1. MODEL (Data Layer Entities) ---
/** Represents a unique identifier for a match. */
data class MatchId(val id: String)

/** Represents the detailed data for a single match. */
data class MatchDetail(
    val matchId: String,
    val homeTeamDisplayName: String,
    val awayTeamDisplayName: String,
    val latestScore: String
)

/** Data structure exposed to the View. */
data class MatchCardData(
    val id: String, // Added ID for DiffUtil
    val homeTeam: String,
    val awayTeam: String,
    val score: String
)