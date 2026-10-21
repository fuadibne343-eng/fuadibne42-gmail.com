package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.TournamentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class TournamentViewModel(
    val repository: TournamentRepository = TournamentRepository()
) : ViewModel() {

    val currentUser = repository.currentUser
    val userTeam = repository.userTeam
    val tournaments = repository.tournaments
    val registrations = repository.registrations
    val brLeaderboards = repository.brLeaderboards
    val loneWolfMatches = repository.loneWolfMatches
    val matchSessions = repository.matchSessions
    val resultSubmissions = repository.resultSubmissions
    val notifications = repository.notifications
    val transactions = repository.transactions
    val prizeRecords = repository.prizeRecords
    val moderationActions = repository.moderationActions

    // Screen Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedTournamentId = MutableStateFlow<String?>("t_br_squad_1")
    val selectedTournamentId: StateFlow<String?> = _selectedTournamentId.asStateFlow()

    // Filters
    val searchQuery = MutableStateFlow("")
    val filterMatchType = MutableStateFlow<MatchType?>(null)
    val filterTeamMode = MutableStateFlow<TeamMode?>(null)
    val filterStatus = MutableStateFlow<TournamentStatus?>(null)
    val filterFeeType = MutableStateFlow("ALL") // "ALL", "FREE", "PAID"

    // UI Feedback Banner / Message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    fun navigateTo(screen: AppScreen, tournamentId: String? = null) {
        if (tournamentId != null) {
            _selectedTournamentId.value = tournamentId
        }
        _currentScreen.value = screen
    }

    val selectedTournament: StateFlow<Tournament?> = combine(tournaments, selectedTournamentId) { list, id ->
        list.find { it.id == id } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val unreadNotificationCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    data class FilterCriteria(
        val query: String,
        val matchType: MatchType?,
        val teamMode: TeamMode?,
        val status: TournamentStatus?,
        val feeType: String
    )

    private val filterCriteria = combine(
        searchQuery,
        filterMatchType,
        filterTeamMode,
        filterStatus,
        filterFeeType
    ) { query, matchType, teamMode, status, feeType ->
        FilterCriteria(query, matchType, teamMode, status, feeType)
    }

    // Filtered tournaments
    val filteredTournaments: StateFlow<List<Tournament>> = combine(
        tournaments,
        filterCriteria
    ) { list, criteria ->
        list.filter { t ->
            val matchesQuery = criteria.query.isBlank() || t.title.contains(criteria.query, ignoreCase = true) || t.map.mapName.contains(criteria.query, ignoreCase = true)
            val matchesType = criteria.matchType == null || t.matchType == criteria.matchType
            val matchesMode = criteria.teamMode == null || t.teamMode == criteria.teamMode
            val matchesStatus = criteria.status == null || t.status == criteria.status
            val matchesFee = when (criteria.feeType) {
                "FREE" -> t.entryFee == 0
                "PAID" -> t.entryFee > 0
                else -> true
            }
            matchesQuery && matchesType && matchesMode && matchesStatus && matchesFee
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Stats
    val adminStats: StateFlow<AdminDashboardStats> = combine(
        tournaments,
        registrations,
        resultSubmissions
    ) { tList, regList, subList ->
        AdminDashboardStats(
            totalUsers = 1420 + regList.size,
            totalTournaments = tList.size,
            brTournaments = tList.count { it.matchType == MatchType.BR_RANK },
            loneWolfTournaments = tList.count { it.matchType == MatchType.LONE_WOLF },
            customTournaments = tList.count { it.matchType == MatchType.CUSTOM },
            liveTournaments = tList.count { it.status == TournamentStatus.LIVE },
            upcomingTournaments = tList.count { it.status == TournamentStatus.UPCOMING || it.status == TournamentStatus.REGISTRATION_OPEN },
            completedTournaments = tList.count { it.status == TournamentStatus.COMPLETED },
            pendingRegistrations = regList.count { it.status == RegistrationStatus.PENDING },
            pendingResults = subList.count { it.status == SubmissionStatus.SUBMITTED || it.status == SubmissionStatus.UNDER_REVIEW }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminDashboardStats())

    fun isUserRegisteredIn(tournamentId: String): Boolean {
        val user = currentUser.value
        return registrations.value.any { it.tournamentId == tournamentId && it.userId == user.id }
    }

    fun registerCurrentPlayer(
        tournamentId: String,
        teamName: String,
        captainIgn: String,
        captainUid: String,
        p1Ign: String,
        p1Uid: String,
        p2Ign: String? = null,
        p2Uid: String? = null,
        p3Ign: String? = null,
        p3Uid: String? = null,
        p4Ign: String? = null,
        p4Uid: String? = null,
        subIgn: String? = null,
        subUid: String? = null
    ): Boolean {
        // UID format check (numeric, 6-12 digits)
        val uidRegex = Regex("^[0-9]{6,12}$")
        if (!uidRegex.matches(captainUid) || !uidRegex.matches(p1Uid)) {
            showMessage("Invalid Free Fire UID! UID must be 6-12 digits.")
            return false
        }
        if (p2Uid != null && p2Uid.isNotBlank() && !uidRegex.matches(p2Uid)) {
            showMessage("Player 2 UID is invalid (6-12 digits required).")
            return false
        }
        if (p3Uid != null && p3Uid.isNotBlank() && !uidRegex.matches(p3Uid)) {
            showMessage("Player 3 UID is invalid (6-12 digits required).")
            return false
        }
        if (p4Uid != null && p4Uid.isNotBlank() && !uidRegex.matches(p4Uid)) {
            showMessage("Player 4 UID is invalid (6-12 digits required).")
            return false
        }

        val success = repository.registerForTournament(
            tournamentId, teamName, captainIgn, captainUid,
            p1Ign, p1Uid, p2Ign, p2Uid, p3Ign, p3Uid, p4Ign, p4Uid, subIgn, subUid
        )

        if (success) {
            showMessage("Successfully joined tournament! Good luck, survivor.")
        } else {
            showMessage("Already registered or tournament is full!")
        }
        return success
    }

    fun submitMatchResult(
        tournamentId: String,
        round: Int,
        placement: Int,
        kills: Int,
        screenshotNote: String
    ) {
        if (placement < 1) {
            showMessage("Please enter valid placement rank (1 or higher).")
            return
        }
        repository.submitResult(tournamentId, round, placement, kills, screenshotNote)
        showMessage("Match result submitted! Pending Admin verification.")
    }

    fun updateProfile(ign: String, uid: String, region: String) {
        val uidRegex = Regex("^[0-9]{6,12}$")
        if (!uidRegex.matches(uid)) {
            showMessage("Free Fire UID must be 6-12 numbers.")
            return
        }
        repository.updateProfile(ign, uid, region)
        showMessage("Profile updated successfully!")
    }

    fun toggleAdminMode() {
        repository.toggleAdminMode()
        val role = if (currentUser.value.isAdmin) "Admin" else "Gamer"
        showMessage("Switched role to: $role")
    }

    // Admin commands
    fun adminCreateTournament(t: Tournament) {
        repository.createTournament(t)
        showMessage("Tournament \"${t.title}\" published successfully!")
    }

    fun adminSetRoomCredentials(
        tournamentId: String,
        roomId: String,
        pass: String,
        instructions: String,
        releaseNow: Boolean
    ) {
        repository.setRoomCredentials(tournamentId, roomId, pass, instructions, releaseNow)
        showMessage(if (releaseNow) "Room details published to participants!" else "Room details saved (Hidden).")
    }

    fun adminUpdateStatus(tournamentId: String, status: TournamentStatus) {
        repository.updateTournamentStatus(tournamentId, status)
        showMessage("Tournament status set to ${status.label}")
    }

    fun adminApproveReg(regId: String) {
        repository.approveRegistration(regId)
        showMessage("Registration approved!")
    }

    fun adminRejectReg(regId: String) {
        repository.rejectRegistration(regId)
        showMessage("Registration rejected.")
    }

    fun adminUpdateLoneWolfWinner(tournamentId: String, matchId: String, p1Score: Int, p2Score: Int, winnerName: String) {
        repository.updateLoneWolfMatchWinner(tournamentId, matchId, p1Score, p2Score, winnerName)
        showMessage("Match result recorded! $winnerName advances.")
    }

    fun adminAddBRScores(
        tournamentId: String,
        teamName: String,
        matches: Int,
        booyahs: Int,
        placementPts: Int,
        kills: Int,
        killPts: Int,
        penalty: Int
    ) {
        repository.addOrUpdateBREntry(tournamentId, teamName, matches, booyahs, placementPts, kills, killPts, penalty)
        showMessage("Leaderboard updated for $teamName!")
    }

    fun adminReviewSubmission(submissionId: String, approved: Boolean, note: String) {
        repository.reviewSubmission(submissionId, approved, note)
        showMessage(if (approved) "Submission approved & points credited!" else "Submission rejected.")
    }

    fun adminApplyModeration(tournamentId: String, target: String, type: ModerationType, penalty: Int, reason: String) {
        repository.applyModeration(tournamentId, target, type, penalty, reason)
        showMessage("Moderation action applied: ${type.label} on $target")
    }

    fun markNotificationRead(id: String) {
        repository.markNotificationRead(id)
    }

    fun markAllNotificationsRead() {
        repository.markAllNotificationsRead()
        showMessage("All notifications marked as read.")
    }
}
