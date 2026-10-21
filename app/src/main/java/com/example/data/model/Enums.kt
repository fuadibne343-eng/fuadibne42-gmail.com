package com.example.data.model

enum class MatchType(val displayName: String, val badgeColorHex: Long) {
    BR_RANK("BR Rank", 0xFFFF5722),
    LONE_WOLF("Lone Wolf", 0xFF00E5FF),
    CUSTOM("Custom Room", 0xFFFFB300)
}

enum class TeamMode(val displayName: String, val maxPlayers: Int) {
    SOLO("Solo", 1),
    DUO("Duo", 2),
    SQUAD("Squad", 4)
}

enum class TournamentStatus(val label: String, val colorHex: Long) {
    REGISTRATION_OPEN("REGISTRATION OPEN", 0xFF00E676),
    UPCOMING("UPCOMING", 0xFF00E5FF),
    FULL("FULL", 0xFFFF9800),
    LIVE("LIVE", 0xFFFF1744),
    COMPLETED("COMPLETED", 0xFF9E9E9E),
    CANCELLED("CANCELLED", 0xFF757575)
}

enum class FFMap(val mapName: String) {
    BERMUDA("Bermuda"),
    PURGATORY("Purgatory"),
    KALAHARI("Kalahari"),
    ALPINE("Alpine"),
    NEXTERRA("Nexterra")
}

enum class RegistrationStatus {
    PENDING,
    APPROVED,
    REJECTED
}

enum class SubmissionStatus {
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    REJECTED
}

enum class PrizeStatus {
    PENDING,
    PROCESSING,
    PAID
}

enum class TransactionStatus {
    PENDING,
    VERIFIED,
    REJECTED,
    REFUNDED
}

enum class ModerationType(val label: String) {
    WARNING("Warning Issued"),
    PENALTY_POINTS("Points Penalty"),
    DISQUALIFIED("Disqualified"),
    SUSPENDED("Account Suspended")
}

enum class AppScreen {
    HOME,
    TOURNAMENTS,
    MATCHES,
    LEADERBOARDS,
    PROFILE,
    ADMIN_DASHBOARD,
    NOTIFICATIONS,
    TOURNAMENT_DETAIL,
    TEAM_MANAGEMENT
}
