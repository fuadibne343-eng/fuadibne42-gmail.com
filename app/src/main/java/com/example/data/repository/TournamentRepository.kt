package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class TournamentRepository {

    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "user_me",
            username = "FXR_Shadow",
            email = "shadow@fxresports.com",
            ffIgn = "FXR★SHADOW",
            ffUid = "7749102834",
            region = "South Asia",
            avatarSeed = "shadow",
            matchesPlayed = 32,
            wins = 9,
            totalKills = 142,
            walletBalance = 2400,
            isAdmin = false
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _userTeam = MutableStateFlow(
        Team(
            id = "team_fxr_prime",
            name = "FXR PRIME ESPORTS",
            tag = "FXR",
            captainId = "user_me",
            captainName = "FXR★SHADOW",
            members = listOf(
                TeamMember("m1", "user_me", "FXR★SHADOW", "7749102834", "Captain / IGL"),
                TeamMember("m2", "u2", "FXR★VIPER", "8923410523", "Rusher"),
                TeamMember("m3", "u3", "FXR★GHOST", "4410982312", "Sniper"),
                TeamMember("m4", "u4", "FXR★BLAZE", "5512093847", "Support"),
                TeamMember("m5", "u5", "FXR★STORM", "9934120876", "Substitute")
            ),
            matches = 28,
            wins = 11,
            achievements = "🏆 Booyah Champion - FF Pro League Qualifier\n🥈 2nd Place - Asia Cup Series S2\n🏅 MVP Fragger - Bermuda Warfare 2026"
        )
    )
    val userTeam: StateFlow<Team> = _userTeam.asStateFlow()

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

    private val _registrations = MutableStateFlow<List<Registration>>(emptyList())
    val registrations: StateFlow<List<Registration>> = _registrations.asStateFlow()

    private val _brLeaderboards = MutableStateFlow<Map<String, List<BRLeaderboardEntry>>>(emptyMap())
    val brLeaderboards: StateFlow<Map<String, List<BRLeaderboardEntry>>> = _brLeaderboards.asStateFlow()

    private val _loneWolfMatches = MutableStateFlow<Map<String, List<LoneWolfMatch>>>(emptyMap())
    val loneWolfMatches: StateFlow<Map<String, List<LoneWolfMatch>>> = _loneWolfMatches.asStateFlow()

    private val _matchSessions = MutableStateFlow<List<MatchSession>>(emptyList())
    val matchSessions: StateFlow<List<MatchSession>> = _matchSessions.asStateFlow()

    private val _resultSubmissions = MutableStateFlow<List<ResultSubmission>>(emptyList())
    val resultSubmissions: StateFlow<List<ResultSubmission>> = _resultSubmissions.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _transactions = MutableStateFlow<List<PaymentTransaction>>(emptyList())
    val transactions: StateFlow<List<PaymentTransaction>> = _transactions.asStateFlow()

    private val _prizeRecords = MutableStateFlow<List<PrizeRecord>>(emptyList())
    val prizeRecords: StateFlow<List<PrizeRecord>> = _prizeRecords.asStateFlow()

    private val _moderationActions = MutableStateFlow<List<ModerationAction>>(emptyList())
    val moderationActions: StateFlow<List<ModerationAction>> = _moderationActions.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val t1 = Tournament(
            id = "t_br_squad_1",
            title = "FXR BATTLE ROYALE CHAMPIONSHIP S4",
            description = "The premier Free Fire BR Squad tournament with 48 teams battling across 4 rounds in Bermuda and Purgatory. Official placement and kill points rules apply.",
            matchType = MatchType.BR_RANK,
            teamMode = TeamMode.SQUAD,
            map = FFMap.BERMUDA,
            roundsCount = 4,
            entryFee = 150,
            prizePool = 15000,
            prize1st = 8000,
            prize2nd = 4000,
            prize3rd = 2000,
            prizeMvp = 1000,
            registrationDeadline = "Today, 18:00",
            matchDateTime = "Today, 20:00 GMT+6",
            maxSlots = 12,
            joinedSlots = 11,
            status = TournamentStatus.REGISTRATION_OPEN,
            roomReleased = true,
            roomId = "FF-ROOM-90412",
            roomPassword = "FXR#SQUAD26",
            roomInstructions = "Enter Custom Room 90412. FXR PRIME has Slot 4. All players must use registered IGN & UID. Character skill attributes enabled. Gun attributes OFF."
        )

        val t2 = Tournament(
            id = "t_lw_1v1_1",
            title = "LONE WOLF DUEL MASTERS: 1v1",
            description = "Pure head-to-head aim and reflex tournament. 8 top duelists clash in single-elimination knockout bracket until one champion remains.",
            matchType = MatchType.LONE_WOLF,
            teamMode = TeamMode.SOLO,
            map = FFMap.BERMUDA,
            roundsCount = 3,
            entryFee = 50,
            prizePool = 5000,
            prize1st = 3000,
            prize2nd = 1500,
            prize3rd = 500,
            prizeMvp = 0,
            registrationDeadline = "Closed",
            matchDateTime = "LIVE NOW",
            maxSlots = 8,
            joinedSlots = 8,
            status = TournamentStatus.LIVE,
            isKnockoutBracket = true,
            roomReleased = true,
            roomId = "LW-ARENA-3310",
            roomPassword = "DUEL#991",
            roomInstructions = "Best of 9 rounds in Lone Wolf mode. Respect timer countdown. Screen recording recommended."
        )

        val t3 = Tournament(
            id = "t_custom_sniper",
            title = "SNIPER CLASH: ELITE CUSTOM ROOM",
            description = "Special Custom room format: AWM and M82B only. Unlimited Gloo Walls, high jump, squad combat in Kalahari.",
            matchType = MatchType.CUSTOM,
            teamMode = TeamMode.SQUAD,
            map = FFMap.KALAHARI,
            roundsCount = 2,
            entryFee = 0, // Free
            prizePool = 3000,
            prize1st = 1800,
            prize2nd = 900,
            prize3rd = 300,
            prizeMvp = 0,
            registrationDeadline = "Tomorrow, 16:00",
            matchDateTime = "Tomorrow, 19:30",
            maxSlots = 12,
            joinedSlots = 7,
            status = TournamentStatus.REGISTRATION_OPEN,
            roomReleased = false,
            roomId = "HIDDEN_UNTIL_RELEASE",
            roomPassword = "HIDDEN"
        )

        val t4 = Tournament(
            id = "t_br_duo_alpine",
            title = "ALPINE APEX DUO SHOWDOWN",
            description = "Intense 2v2 tactical survival in high altitude Alpine map. Fast gas, double drop rates.",
            matchType = MatchType.BR_RANK,
            teamMode = TeamMode.DUO,
            map = FFMap.ALPINE,
            roundsCount = 3,
            entryFee = 80,
            prizePool = 8000,
            prize1st = 4500,
            prize2nd = 2500,
            prize3rd = 1000,
            prizeMvp = 0,
            registrationDeadline = "Friday, 17:00",
            matchDateTime = "Friday, 21:00",
            maxSlots = 24,
            joinedSlots = 19,
            status = TournamentStatus.UPCOMING,
            roomReleased = false,
            roomId = "PENDING",
            roomPassword = "PENDING"
        )

        val t5 = Tournament(
            id = "t_completed_grand",
            title = "FREE FIRE MEGA INVITATIONAL 2026",
            description = "Grand finale completed! Witness the championship standings and prize distribution.",
            matchType = MatchType.BR_RANK,
            teamMode = TeamMode.SQUAD,
            map = FFMap.PURGATORY,
            roundsCount = 5,
            entryFee = 200,
            prizePool = 25000,
            prize1st = 14000,
            prize2nd = 7000,
            prize3rd = 3000,
            prizeMvp = 1000,
            registrationDeadline = "Ended",
            matchDateTime = "Completed",
            maxSlots = 12,
            joinedSlots = 12,
            status = TournamentStatus.COMPLETED,
            championName = "FXR PRIME ESPORTS",
            runnerUpName = "ROYAL TITANS",
            thirdPlaceName = "APEX WARRIORS",
            mvpName = "FXR★VIPER"
        )

        _tournaments.value = listOf(t1, t2, t3, t4, t5)

        // Seed Registrations
        val reg1 = Registration(
            id = "reg_1",
            tournamentId = "t_br_squad_1",
            userId = "user_me",
            teamName = "FXR PRIME ESPORTS",
            captainIgn = "FXR★SHADOW",
            captainUid = "7749102834",
            player1Ign = "FXR★SHADOW",
            player1Uid = "7749102834",
            player2Ign = "FXR★VIPER",
            player2Uid = "8923410523",
            player3Ign = "FXR★GHOST",
            player3Uid = "4410982312",
            player4Ign = "FXR★BLAZE",
            player4Uid = "5512093847",
            status = RegistrationStatus.APPROVED,
            transactionId = "TXN-8941092"
        )

        val reg2 = Registration(
            id = "reg_2",
            tournamentId = "t_lw_1v1_1",
            userId = "user_me",
            teamName = "FXR_Shadow",
            captainIgn = "FXR★SHADOW",
            captainUid = "7749102834",
            player1Ign = "FXR★SHADOW",
            player1Uid = "7749102834",
            status = RegistrationStatus.APPROVED,
            transactionId = "TXN-7738192"
        )

        val reg3 = Registration(
            id = "reg_3",
            tournamentId = "t_custom_sniper",
            userId = "user_me",
            teamName = "FXR PRIME ESPORTS",
            captainIgn = "FXR★SHADOW",
            captainUid = "7749102834",
            player1Ign = "FXR★SHADOW",
            player1Uid = "7749102834",
            player2Ign = "FXR★GHOST",
            player2Uid = "4410982312",
            player3Ign = "FXR★VIPER",
            player3Uid = "8923410523",
            player4Ign = "FXR★BLAZE",
            player4Uid = "5512093847",
            status = RegistrationStatus.APPROVED,
            transactionId = "FREE-001"
        )

        _registrations.value = listOf(reg1, reg2, reg3)

        // Seed BR Leaderboards for t1 and t5
        val lbT1 = listOf(
            BRLeaderboardEntry("lb1", "t_br_squad_1", 1, "FXR PRIME ESPORTS", 2, 1, 21, 14, 14, 0, 35),
            BRLeaderboardEntry("lb2", "t_br_squad_1", 2, "ROYAL TITANS", 2, 1, 17, 12, 12, 0, 29),
            BRLeaderboardEntry("lb3", "t_br_squad_1", 3, "APEX WARRIORS", 2, 0, 14, 11, 11, 0, 25),
            BRLeaderboardEntry("lb4", "t_br_squad_1", 4, "CYBER DRAGONS", 2, 0, 12, 9, 9, 0, 21),
            BRLeaderboardEntry("lb5", "t_br_squad_1", 5, "PHANTOM GANG", 2, 0, 10, 8, 8, -5, 13),
            BRLeaderboardEntry("lb6", "t_br_squad_1", 6, "TEAM GODLIKE", 2, 0, 8, 4, 4, 0, 12)
        )

        val lbT5 = listOf(
            BRLeaderboardEntry("lb_c1", "t_completed_grand", 1, "FXR PRIME ESPORTS", 5, 3, 52, 38, 38, 0, 90),
            BRLeaderboardEntry("lb_c2", "t_completed_grand", 2, "ROYAL TITANS", 5, 1, 41, 31, 31, 0, 72),
            BRLeaderboardEntry("lb_c3", "t_completed_grand", 3, "APEX WARRIORS", 5, 1, 36, 27, 27, 0, 63),
            BRLeaderboardEntry("lb_c4", "t_completed_grand", 4, "DEADLY SAMURAI", 5, 0, 29, 22, 22, 0, 51),
            BRLeaderboardEntry("lb_c5", "t_completed_grand", 5, "VENOMOUS KINGS", 5, 0, 22, 19, 19, -5, 36)
        )

        _brLeaderboards.value = mapOf(
            "t_br_squad_1" to lbT1,
            "t_completed_grand" to lbT5
        )

        // Seed Lone Wolf Bracket for t2
        val lwMatches = listOf(
            // Quarterfinals
            LoneWolfMatch("lw_q1", "t_lw_1v1_1", "Quarter Final 1", 1, "FXR★SHADOW", "7749102834", 5, "NEXUS_VIPER", "9012384711", 2, "FXR★SHADOW", true),
            LoneWolfMatch("lw_q2", "t_lw_1v1_1", "Quarter Final 2", 2, "GHOST_RIDER", "3312094821", 5, "SAVAGE_BOY", "8810293412", 4, "GHOST_RIDER", true),
            LoneWolfMatch("lw_q3", "t_lw_1v1_1", "Quarter Final 3", 3, "DARK_LORD_FF", "5512903841", 5, "BULLET_KING", "6623094812", 1, "DARK_LORD_FF", true),
            LoneWolfMatch("lw_q4", "t_lw_1v1_1", "Quarter Final 4", 4, "VENOM_SNIPER", "9921049281", 5, "PRO_HEADHUNTER", "4412093812", 3, "VENOM_SNIPER", true),
            // Semifinals
            LoneWolfMatch("lw_s1", "t_lw_1v1_1", "Semi Final 1", 5, "FXR★SHADOW", "7749102834", 5, "GHOST_RIDER", "3312094821", 3, "FXR★SHADOW", true),
            LoneWolfMatch("lw_s2", "t_lw_1v1_1", "Semi Final 2", 6, "DARK_LORD_FF", "5512903841", 5, "VENOM_SNIPER", "9921049281", 4, "DARK_LORD_FF", true),
            // Grand Final
            LoneWolfMatch("lw_f1", "t_lw_1v1_1", "Grand Final", 7, "FXR★SHADOW", "7749102834", 0, "DARK_LORD_FF", "5512903841", 0, null, false)
        )
        _loneWolfMatches.value = mapOf("t_lw_1v1_1" to lwMatches)

        // Seed Match Sessions
        _matchSessions.value = listOf(
            MatchSession("ms1", "t_lw_1v1_1", "LONE WOLF DUEL MASTERS: 1v1", MatchType.LONE_WOLF, 3, FFMap.BERMUDA, "LIVE NOW", TournamentStatus.LIVE, "Grand Final in progress"),
            MatchSession("ms2", "t_br_squad_1", "FXR BATTLE ROYALE CHAMPIONSHIP S4", MatchType.BR_RANK, 3, FFMap.BERMUDA, "Tonight 20:00", TournamentStatus.REGISTRATION_OPEN, "Round 3 & 4 pending"),
            MatchSession("ms3", "t_custom_sniper", "SNIPER CLASH: ELITE CUSTOM ROOM", MatchType.CUSTOM, 1, FFMap.KALAHARI, "Tomorrow 19:30", TournamentStatus.UPCOMING, "Custom Room ID awaiting release")
        )

        // Seed Result Submissions
        _resultSubmissions.value = listOf(
            ResultSubmission(
                id = "sub_1",
                tournamentId = "t_br_squad_1",
                tournamentTitle = "FXR BATTLE ROYALE CHAMPIONSHIP S4",
                userId = "user_me",
                teamName = "FXR PRIME ESPORTS",
                roundNumber = 2,
                claimedPlacement = 1,
                claimedEliminations = 9,
                screenshotNote = "Booyah screenshot uploaded with 9 team kills. Captain FXR★SHADOW.",
                status = SubmissionStatus.APPROVED,
                reviewNote = "Verified match log & screenshot. Points credited to leaderboard.",
                submittedAt = "15 mins ago"
            ),
            ResultSubmission(
                id = "sub_2",
                tournamentId = "t_br_squad_1",
                tournamentTitle = "FXR BATTLE ROYALE CHAMPIONSHIP S4",
                userId = "u_rt",
                teamName = "ROYAL TITANS",
                roundNumber = 2,
                claimedPlacement = 2,
                claimedEliminations = 7,
                screenshotNote = "Placement #2 match result screenshot attached.",
                status = SubmissionStatus.UNDER_REVIEW,
                reviewNote = "",
                submittedAt = "10 mins ago"
            )
        )

        // Seed Notifications
        _notifications.value = listOf(
            AppNotification("n1", "Room Details Released!", "Room ID and Password are now available for FXR BATTLE ROYALE CHAMPIONSHIP S4.", "ROOM", "5 mins ago", false, "t_br_squad_1"),
            AppNotification("n2", "Lone Wolf Finalist!", "You qualified for the Grand Final vs DARK_LORD_FF! Get ready.", "BRACKET", "30 mins ago", false, "t_lw_1v1_1"),
            AppNotification("n3", "Registration Approved", "Your team FXR PRIME ESPORTS has been confirmed for FXR BATTLE ROYALE CHAMPIONSHIP S4.", "REGISTRATION", "2 hours ago", true, "t_br_squad_1"),
            AppNotification("n4", "Prize Payout Sent", "Your prize of $14,000 for FREE FIRE MEGA INVITATIONAL 2026 has been marked PAID.", "PRIZE", "Yesterday", true, "t_completed_grand")
        )

        // Seed Transactions
        _transactions.value = listOf(
            PaymentTransaction("tx1", "TXN-8941092", "user_me", "t_br_squad_1", "FXR BATTLE ROYALE CHAMPIONSHIP S4", 150, TransactionStatus.VERIFIED, "In-App Wallet", "Today, 14:30"),
            PaymentTransaction("tx2", "TXN-7738192", "user_me", "t_lw_1v1_1", "LONE WOLF DUEL MASTERS: 1v1", 50, TransactionStatus.VERIFIED, "bKash Instant", "Today, 12:15"),
            PaymentTransaction("tx3", "TXN-1002931", "user_me", "t_completed_grand", "FREE FIRE MEGA INVITATIONAL 2026", 200, TransactionStatus.VERIFIED, "Nagad Direct", "Oct 19, 2026")
        )

        // Seed Prize Records
        _prizeRecords.value = listOf(
            PrizeRecord("pr1", "t_completed_grand", "FREE FIRE MEGA INVITATIONAL 2026", "FXR PRIME ESPORTS", "1st Place (Champion)", 14000, PrizeStatus.PAID, "Oct 20, 2026"),
            PrizeRecord("pr2", "t_completed_grand", "FREE FIRE MEGA INVITATIONAL 2026", "FXR★VIPER", "MVP Fragger Award", 1000, PrizeStatus.PAID, "Oct 20, 2026"),
            PrizeRecord("pr3", "t_lw_1v1_1", "LONE WOLF DUEL MASTERS: 1v1", "Winner (TBD)", "1st Place", 3000, PrizeStatus.PENDING, "Pending Final")
        )

        // Seed Moderation Actions
        _moderationActions.value = listOf(
            ModerationAction("mod1", "t_br_squad_1", "FXR BATTLE ROYALE CHAMPIONSHIP S4", "PHANTOM GANG", ModerationType.PENALTY_POINTS, 5, "Late entry to designated room slot (-5 points)", "Admin_FXR", "Today, 15:10"),
            ModerationAction("mod2", "t_completed_grand", "FREE FIRE MEGA INVITATIONAL 2026", "VENOMOUS KINGS", ModerationType.PENALTY_POINTS, 5, "Unsporting emote spam in spectator area", "Admin_FXR", "Oct 19, 2026")
        )
    }

    // User Operations
    fun updateProfile(ffIgn: String, ffUid: String, region: String) {
        _currentUser.value = _currentUser.value.copy(
            ffIgn = ffIgn.trim(),
            ffUid = ffUid.trim(),
            region = region.trim()
        )
    }

    fun toggleAdminMode() {
        _currentUser.value = _currentUser.value.copy(
            isAdmin = !_currentUser.value.isAdmin
        )
    }

    fun updateTeam(teamName: String, tag: String, members: List<TeamMember>) {
        _userTeam.value = _userTeam.value.copy(
            name = teamName,
            tag = tag,
            members = members
        )
    }

    // Tournament Registration
    fun registerForTournament(
        tournamentId: String,
        teamName: String,
        captainIgn: String,
        captainUid: String,
        p1Ign: String,
        p1Uid: String,
        p2Ign: String?,
        p2Uid: String?,
        p3Ign: String?,
        p3Uid: String?,
        p4Ign: String?,
        p4Uid: String?,
        subIgn: String?,
        subUid: String?
    ): Boolean {
        // Prevent duplicate registration
        val existing = _registrations.value.find { it.tournamentId == tournamentId && it.userId == _currentUser.value.id }
        if (existing != null) return false

        val tournament = _tournaments.value.find { it.id == tournamentId } ?: return false
        val newJoined = (tournament.joinedSlots + 1).coerceAtMost(tournament.maxSlots)
        val newStatus = if (newJoined >= tournament.maxSlots) TournamentStatus.FULL else tournament.status

        val newReg = Registration(
            id = "reg_${UUID.randomUUID()}",
            tournamentId = tournamentId,
            userId = _currentUser.value.id,
            teamName = teamName.ifBlank { _currentUser.value.username },
            captainIgn = captainIgn,
            captainUid = captainUid,
            player1Ign = p1Ign,
            player1Uid = p1Uid,
            player2Ign = p2Ign,
            player2Uid = p2Uid,
            player3Ign = p3Ign,
            player3Uid = p3Uid,
            player4Ign = p4Ign,
            player4Uid = p4Uid,
            subIgn = subIgn,
            subUid = subUid,
            status = RegistrationStatus.APPROVED, // auto approved or pending
            transactionId = "TXN-${System.currentTimeMillis() % 1000000}"
        )

        _registrations.value = _registrations.value + newReg
        _tournaments.value = _tournaments.value.map {
            if (it.id == tournamentId) it.copy(joinedSlots = newJoined, status = newStatus) else it
        }

        // Add notification
        val notif = AppNotification(
            id = "notif_${UUID.randomUUID()}",
            title = "Registration Confirmed!",
            message = "You have successfully joined ${tournament.title}.",
            type = "REGISTRATION",
            timeAgo = "Just now",
            isRead = false,
            tournamentId = tournamentId
        )
        _notifications.value = listOf(notif) + _notifications.value

        return true
    }

    // Submit Match Result
    fun submitResult(
        tournamentId: String,
        round: Int,
        placement: Int,
        kills: Int,
        screenshotNote: String
    ) {
        val tournament = _tournaments.value.find { it.id == tournamentId }
        val title = tournament?.title ?: "Tournament Match"
        val submission = ResultSubmission(
            id = "sub_${UUID.randomUUID()}",
            tournamentId = tournamentId,
            tournamentTitle = title,
            userId = _currentUser.value.id,
            teamName = _userTeam.value.name,
            roundNumber = round,
            claimedPlacement = placement,
            claimedEliminations = kills,
            screenshotNote = screenshotNote,
            status = SubmissionStatus.SUBMITTED,
            submittedAt = "Just now"
        )
        _resultSubmissions.value = listOf(submission) + _resultSubmissions.value

        val notif = AppNotification(
            id = "n_${UUID.randomUUID()}",
            title = "Result Submitted",
            message = "Your Round $round result has been submitted and is under Admin review.",
            type = "RESULT",
            timeAgo = "Just now",
            isRead = false,
            tournamentId = tournamentId
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    // Admin Operations
    fun createTournament(tournament: Tournament) {
        _tournaments.value = listOf(tournament) + _tournaments.value

        // If Lone Wolf, generate initial bracket matches
        if (tournament.matchType == MatchType.LONE_WOLF) {
            val bracket = listOf(
                LoneWolfMatch("lw_${tournament.id}_q1", tournament.id, "Quarter Final 1", 1, "Player Alpha", "8823101", 0, "Player Beta", "8823102", 0, null, false),
                LoneWolfMatch("lw_${tournament.id}_q2", tournament.id, "Quarter Final 2", 2, "Player Gamma", "8823103", 0, "Player Delta", "8823104", 0, null, false),
                LoneWolfMatch("lw_${tournament.id}_q3", tournament.id, "Quarter Final 3", 3, "Player Epsilon", "8823105", 0, "Player Zeta", "8823106", 0, null, false),
                LoneWolfMatch("lw_${tournament.id}_q4", tournament.id, "Quarter Final 4", 4, "Player Eta", "8823107", 0, "Player Theta", "8823108", 0, null, false),
                LoneWolfMatch("lw_${tournament.id}_s1", tournament.id, "Semi Final 1", 5, "Winner QF1", "", 0, "Winner QF2", "", 0, null, false),
                LoneWolfMatch("lw_${tournament.id}_s2", tournament.id, "Semi Final 2", 6, "Winner QF3", "", 0, "Winner QF4", "", 0, null, false),
                LoneWolfMatch("lw_${tournament.id}_f1", tournament.id, "Grand Final", 7, "Finalist 1", "", 0, "Finalist 2", "", 0, null, false)
            )
            val currentMap = _loneWolfMatches.value.toMutableMap()
            currentMap[tournament.id] = bracket
            _loneWolfMatches.value = currentMap
        }

        // If BR Rank, initialize empty leaderboard
        if (tournament.matchType == MatchType.BR_RANK) {
            val currentMap = _brLeaderboards.value.toMutableMap()
            currentMap[tournament.id] = emptyList()
            _brLeaderboards.value = currentMap
        }
    }

    fun updateTournamentStatus(tournamentId: String, newStatus: TournamentStatus) {
        _tournaments.value = _tournaments.value.map {
            if (it.id == tournamentId) it.copy(status = newStatus) else it
        }
    }

    fun setRoomCredentials(tournamentId: String, roomId: String, pass: String, instructions: String, releaseNow: Boolean) {
        _tournaments.value = _tournaments.value.map {
            if (it.id == tournamentId) {
                it.copy(
                    roomId = roomId,
                    roomPassword = pass,
                    roomInstructions = instructions,
                    roomReleased = releaseNow
                )
            } else it
        }

        if (releaseNow) {
            val t = _tournaments.value.find { it.id == tournamentId }
            val notif = AppNotification(
                id = "notif_${UUID.randomUUID()}",
                title = "Room Credentials Released!",
                message = "Room ID and Password are now live for ${t?.title ?: "tournament"}. Check Tournament Details to join.",
                type = "ROOM",
                timeAgo = "Just now",
                isRead = false,
                tournamentId = tournamentId
            )
            _notifications.value = listOf(notif) + _notifications.value
        }
    }

    fun approveRegistration(regId: String) {
        _registrations.value = _registrations.value.map {
            if (it.id == regId) it.copy(status = RegistrationStatus.APPROVED) else it
        }
    }

    fun rejectRegistration(regId: String) {
        _registrations.value = _registrations.value.map {
            if (it.id == regId) it.copy(status = RegistrationStatus.REJECTED) else it
        }
    }

    // Lone Wolf Match Result update
    fun updateLoneWolfMatchWinner(
        tournamentId: String,
        matchId: String,
        p1Score: Int,
        p2Score: Int,
        winnerName: String
    ) {
        val matches = _loneWolfMatches.value[tournamentId]?.toMutableList() ?: return
        val index = matches.indexOfFirst { it.id == matchId }
        if (index == -1) return

        val current = matches[index]
        val updated = current.copy(
            player1Score = p1Score,
            player2Score = p2Score,
            winnerName = winnerName,
            isCompleted = true
        )
        matches[index] = updated

        // Advance winner to next round bracket
        if (current.matchIndex == 1) {
            val s1 = matches.find { it.matchIndex == 5 }
            if (s1 != null) {
                val s1Idx = matches.indexOf(s1)
                matches[s1Idx] = s1.copy(player1Name = winnerName)
            }
        } else if (current.matchIndex == 2) {
            val s1 = matches.find { it.matchIndex == 5 }
            if (s1 != null) {
                val s1Idx = matches.indexOf(s1)
                matches[s1Idx] = s1.copy(player2Name = winnerName)
            }
        } else if (current.matchIndex == 3) {
            val s2 = matches.find { it.matchIndex == 6 }
            if (s2 != null) {
                val s2Idx = matches.indexOf(s2)
                matches[s2Idx] = s2.copy(player1Name = winnerName)
            }
        } else if (current.matchIndex == 4) {
            val s2 = matches.find { it.matchIndex == 6 }
            if (s2 != null) {
                val s2Idx = matches.indexOf(s2)
                matches[s2Idx] = s2.copy(player2Name = winnerName)
            }
        } else if (current.matchIndex == 5) {
            val f = matches.find { it.matchIndex == 7 }
            if (f != null) {
                val fIdx = matches.indexOf(f)
                matches[fIdx] = f.copy(player1Name = winnerName)
            }
        } else if (current.matchIndex == 6) {
            val f = matches.find { it.matchIndex == 7 }
            if (f != null) {
                val fIdx = matches.indexOf(f)
                matches[fIdx] = f.copy(player2Name = winnerName)
            }
        } else if (current.matchIndex == 7) {
            // Grand final champion decided!
            _tournaments.value = _tournaments.value.map {
                if (it.id == tournamentId) {
                    val loserName = if (winnerName == current.player1Name) current.player2Name else current.player1Name
                    it.copy(
                        status = TournamentStatus.COMPLETED,
                        championName = winnerName,
                        runnerUpName = loserName
                    )
                } else it
            }
        }

        val map = _loneWolfMatches.value.toMutableMap()
        map[tournamentId] = matches
        _loneWolfMatches.value = map
    }

    // Add or Update BR Leaderboard Entry
    fun addOrUpdateBREntry(
        tournamentId: String,
        teamName: String,
        matchesPlayed: Int,
        booyahs: Int,
        placementPoints: Int,
        eliminations: Int,
        eliminationPoints: Int,
        penaltyPoints: Int
    ) {
        val tournament = _tournaments.value.find { it.id == tournamentId }
        val killPts = if (eliminationPoints > 0) eliminationPoints else eliminations * (tournament?.killPoints ?: 1)
        val total = placementPoints + killPts - penaltyPoints

        val list = _brLeaderboards.value[tournamentId]?.toMutableList() ?: mutableListOf()
        val existingIndex = list.indexOfFirst { it.teamName.equals(teamName, ignoreCase = true) }

        val entry = if (existingIndex >= 0) {
            list[existingIndex].copy(
                matchesPlayed = matchesPlayed,
                booyahs = booyahs,
                placementPoints = placementPoints,
                eliminations = eliminations,
                eliminationPoints = killPts,
                penaltyPoints = penaltyPoints,
                totalPoints = total
            )
        } else {
            BRLeaderboardEntry(
                id = "lb_${UUID.randomUUID()}",
                tournamentId = tournamentId,
                rank = list.size + 1,
                teamName = teamName,
                matchesPlayed = matchesPlayed,
                booyahs = booyahs,
                placementPoints = placementPoints,
                eliminations = eliminations,
                eliminationPoints = killPts,
                penaltyPoints = penaltyPoints,
                totalPoints = total
            )
        }

        if (existingIndex >= 0) {
            list[existingIndex] = entry
        } else {
            list.add(entry)
        }

        // Sort descending by totalPoints, then by booyahs, then by eliminationPoints
        val sorted = list.sortedWith(
            compareByDescending<BRLeaderboardEntry> { it.totalPoints }
                .thenByDescending { it.booyahs }
                .thenByDescending { it.eliminationPoints }
        ).mapIndexed { idx, item ->
            item.copy(rank = idx + 1)
        }

        val map = _brLeaderboards.value.toMutableMap()
        map[tournamentId] = sorted
        _brLeaderboards.value = map
    }

    // Review Player Result Submission
    fun reviewSubmission(submissionId: String, approved: Boolean, adminNote: String) {
        val sub = _resultSubmissions.value.find { it.id == submissionId } ?: return
        val newStatus = if (approved) SubmissionStatus.APPROVED else SubmissionStatus.REJECTED

        _resultSubmissions.value = _resultSubmissions.value.map {
            if (it.id == submissionId) it.copy(status = newStatus, reviewNote = adminNote) else it
        }

        if (approved) {
            // Credit to BR leaderboard
            val tournament = _tournaments.value.find { it.id == sub.tournamentId }
            val placePts = tournament?.placementPoints?.get(sub.claimedPlacement) ?: 0
            val killPts = sub.claimedEliminations * (tournament?.killPoints ?: 1)
            val isBooyah = if (sub.claimedPlacement == 1) 1 else 0

            addOrUpdateBREntry(
                tournamentId = sub.tournamentId,
                teamName = sub.teamName,
                matchesPlayed = 1,
                booyahs = isBooyah,
                placementPoints = placePts,
                eliminations = sub.claimedEliminations,
                eliminationPoints = killPts,
                penaltyPoints = 0
            )
        }
    }

    // Apply Fair Play Moderation
    fun applyModeration(
        tournamentId: String,
        targetName: String,
        actionType: ModerationType,
        penaltyPoints: Int,
        reason: String
    ) {
        val tournament = _tournaments.value.find { it.id == tournamentId }
        val title = tournament?.title ?: "Tournament"
        val mod = ModerationAction(
            id = "mod_${UUID.randomUUID()}",
            tournamentId = tournamentId,
            tournamentTitle = title,
            targetName = targetName,
            actionType = actionType,
            pointsDeducted = penaltyPoints,
            reason = reason,
            adminName = "Admin_FXR",
            date = "Today"
        )
        _moderationActions.value = listOf(mod) + _moderationActions.value

        // If penalty points, deduct from leaderboard
        if (actionType == ModerationType.PENALTY_POINTS && penaltyPoints > 0) {
            val list = _brLeaderboards.value[tournamentId]?.toMutableList()
            val itemIdx = list?.indexOfFirst { it.teamName.equals(targetName, ignoreCase = true) }
            if (list != null && itemIdx != null && itemIdx >= 0) {
                val current = list[itemIdx]
                val updatedPenalty = current.penaltyPoints + penaltyPoints
                addOrUpdateBREntry(
                    tournamentId = tournamentId,
                    teamName = current.teamName,
                    matchesPlayed = current.matchesPlayed,
                    booyahs = current.booyahs,
                    placementPoints = current.placementPoints,
                    eliminations = current.eliminations,
                    eliminationPoints = current.eliminationPoints,
                    penaltyPoints = updatedPenalty
                )
            }
        }
    }

    // Mark notification read
    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }
}
