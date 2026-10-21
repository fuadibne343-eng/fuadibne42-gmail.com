package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Tournament
import com.example.ui.theme.*

@Composable
fun TournamentCard(
    tournament: Tournament,
    isRegistered: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("tournament_card_${tournament.id}"),
        colors = CardDefaults.cardColors(containerColor = EsportsCard),
        border = BorderStroke(1.dp, if (isRegistered) FlameOrange.copy(alpha = 0.6f) else EsportsCardBorder),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column {
            // Header accent bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                EsportsSurfaceVariant,
                                when (tournament.matchType) {
                                    com.example.data.model.MatchType.BR_RANK -> FlameOrange.copy(alpha = 0.35f)
                                    com.example.data.model.MatchType.LONE_WOLF -> CyberCyan.copy(alpha = 0.35f)
                                    com.example.data.model.MatchType.CUSTOM -> RadiantGold.copy(alpha = 0.35f)
                                }
                            )
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MatchTypeBadge(type = tournament.matchType)
                        ModeBadge(mode = tournament.teamMode)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EsportsSurface.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = tournament.map.mapName,
                                fontFamily = RajdhaniFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    StatusBadge(status = tournament.status)
                }
            }

            // Body
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = tournament.title,
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (isRegistered) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = NeonGreen.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "JOINED",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = NeonGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Schedule & Rounds Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = tournament.matchDateTime,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsScore,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${tournament.roundsCount} ${if (tournament.roundsCount > 1) "Rounds" else "Round"}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Slots Progress
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Slots: ${tournament.joinedSlots}/${tournament.maxSlots} Joined",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        val slotsLeft = (tournament.maxSlots - tournament.joinedSlots).coerceAtLeast(0)
                        Text(
                            text = if (slotsLeft == 0) "FULL" else "$slotsLeft Slots Left",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = if (slotsLeft <= 2) FlameOrange else RadiantGold
                        )
                    }

                    val progress = (tournament.joinedSlots.toFloat() / tournament.maxSlots.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (progress >= 1f) DangerCrimson else FlameOrange,
                        trackColor = EsportsSurfaceVariant
                    )
                }

                Divider(color = EsportsCardBorder, thickness = 1.dp)

                // Footer: Entry Fee & Prize Pool
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ENTRY FEE",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Text(
                            text = if (tournament.entryFee == 0) "FREE" else "$${tournament.entryFee}",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (tournament.entryFee == 0) NeonGreen else TextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "PRIZE POOL",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = RadiantGold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = RadiantGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$${tournament.prizePool}",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = RadiantGold
                            )
                        }
                    }
                }
            }
        }
    }
}
