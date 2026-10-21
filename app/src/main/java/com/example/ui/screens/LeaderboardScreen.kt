package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.MatchTypeBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.TournamentViewModel

@Composable
fun LeaderboardScreen(
    viewModel: TournamentViewModel,
    onNavigateToTournament: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tournaments by viewModel.tournaments.collectAsState()
    val brLeaderboards by viewModel.brLeaderboards.collectAsState()
    val loneWolfMatches by viewModel.loneWolfMatches.collectAsState()

    var selectedTournamentId by remember {
        mutableStateOf(tournaments.firstOrNull()?.id ?: "")
    }

    val selectedTournament = tournaments.find { it.id == selectedTournamentId } ?: tournaments.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
    ) {
        // Tournament Selector Bar
        Surface(color = EsportsSurface, tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SELECT TOURNAMENT",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = RadiantGold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tournaments) { t ->
                        val isSelected = (selectedTournament?.id == t.id)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) FlameOrange.copy(alpha = 0.2f) else EsportsCard,
                            border = BorderStroke(1.dp, if (isSelected) FlameOrange else EsportsCardBorder),
                            modifier = Modifier.clickable { selectedTournamentId = t.id }
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text(
                                    text = t.title,
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) RadiantGold else TextPrimary,
                                    maxLines = 1
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(t.matchType.displayName, fontSize = 11.sp, color = TextSecondary)
                                    Text("•", fontSize = 11.sp, color = TextMuted)
                                    Text(t.status.label, fontSize = 11.sp, color = if (t.status == TournamentStatus.LIVE) DangerCrimson else TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedTournament == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tournament selected", color = TextSecondary)
            }
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info for Selected Tournament
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EsportsCard),
                    border = BorderStroke(1.dp, EsportsCardBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                MatchTypeBadge(type = selectedTournament.matchType)
                                StatusBadge(status = selectedTournament.status)
                            }
                            Text(
                                text = "Prize: $${selectedTournament.prizePool}",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = RadiantGold
                            )
                        }

                        Text(
                            text = selectedTournament.title,
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Winner Podium (if Completed)
            if (selectedTournament.status == TournamentStatus.COMPLETED && selectedTournament.championName != null) {
                item {
                    WinnerPodiumCard(selectedTournament)
                }
            }

            // Lone Wolf Bracket OR BR Rank Leaderboard
            if (selectedTournament.matchType == MatchType.LONE_WOLF) {
                val matches = loneWolfMatches[selectedTournament.id] ?: emptyList()
                item {
                    Text(
                        text = "LONE WOLF KNOCKOUT BRACKET",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = RadiantGold
                    )
                }

                if (matches.isEmpty()) {
                    item {
                        Text("No bracket generated yet. Bracket will populate automatically.", color = TextSecondary)
                    }
                } else {
                    items(matches) { match ->
                        LoneWolfBracketCard(match)
                    }
                }
            } else {
                // BR Rank Leaderboard Table
                val leaderboard = brLeaderboards[selectedTournament.id] ?: emptyList()
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OFFICIAL BR LEADERBOARD",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = RadiantGold
                        )
                        Text(
                            text = "Placement + Kill Pts",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                if (leaderboard.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EsportsCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("Awaiting first round matches and referee verification.", color = TextSecondary)
                            }
                        }
                    }
                } else {
                    // Leaderboard header bar
                    item {
                        Surface(
                            color = EsportsSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("#  TEAM", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted)
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text("MATCH", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted)
                                    Text("BOOYAH", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted)
                                    Text("KILLS", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted)
                                    Text("PTS", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = RadiantGold)
                                }
                            }
                        }
                    }

                    items(leaderboard) { entry ->
                        BRLeaderboardRow(entry)
                    }
                }
            }
        }
    }
}

@Composable
fun WinnerPodiumCard(tournament: Tournament) {
    Card(
        colors = CardDefaults.cardColors(containerColor = EsportsCard),
        border = BorderStroke(1.dp, RadiantGold),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(RadiantGold.copy(alpha = 0.15f), EsportsCard)
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = RadiantGold,
                modifier = Modifier.size(44.dp)
            )

            Text(
                text = "TOURNAMENT CHAMPIONS",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = RadiantGold,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd Place (Runner-up)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFC0C0C0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("2", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Text(
                        text = tournament.runnerUpName ?: "Runner-Up",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text("$${tournament.prize2nd}", fontSize = 11.sp, color = Color(0xFFC0C0C0), fontWeight = FontWeight.Bold)
                }

                // 1st Place (Champion)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(RadiantGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = tournament.championName ?: "Champion",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = RadiantGold
                    )
                    Text(
                        text = "$${tournament.prize1st}",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = RadiantGold
                    )
                }

                // 3rd Place
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCD7F32)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("3", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Text(
                        text = tournament.thirdPlaceName ?: "3rd Place",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text("$${tournament.prize3rd}", fontSize = 11.sp, color = Color(0xFFCD7F32), fontWeight = FontWeight.Bold)
                }
            }

            if (tournament.mvpName != null) {
                Divider(color = EsportsCardBorder)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.MilitaryTech, contentDescription = null, tint = FlameOrange, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "MVP Fragger: ${tournament.mvpName} (+$${tournament.prizeMvp})",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = FlameOrange
                    )
                }
            }
        }
    }
}

@Composable
fun BRLeaderboardRow(entry: BRLeaderboardEntry) {
    Card(
        colors = CardDefaults.cardColors(containerColor = EsportsCard),
        border = BorderStroke(
            1.dp,
            when (entry.rank) {
                1 -> RadiantGold.copy(alpha = 0.5f)
                2 -> Color(0xFFC0C0C0).copy(alpha = 0.4f)
                3 -> Color(0xFFCD7F32).copy(alpha = 0.4f)
                else -> EsportsCardBorder
            }
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (entry.rank) {
                        1 -> RadiantGold.copy(alpha = 0.2f)
                        2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                        3 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                        else -> EsportsSurfaceVariant
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${entry.rank}",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = when (entry.rank) {
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
                        text = entry.teamName,
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    if (entry.penaltyPoints > 0) {
                        Text(
                            text = "Penalty: -${entry.penaltyPoints} pts",
                            fontSize = 10.sp,
                            color = DangerCrimson,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${entry.matchesPlayed}",
                    fontFamily = RajdhaniFamily,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(30.dp)
                )
                Text(
                    text = "${entry.booyahs}",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (entry.booyahs > 0) RadiantGold else TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(30.dp)
                )
                Text(
                    text = "${entry.eliminations}",
                    fontFamily = RajdhaniFamily,
                    fontSize = 13.sp,
                    color = FlameOrange,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(30.dp)
                )
                Text(
                    text = "${entry.totalPoints}",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = RadiantGold,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(40.dp)
                )
            }
        }
    }
}

@Composable
fun LoneWolfBracketCard(match: LoneWolfMatch) {
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
                    Text(
                        text = "WINNER: ${match.winnerName}",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = NeonGreen
                    )
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
                    fontSize = 15.sp,
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
                    fontSize = 15.sp,
                    color = TextPrimary
                )
            }
        }
    }
}
