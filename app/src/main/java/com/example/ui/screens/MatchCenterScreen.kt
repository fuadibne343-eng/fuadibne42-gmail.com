package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.MatchTypeBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.TournamentViewModel

@Composable
fun MatchCenterScreen(
    viewModel: TournamentViewModel,
    onNavigateToTournament: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tournaments by viewModel.tournaments.collectAsState()
    val matchSessions by viewModel.matchSessions.collectAsState()
    val submissions by viewModel.resultSubmissions.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showSubmitDialogForTournament by remember { mutableStateOf<Tournament?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
    ) {
        // Tab Header
        Surface(color = EsportsSurface, tonalElevation = 3.dp) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = EsportsSurface,
                contentColor = FlameOrange,
                divider = { Divider(color = EsportsCardBorder) }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "LIVE & UPCOMING",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedTab == 0) FlameOrange else TextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "MY SUBMISSIONS (${submissions.size})",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedTab == 1) FlameOrange else TextSecondary
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "COMPLETED",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (selectedTab == 2) FlameOrange else TextSecondary
                        )
                    }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Live and Upcoming Matches
                val activeTournaments = tournaments.filter {
                    it.status == TournamentStatus.LIVE || it.status == TournamentStatus.REGISTRATION_OPEN || it.status == TournamentStatus.UPCOMING
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "OFFICIAL MATCH SCHEDULE",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = RadiantGold
                        )
                    }

                    items(activeTournaments) { t ->
                        val isRegistered = viewModel.isUserRegisteredIn(t.id)
                        MatchCardItem(
                            tournament = t,
                            isRegistered = isRegistered,
                            onViewDetails = { onNavigateToTournament(t.id) },
                            onSubmitResult = { showSubmitDialogForTournament = t }
                        )
                    }
                }
            }
            1 -> {
                // Result Submissions
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "MATCH RESULT VERIFICATION STATUS",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = RadiantGold
                        )
                    }

                    if (submissions.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = EsportsCard),
                                border = BorderStroke(1.dp, EsportsCardBorder),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudUpload,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Text(
                                        "No results submitted yet",
                                        fontFamily = RajdhaniFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        "After finishing your match, submit screenshot evidence to earn official leaderboard points.",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(submissions) { sub ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = EsportsCard),
                                border = BorderStroke(
                                    1.dp,
                                    when (sub.status) {
                                        SubmissionStatus.APPROVED -> NeonGreen.copy(alpha = 0.5f)
                                        SubmissionStatus.UNDER_REVIEW -> RadiantGold.copy(alpha = 0.5f)
                                        SubmissionStatus.REJECTED -> DangerCrimson.copy(alpha = 0.5f)
                                        SubmissionStatus.SUBMITTED -> CyberCyan.copy(alpha = 0.5f)
                                    }
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = sub.tournamentTitle,
                                            fontFamily = RajdhaniFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = TextPrimary
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = when (sub.status) {
                                                SubmissionStatus.APPROVED -> NeonGreen.copy(alpha = 0.2f)
                                                SubmissionStatus.UNDER_REVIEW -> RadiantGold.copy(alpha = 0.2f)
                                                SubmissionStatus.REJECTED -> DangerCrimson.copy(alpha = 0.2f)
                                                SubmissionStatus.SUBMITTED -> CyberCyan.copy(alpha = 0.2f)
                                            }
                                        ) {
                                            Text(
                                                text = sub.status.name.replace("_", " "),
                                                fontFamily = RajdhaniFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                color = when (sub.status) {
                                                    SubmissionStatus.APPROVED -> NeonGreen
                                                    SubmissionStatus.UNDER_REVIEW -> RadiantGold
                                                    SubmissionStatus.REJECTED -> DangerCrimson
                                                    SubmissionStatus.SUBMITTED -> CyberCyan
                                                },
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Text("Round: #${sub.roundNumber}", fontSize = 12.sp, color = TextSecondary)
                                        Text("Claimed Place: #${sub.claimedPlacement}", fontSize = 12.sp, color = RadiantGold, fontWeight = FontWeight.Bold)
                                        Text("Kills: ${sub.claimedEliminations}", fontSize = 12.sp, color = FlameOrange, fontWeight = FontWeight.Bold)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = EsportsSurfaceVariant
                                    ) {
                                        Text(
                                            text = "Screenshot note: ${sub.screenshotNote}",
                                            fontSize = 11.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }

                                    if (sub.reviewNote.isNotBlank()) {
                                        Text(
                                            text = "Admin Note: ${sub.reviewNote}",
                                            fontSize = 11.sp,
                                            color = if (sub.status == SubmissionStatus.APPROVED) NeonGreen else DangerCrimson,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Completed Matches
                val completed = tournaments.filter { it.status == TournamentStatus.COMPLETED }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(completed) { t ->
                        MatchCardItem(
                            tournament = t,
                            isRegistered = viewModel.isUserRegisteredIn(t.id),
                            onViewDetails = { onNavigateToTournament(t.id) },
                            onSubmitResult = null
                        )
                    }
                }
            }
        }
    }

    // Result Submission Dialog
    if (showSubmitDialogForTournament != null) {
        val t = showSubmitDialogForTournament!!
        var roundNum by remember { mutableStateOf("1") }
        var placementRank by remember { mutableStateOf("1") }
        var eliminations by remember { mutableStateOf("4") }
        var screenshotEvidence by remember { mutableStateOf("Booyah final screen with player stats captured.") }

        AlertDialog(
            onDismissRequest = { showSubmitDialogForTournament = null },
            title = {
                Text(
                    text = "SUBMIT MATCH RESULT EVIDENCE",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = FlameOrange
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${t.title} • Map: ${t.map.mapName}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = roundNum,
                            onValueChange = { roundNum = it },
                            label = { Text("Round #") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = placementRank,
                            onValueChange = { placementRank = it },
                            label = { Text("Placement (#)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = eliminations,
                        onValueChange = { eliminations = it },
                        label = { Text("Team Total Kills") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = screenshotEvidence,
                        onValueChange = { screenshotEvidence = it },
                        label = { Text("Screenshot Evidence Note") },
                        placeholder = { Text("Describe proof or link image...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Note: Submitted results are audited by Admin referees before leaderboard points update.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val round = roundNum.toIntOrNull() ?: 1
                        val place = placementRank.toIntOrNull() ?: 1
                        val kills = eliminations.toIntOrNull() ?: 0
                        viewModel.submitMatchResult(t.id, round, place, kills, screenshotEvidence)
                        showSubmitDialogForTournament = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Text("SUBMIT EVIDENCE")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialogForTournament = null }) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            containerColor = EsportsSurface,
            shape = RoundedCornerShape(14.dp)
        )
    }
}

@Composable
fun MatchCardItem(
    tournament: Tournament,
    isRegistered: Boolean,
    onViewDetails: () -> Unit,
    onSubmitResult: (() -> Unit)?
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = EsportsCard),
        border = BorderStroke(1.dp, EsportsCardBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MatchTypeBadge(type = tournament.matchType)
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = EsportsSurfaceVariant
                    ) {
                        Text(
                            text = tournament.map.mapName,
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                StatusBadge(status = tournament.status)
            }

            Text(
                text = tournament.title,
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Text(tournament.matchDateTime, fontSize = 12.sp, color = TextSecondary)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = if (tournament.roomReleased) NeonGreen else TextMuted, modifier = Modifier.size(14.dp))
                    Text(
                        text = if (tournament.roomReleased) "Room Live" else "Room Locked",
                        fontSize = 12.sp,
                        color = if (tournament.roomReleased) NeonGreen else TextMuted
                    )
                }
            }

            Divider(color = EsportsCardBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("TOURNAMENT INFO", fontSize = 11.sp, fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold)
                }

                if (isRegistered && onSubmitResult != null) {
                    Button(
                        onClick = onSubmitResult,
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("SUBMIT RESULT", fontSize = 11.sp, fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
