package com.example.data.model

data class UserProfile(
    val id: String = "user_default",
    val username: String = "FXR_Sniper99",
    val email: String = "player@fxrtournament.gg",
    val ffIgn: String = "FXR★VIPER",
    val ffUid: String = "8923410523",
    val region: String = "South Asia (BD/IND)",
    val avatarSeed: String = "viper",
    val matchesPlayed: Int = 24,
    val wins: Int = 7,
    val totalKills: Int = 89,
    val walletBalance: Int = 1250,
    val isAdmin: Boolean = false
)

data class Team(
    val id: String,
    val name: String,
    val tag: String,
    val captainId: String,
    val captainName: String,
    val members: List<TeamMember>,
    val matches: Int = 14,
    val wins: Int = 5,
    val achievements: String = "Champions - Elite Fire Cup S1\nRunner-up - Lone Wolf Masters 2026"
)

data class TeamMember(
    val id: String,
    val userId: String,
    val ign: String,
    val uid: String,
    val role: String = "Member" // Captain, Fragger, Sniper, Support, Sub
)

data class Registration(
    val id: String,
    val tournamentId: String,
    val userId: String,
    val teamName: String,
    val captainIgn: String,
    val captainUid: String,
    val player1Ign: String,
    val player1Uid: String,
    val player2Ign: String? = null,
    val player2Uid: String? = null,
    val player3Ign: String? = null,
    val player3Uid: String? = null,
    val player4Ign: String? = null,
    val player4Uid: String? = null,
    val subIgn: String? = null,
    val subUid: String? = null,
    val status: RegistrationStatus = RegistrationStatus.PENDING,
    val transactionId: String? = null,
    val registeredAt: String = "Today, 14:30"
)
