package com.example.data.model

data class Tournament(
    val id: String,
    val title: String,
    val description: String,
    val matchType: MatchType,
    val teamMode: TeamMode,
    val map: FFMap,
    val roundsCount: Int = 1,
    val entryFee: Int = 0, // 0 = Free
    val prizePool: Int = 1000,
    val prize1st: Int = 500,
    val prize2nd: Int = 300,
    val prize3rd: Int = 150,
    val prizeMvp: Int = 50,
    val registrationDeadline: String,
    val matchDateTime: String,
    val maxSlots: Int = 12,
    val joinedSlots: Int = 0,
    val status: TournamentStatus = TournamentStatus.REGISTRATION_OPEN,
    val rules: String = "1. Mobile devices only (No Emulators/PC).\n2. Gun attributes OFF in Custom & Lone Wolf.\n3. Hacking, teaming, or third-party tools result in immediate permanent ban.\n4. Players must join assigned room slot 10 minutes before match time.\n5. Match results must be submitted with valid screenshot evidence within 15 minutes.",
    val scoringSystemDesc: String = "Placement: 1st=12pts, 2nd=9pts, 3rd=8pts, 4th=7pts, 5th=6pts, 6th=5pts, 7th=4pts, 8th=3pts, 9th=2pts, 10th=1pt. Kill: 1 pt/kill. Penalty: -5 pts for infractions.",
    val placementPoints: Map<Int, Int> = mapOf(
        1 to 12, 2 to 9, 3 to 8, 4 to 7, 5 to 6,
        6 to 5, 7 to 4, 8 to 3, 9 to 2, 10 to 1
    ),
    val killPoints: Int = 1,
    val penaltyPerViolation: Int = 5,
    val isKnockoutBracket: Boolean = false,
    val roomReleased: Boolean = false,
    val roomId: String = "FF-ROOM-88231",
    val roomPassword: String = "FXR#2026",
    val roomInstructions: String = "Enter Free Fire -> Custom -> Search Room ID -> Enter Password. Take your team's designated slot number. Friendly emotes only. Good luck & Booyah!",
    val championName: String? = null,
    val runnerUpName: String? = null,
    val thirdPlaceName: String? = null,
    val mvpName: String? = null
)
