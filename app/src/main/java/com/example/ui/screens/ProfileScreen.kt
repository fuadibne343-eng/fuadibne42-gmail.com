package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppScreen
import com.example.data.model.PrizeStatus
import com.example.data.model.TransactionStatus
import com.example.ui.components.StatCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TournamentViewModel

@Composable
fun ProfileScreen(
    viewModel: TournamentViewModel,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val prizes by viewModel.prizeRecords.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Gamer Card Header
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
                                listOf(FlameOrange.copy(alpha = 0.25f), EsportsCard)
                            )
                        )
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(FlameOrange, RadiantGold))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = "Avatar",
                            tint = Color.Black,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = user.ffIgn,
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "@${user.username} • ${user.email}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EsportsSurfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "UID: ${user.ffUid}",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = RadiantGold
                            )
                            Text("•", color = TextMuted)
                            Text(
                                text = user.region,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { showEditProfileDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_edit_profile")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("UPDATE FF INFO", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onNavigateToAdmin,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RadiantGold),
                            modifier = Modifier.testTag("btn_goto_admin")
                        ) {
                            Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ADMIN PANEL", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold)
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
                    value = "${user.matchesPlayed}",
                    icon = Icons.Default.Gamepad,
                    iconColor = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Wins",
                    value = "${user.wins}",
                    icon = Icons.Default.EmojiEvents,
                    iconColor = RadiantGold,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Eliminations",
                    value = "${user.totalKills}",
                    icon = Icons.Default.MilitaryTech,
                    iconColor = FlameOrange,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Wallet Card
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EsportsCard),
                    border = BorderStroke(1.dp, RadiantGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "TOURNAMENT WALLET BALANCE",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = RadiantGold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = RadiantGold, modifier = Modifier.size(24.dp))
                                Text(
                                    text = "$${user.walletBalance}",
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = TextPrimary
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.showMessage("Withdrawal request initiated for verification.") },
                            colors = ButtonDefaults.buttonColors(containerColor = RadiantGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("WITHDRAW", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }

        // Prize History Section
        item {
            Text(
                text = "PRIZE EARNINGS & PAYOUTS",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = RadiantGold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }

        items(prizes) { pr ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EsportsCard),
                    border = BorderStroke(1.dp, EsportsCardBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(pr.tournamentTitle, fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text("${pr.position} • ${pr.date}", fontSize = 11.sp, color = TextSecondary)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "+$${pr.amount}",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = RadiantGold
                            )
                            Text(
                                text = pr.status.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (pr.status == PrizeStatus.PAID) NeonGreen else RadiantGold
                            )
                        }
                    }
                }
            }
        }

        // Transactions History Section
        item {
            Text(
                text = "ENTRY FEE TRANSACTIONS",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = RadiantGold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }

        items(transactions) { tx ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EsportsCard),
                    border = BorderStroke(1.dp, EsportsCardBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(tx.transactionId, fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FlameOrange)
                            Text(tx.tournamentTitle, fontSize = 11.sp, color = TextPrimary)
                            Text("${tx.paymentMethod} • ${tx.createdAt}", fontSize = 10.sp, color = TextMuted)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$${tx.amount}",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (tx.status == TransactionStatus.VERIFIED) NeonGreen.copy(alpha = 0.2f) else RadiantGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = tx.status.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tx.status == TransactionStatus.VERIFIED) NeonGreen else RadiantGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var ignInput by remember { mutableStateOf(user.ffIgn) }
        var uidInput by remember { mutableStateOf(user.ffUid) }
        var regionInput by remember { mutableStateOf(user.region) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = "UPDATE FREE FIRE CREDENTIALS",
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    color = FlameOrange
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = ignInput,
                        onValueChange = { ignInput = it },
                        label = { Text("Free Fire In-Game Name (IGN)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uidInput,
                        onValueChange = { uidInput = it },
                        label = { Text("Free Fire UID (8-12 digits)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = regionInput,
                        onValueChange = { regionInput = it },
                        label = { Text("Server / Region") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(ignInput, uidInput, regionInput)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Text("SAVE")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            containerColor = EsportsSurface,
            shape = RoundedCornerShape(14.dp)
        )
    }
}
