package com.example.data.model

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: String, // "ROOM", "REGISTRATION", "MATCH", "RESULT", "BRACKET", "PRIZE", "FAIR_PLAY"
    val timeAgo: String,
    val isRead: Boolean = false,
    val tournamentId: String? = null
)

data class PaymentTransaction(
    val id: String,
    val transactionId: String,
    val userId: String,
    val tournamentId: String,
    val tournamentTitle: String,
    val amount: Int,
    val status: TransactionStatus,
    val paymentMethod: String = "Mobile Banking (Instant)",
    val createdAt: String
)

data class PrizeRecord(
    val id: String,
    val tournamentId: String,
    val tournamentTitle: String,
    val recipientName: String,
    val position: String, // "1st Place (Champion)", "2nd Place", "3rd Place", "MVP Fragger"
    val amount: Int,
    val status: PrizeStatus,
    val date: String
)

data class ModerationAction(
    val id: String,
    val tournamentId: String,
    val tournamentTitle: String,
    val targetName: String,
    val actionType: ModerationType,
    val pointsDeducted: Int = 0,
    val reason: String,
    val adminName: String = "Admin_FXR",
    val date: String
)

data class AdminDashboardStats(
    val totalUsers: Int = 1420,
    val totalTournaments: Int = 18,
    val brTournaments: Int = 8,
    val loneWolfTournaments: Int = 6,
    val customTournaments: Int = 4,
    val liveTournaments: Int = 2,
    val upcomingTournaments: Int = 9,
    val completedTournaments: Int = 7,
    val pendingRegistrations: Int = 3,
    val pendingResults: Int = 2
)
