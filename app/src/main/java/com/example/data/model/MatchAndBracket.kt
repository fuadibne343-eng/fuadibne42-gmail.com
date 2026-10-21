package com.example.data.model

data class BRLeaderboardEntry(
    val id: String,
    val tournamentId: String,
    val rank: Int,
    val teamName: String,
    val matchesPlayed: Int = 1,
    val booyahs: Int = 0,
    val placementPoints: Int = 0,
    val eliminations: Int = 0,
    val eliminationPoints: Int = 0,
    val penaltyPoints: Int = 0,
    val totalPoints: Int = 0
)

data class LoneWolfMatch(
    val id: String,
    val tournamentId: String,
    val roundName: String, // "Quarter Final", "Semi Final", "Final"
    val matchIndex: Int,
    val player1Name: String,
    val player1Uid: String,
    val player1Score: Int = 0,
    val player2Name: String,
    val player2Uid: String,
    val player2Score: Int = 0,
    val winnerName: String? = null,
    val isCompleted: Boolean = false
)

data class MatchSession(
    val id: String,
    val tournamentId: String,
    val tournamentTitle: String,
    val matchType: MatchType,
    val roundNumber: Int,
    val map: FFMap,
    val startTime: String,
    val status: TournamentStatus,
    val resultSummary: String? = null
)

data class ResultSubmission(
    val id: String,
    val tournamentId: String,
    val tournamentTitle: String,
    val userId: String,
    val teamName: String,
    val roundNumber: Int,
    val claimedPlacement: Int,
    val claimedEliminations: Int,
    val screenshotNote: String,
    val status: SubmissionStatus = SubmissionStatus.SUBMITTED,
    val reviewNote: String = "",
    val submittedAt: String = "Just now"
)
