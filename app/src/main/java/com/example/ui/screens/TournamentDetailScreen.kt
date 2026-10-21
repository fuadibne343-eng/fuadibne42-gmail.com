package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.TournamentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailScreen(
    tournamentId: String,
    viewModel: TournamentViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tournaments by viewModel.tournaments.collectAsState()
    val tournament = tournaments.find { it.id == tournamentId } ?: tournaments.firstOrNull()
    val currentUser by viewModel.currentUser.collectAsState()
    val userTeam by viewModel.userTeam.collectAsState()
    val registrations by viewModel.registrations.collectAsState()
    val isRegistered = viewModel.isUserRegisteredIn(tournament?.id ?: "")

    var selectedTab by remember { mutableStateOf(0) }
    var showRegistrationSheet by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current

    if (tournament == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tournament not found", color = TextPrimary)
        }
        return
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = EsportsSurface,
                tonalElevation = 8.dp,
                border = BorderStroke(1.dp, EsportsCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ENTRY FEE",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = if (tournament.entryFee == 0) "FREE ENTRY" else "$${tournament.entryFee}",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (tournament.entryFee == 0) NeonGreen else RadiantGold
                        )
                    }

                    if (isRegistered) {
                        Button(
                            onClick = { selectedTab = 4 }, // jump to Room tab
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_view_room")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MeetingRoom,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "VIEW ROOM DETAILS",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    } else if (tournament.status == TournamentStatus.FULL || tournament.status == TournamentStatus.COMPLETED || tournament.status == TournamentStatus.CANCELLED) {
                        Button(
                            onClick = { /* disabled */ },
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(disabledContainerColor = EsportsSurfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = tournament.status.label,
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }
                    } else {
                        Button(
                            onClick = { showRegistrationSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .testTag("btn_join_tournament")
                        ) {
                            Icon(
                                imageVector = Icons.Default.HowToReg,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "JOIN TOURNAMENT",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(EsportsBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Banner Block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                when (tournament.matchType) {
                                    MatchType.BR_RANK -> FlameOrange.copy(alpha = 0.45f)
                                    MatchType.LONE_WOLF -> CyberCyan.copy(alpha = 0.45f)
                                    MatchType.CUSTOM -> RadiantGold.copy(alpha = 0.45f)
                                },
                                EsportsSurface
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(EsportsSurface.copy(alpha = 0.8f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                        StatusBadge(status = tournament.status)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MatchTypeBadge(type = tournament.matchType)
                            ModeBadge(mode = tournament.teamMode)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EsportsSurface.copy(alpha = 0.8f)
                            ) {
                                Text(
                                    text = "Map: ${tournament.map.mapName}",
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    color = RadiantGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = tournament.title,
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = TextPrimary
                        )

                        Text(
                            text = "Match Time: ${tournament.matchDateTime} • ${tournament.roundsCount} Rounds",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Quick Stats Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Prize Pool",
                    value = "$${tournament.prizePool}",
                    icon = Icons.Default.EmojiEvents,
                    iconColor = RadiantGold,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Slots Joined",
                    value = "${tournament.joinedSlots}/${tournament.maxSlots}",
                    icon = Icons.Default.Groups,
                    iconColor = FlameOrange,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Entry Fee",
                    value = if (tournament.entryFee == 0) "FREE" else "$${tournament.entryFee}",
                    icon = Icons.Default.ConfirmationNumber,
                    iconColor = if (tournament.entryFee == 0) NeonGreen else CyberCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            // Tabs Row
            val tabs = listOf("Overview", "Scoring", "Prizes", "Standings", "Room Details")
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = EsportsSurface,
                contentColor = FlameOrange,
                edgePadding = 16.dp,
                divider = { Divider(color = EsportsCardBorder) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title.uppercase(),
                                fontFamily = RajdhaniFamily,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (selectedTab == index) FlameOrange else TextSecondary
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(modifier = Modifier.padding(16.dp)) {
                when (selectedTab) {
                    0 -> OverviewTabContent(tournament)
                    1 -> ScoringTabContent(tournament)
                    2 -> PrizesTabContent(tournament)
                    3 -> StandingsTabContent(tournament, viewModel)
                    4 -> RoomDetailsTabContent(tournament, isRegistered, clipboardManager)
                }
            }
        }
    }

    // Join / Registration Dialog
    if (showRegistrationSheet) {
        RegistrationDialog(
            tournament = tournament,
            currentUser = currentUser,
            userTeam = userTeam,
            onDismiss = { showRegistrationSheet = false },
            onConfirmRegistration = { teamName, capIgn, capUid, p1Ign, p1Uid, p2Ign, p2Uid, p3Ign, p3Uid, p4Ign, p4Uid, sIgn, sUid ->
                val ok = viewModel.registerCurrentPlayer(
                    tournament.id, teamName, capIgn, capUid,
                    p1Ign, p1Uid, p2Ign, p2Uid, p3Ign, p3Uid, p4Ign, p4Uid, sIgn, sUid
                )
                if (ok) {
                    showRegistrationSheet = false
                    selectedTab = 4 // show room tab
                }
            }
        )
    }
}

@Composable
fun OverviewTabContent(tournament: Tournament) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = EsportsCard),
            border = BorderStroke(1.dp, EsportsCardBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ABOUT TOURNAMENT",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = RadiantGold
                )
                Text(
                    text = tournament.description,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = EsportsCard),
            border = BorderStroke(1.dp, EsportsCardBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "OFFICIAL TOURNAMENT RULES",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = FlameOrange
                )
                Text(
                    text = tournament.rules,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun ScoringTabContent(tournament: Tournament) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = EsportsCard),
            border = BorderStroke(1.dp, EsportsCardBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "SCORING SYSTEM & FORMULA",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = RadiantGold
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EsportsSurfaceVariant
                ) {
                    Text(
                        text = "Total Points = Placement Points + (${tournament.killPoints} × Eliminations) - Penalty Points",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = CyberCyan,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Text(
                    text = tournament.scoringSystemDesc,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // Placement Points Table
        Card(
            colors = CardDefaults.cardColors(containerColor = EsportsCard),
            border = BorderStroke(1.dp, EsportsCardBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "PLACEMENT POINTS BREAKDOWN",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                tournament.placementPoints.entries.sortedBy { it.key }.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        pair.forEach { (place, pts) ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EsportsSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (place == 1) "1st (Booyah)" else "#$place Place",
                                        fontFamily = RajdhaniFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = if (place == 1) RadiantGold else TextSecondary
                                    )
                                    Text(
                                        text = "+$pts pts",
                                        fontFamily = RajdhaniFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (place == 1) RadiantGold else FlameOrange
                                    )
                                }
                            }
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrizesTabContent(tournament: Tournament) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val prizes = listOf(
            Triple("1st Place (Champion)", tournament.prize1st, RadiantGold),
            Triple("2nd Place (Runner-up)", tournament.prize2nd, Color(0xFFC0C0C0)),
            Triple("3rd Place", tournament.prize3rd, Color(0xFFCD7F32)),
            Triple("MVP Fragger Award", tournament.prizeMvp, FlameOrange)
        )

        prizes.forEach { (label, amt, color) ->
            if (amt > 0) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EsportsCard),
                    border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = label,
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Automated prize pool payout",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Text(
                            text = "$$amt",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = color
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StandingsTabContent(tournament: Tournament, viewModel: TournamentViewModel) {
    if (tournament.matchType == MatchType.LONE_WOLF) {
        val lwMatches by viewModel.loneWolfMatches.collectAsState()
        val matches = lwMatches[tournament.id] ?: emptyList()

        if (matches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Bracket generates automatically when registration closes.", color = TextSecondary)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "LONE WOLF KNOCKOUT BRACKET",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = RadiantGold
                )
                matches.forEach { match ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EsportsCard),
                        border = BorderStroke(1.dp, if (match.isCompleted) NeonGreen.copy(alpha = 0.4f) else EsportsCardBorder),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = match.roundName.uppercase(),
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = FlameOrange
                                )
                                if (match.isCompleted) {
                                    Text("COMPLETED", fontSize = 10.sp, color = NeonGreen, fontWeight = FontWeight.Bold)
                                } else {
                                    Text("PENDING", fontSize = 10.sp, color = RadiantGold, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = match.player1Name,
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = if (match.winnerName == match.player1Name) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = if (match.winnerName == match.player1Name) RadiantGold else TextPrimary
                                )
                                Text(
                                    text = "${match.player1Score}",
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                            }
                            Divider(color = EsportsCardBorder)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = match.player2Name,
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = if (match.winnerName == match.player2Name) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = if (match.winnerName == match.player2Name) RadiantGold else TextPrimary
                                )
                                Text(
                                    text = "${match.player2Score}",
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        // BR Rank Leaderboard
        val brLeaderboards by viewModel.brLeaderboards.collectAsState()
        val list = brLeaderboards[tournament.id] ?: emptyList()

        if (list.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Official leaderboard will update once Round 1 results are submitted.", color = TextSecondary)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "BATTLE ROYALE OVERALL STANDINGS",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = RadiantGold
                )
                list.forEach { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EsportsCard),
                        border = BorderStroke(
                            1.dp,
                            when (item.rank) {
                                1 -> RadiantGold.copy(alpha = 0.6f)
                                2 -> Color(0xFFC0C0C0).copy(alpha = 0.5f)
                                3 -> Color(0xFFCD7F32).copy(alpha = 0.5f)
                                else -> EsportsCardBorder
                            }
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (item.rank) {
                                        1 -> RadiantGold.copy(alpha = 0.2f)
                                        2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                                        3 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                                        else -> EsportsSurfaceVariant
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "#${item.rank}",
                                            fontFamily = RajdhaniFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = when (item.rank) {
                                                1 -> RadiantGold
                                                2 -> Color(0xFFC0C0C0)
                                                3 -> Color(0xFFCD7F32)
                                                else -> TextSecondary
                                            }
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = item.teamName,
                                        fontFamily = RajdhaniFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Booyah: ${item.booyahs} | Kills: ${item.eliminations} | Place Pts: ${item.placementPoints}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${item.totalPoints} PTS",
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = FlameOrange
                                )
                                if (item.penaltyPoints > 0) {
                                    Text(
                                        text = "-${item.penaltyPoints} Penalty",
                                        fontSize = 10.sp,
                                        color = DangerCrimson,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoomDetailsTabContent(
    tournament: Tournament,
    isRegistered: Boolean,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager
) {
    if (!isRegistered) {
        Card(
            colors = CardDefaults.cardColors(containerColor = EsportsCard),
            border = BorderStroke(1.dp, DangerCrimson.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = DangerCrimson,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "ACCESS RESTRICTED",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DangerCrimson
                )
                Text(
                    text = "Per Free Fire Fair Play regulations, Custom Room credentials are ONLY revealed to approved participants who have joined this tournament.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else if (!tournament.roomReleased) {
        Card(
            colors = CardDefaults.cardColors(containerColor = EsportsCard),
            border = BorderStroke(1.dp, RadiantGold.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = RadiantGold,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "ROOM CREDENTIALS PENDING",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = RadiantGold
                )
                Text(
                    text = "You are an approved participant! The Admin will release Room ID and Password 15 minutes prior to the match start time. You will receive an instant notification.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        // Unlocked and Released!
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(
                colors = CardDefaults.cardColors(containerColor = EsportsCard),
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "MATCH ROOM ACCESS: GRANTED",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NeonGreen
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = NeonGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "LIVE ROOM",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = NeonGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Divider(color = EsportsCardBorder)

                    // Room ID
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ROOM ID", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = tournament.roomId,
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = RadiantGold
                            )
                        }
                        IconButton(
                            onClick = { clipboardManager.setText(AnnotatedString(tournament.roomId)) },
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EsportsSurfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Room ID",
                                tint = RadiantGold
                            )
                        }
                    }

                    // Room Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ROOM PASSWORD", fontSize = 11.sp, color = TextMuted)
                            Text(
                                text = tournament.roomPassword,
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = CyberCyan
                            )
                        }
                        IconButton(
                            onClick = { clipboardManager.setText(AnnotatedString(tournament.roomPassword)) },
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EsportsSurfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Password",
                                tint = CyberCyan
                            )
                        }
                    }

                    // Room Instructions
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EsportsSurfaceVariant
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("INSTRUCTIONS & SLOT ASSIGNMENT:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FlameOrange)
                            Text(tournament.roomInstructions, fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationDialog(
    tournament: Tournament,
    currentUser: UserProfile,
    userTeam: Team,
    onDismiss: () -> Unit,
    onConfirmRegistration: (
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
    ) -> Unit
) {
    var teamName by remember { mutableStateOf(if (tournament.teamMode == TeamMode.SOLO) currentUser.ffIgn else userTeam.name) }
    var captainIgn by remember { mutableStateOf(currentUser.ffIgn) }
    var captainUid by remember { mutableStateOf(currentUser.ffUid) }

    var p1Ign by remember { mutableStateOf(currentUser.ffIgn) }
    var p1Uid by remember { mutableStateOf(currentUser.ffUid) }

    var p2Ign by remember { mutableStateOf(if (userTeam.members.size > 1) userTeam.members[1].ign else "FXR★VIPER") }
    var p2Uid by remember { mutableStateOf(if (userTeam.members.size > 1) userTeam.members[1].uid else "8923410523") }

    var p3Ign by remember { mutableStateOf(if (userTeam.members.size > 2) userTeam.members[2].ign else "FXR★GHOST") }
    var p3Uid by remember { mutableStateOf(if (userTeam.members.size > 2) userTeam.members[2].uid else "4410982312") }

    var p4Ign by remember { mutableStateOf(if (userTeam.members.size > 3) userTeam.members[3].ign else "FXR★BLAZE") }
    var p4Uid by remember { mutableStateOf(if (userTeam.members.size > 3) userTeam.members[3].uid else "5512093847") }

    var subIgn by remember { mutableStateOf("") }
    var subUid by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "TOURNAMENT REGISTRATION",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = FlameOrange
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${tournament.title} • ${tournament.teamMode.displayName}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                if (tournament.teamMode != TeamMode.SOLO) {
                    OutlinedTextField(
                        value = teamName,
                        onValueChange = { teamName = it },
                        label = { Text("Team Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text("CAPTAIN DETAILS", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RadiantGold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = captainIgn,
                        onValueChange = { captainIgn = it; p1Ign = it },
                        label = { Text("IGN") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = captainUid,
                        onValueChange = { captainUid = it; p1Uid = it },
                        label = { Text("UID (Digits)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (tournament.teamMode == TeamMode.DUO || tournament.teamMode == TeamMode.SQUAD) {
                    Text("PLAYER 2", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = p2Ign,
                            onValueChange = { p2Ign = it },
                            label = { Text("P2 IGN") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = p2Uid,
                            onValueChange = { p2Uid = it },
                            label = { Text("P2 UID") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (tournament.teamMode == TeamMode.SQUAD) {
                    Text("PLAYER 3", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = p3Ign,
                            onValueChange = { p3Ign = it },
                            label = { Text("P3 IGN") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = p3Uid,
                            onValueChange = { p3Uid = it },
                            label = { Text("P3 UID") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text("PLAYER 4", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = p4Ign,
                            onValueChange = { p4Ign = it },
                            label = { Text("P4 IGN") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = p4Uid,
                            onValueChange = { p4Uid = it },
                            label = { Text("P4 UID") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text("OPTIONAL SUBSTITUTE", fontFamily = RajdhaniFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = TextMuted)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = subIgn,
                            onValueChange = { subIgn = it },
                            label = { Text("Sub IGN") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = subUid,
                            onValueChange = { subUid = it },
                            label = { Text("Sub UID") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmRegistration(
                        teamName, captainIgn, captainUid,
                        p1Ign, p1Uid,
                        if (tournament.teamMode != TeamMode.SOLO) p2Ign else null,
                        if (tournament.teamMode != TeamMode.SOLO) p2Uid else null,
                        if (tournament.teamMode == TeamMode.SQUAD) p3Ign else null,
                        if (tournament.teamMode == TeamMode.SQUAD) p3Uid else null,
                        if (tournament.teamMode == TeamMode.SQUAD) p4Ign else null,
                        if (tournament.teamMode == TeamMode.SQUAD) p4Uid else null,
                        if (tournament.teamMode == TeamMode.SQUAD && subIgn.isNotBlank()) subIgn else null,
                        if (tournament.teamMode == TeamMode.SQUAD && subUid.isNotBlank()) subUid else null
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                modifier = Modifier.testTag("btn_confirm_registration")
            ) {
                Text("CONFIRM ENTRY", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        },
        containerColor = EsportsSurface,
        shape = RoundedCornerShape(14.dp)
    )
}
