package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TeamMember
import com.example.ui.components.StatCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TournamentViewModel

@Composable
fun TeamScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    val team by viewModel.userTeam.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    var showEditDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Team Hero Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EsportsCard),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                border = BorderStroke(1.dp, EsportsCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(FlameOrange.copy(alpha = 0.2f), EsportsCard)
                            )
                        )
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(FlameOrange, RadiantGold))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Text(
                        text = team.name,
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = TextPrimary
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EsportsSurfaceVariant
                    ) {
                        Text(
                            text = "CLAN TAG: [${team.tag}] • CAPTAIN: ${team.captainName}",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = RadiantGold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { showEditDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_edit_team")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("EDIT ROSTER", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString("FXR-INVITE-${team.tag}-2026"))
                                viewModel.showMessage("Team invite link copied to clipboard!")
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("INVITE", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Matches",
                    value = "${team.matches}",
                    icon = Icons.Default.SportsEsports,
                    iconColor = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Wins",
                    value = "${team.wins}",
                    icon = Icons.Default.EmojiEvents,
                    iconColor = RadiantGold,
                    modifier = Modifier.weight(1f)
                )
                val winRate = if (team.matches > 0) ((team.wins * 100) / team.matches) else 0
                StatCard(
                    title = "Win Rate",
                    value = "$winRate%",
                    icon = Icons.Default.TrendingUp,
                    iconColor = NeonGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Roster Members
        item {
            Text(
                text = "TEAM ROSTER (${team.members.size} PLAYERS)",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = RadiantGold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        items(team.members) { member ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EsportsCard),
                    border = BorderStroke(1.dp, if (member.role.contains("Captain")) FlameOrange.copy(alpha = 0.5f) else EsportsCardBorder),
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
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (member.role.contains("Captain")) FlameOrange.copy(alpha = 0.2f) else EsportsSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (member.role.contains("Captain")) Icons.Default.Star else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (member.role.contains("Captain")) RadiantGold else TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = member.ign,
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "UID: ${member.uid}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EsportsSurfaceVariant
                        ) {
                            Text(
                                text = member.role.uppercase(),
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                color = if (member.role.contains("Captain")) FlameOrange else TextMuted,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        // Achievements
        item {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TEAM ACHIEVEMENTS",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = RadiantGold
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = EsportsCard),
                    border = BorderStroke(1.dp, EsportsCardBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = team.achievements,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }
    }

    // Edit Team Dialog
    if (showEditDialog) {
        var teamNameInput by remember { mutableStateOf(team.name) }
        var tagInput by remember { mutableStateOf(team.tag) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "EDIT TEAM DETAILS",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    color = FlameOrange
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = teamNameInput,
                        onValueChange = { teamNameInput = it },
                        label = { Text("Team Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tagInput,
                        onValueChange = { tagInput = it },
                        label = { Text("Team Tag (e.g. FXR)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.repository.updateTeam(teamNameInput, tagInput, team.members)
                        showEditDialog = false
                        viewModel.showMessage("Team updated successfully!")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Text("SAVE CHANGES")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            containerColor = EsportsSurface,
            shape = RoundedCornerShape(14.dp)
        )
    }
}
