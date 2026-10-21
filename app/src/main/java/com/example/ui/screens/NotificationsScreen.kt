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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppNotification
import com.example.ui.theme.*
import com.example.ui.viewmodel.TournamentViewModel

@Composable
fun NotificationsScreen(
    viewModel: TournamentViewModel,
    onNavigateToTournament: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
    ) {
        // Header
        Surface(
            color = EsportsSurface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Text(
                        text = "NOTIFICATION CENTER",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                }

                if (notifications.any { !it.isRead }) {
                    TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                        Text(
                            text = "MARK ALL READ",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = RadiantGold
                        )
                    }
                }
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.NotificationsNone, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                    Text("No notifications yet", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications) { notif ->
                    NotificationItemCard(
                        notif = notif,
                        onClick = {
                            viewModel.markNotificationRead(notif.id)
                            if (notif.tournamentId != null) {
                                onNavigateToTournament(notif.tournamentId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItemCard(
    notif: AppNotification,
    onClick: () -> Unit
) {
    val (icon, iconColor) = when (notif.type) {
        "ROOM" -> Icons.Default.MeetingRoom to NeonGreen
        "REGISTRATION" -> Icons.Default.HowToReg to CyberCyan
        "MATCH" -> Icons.Default.SportsEsports to FlameOrange
        "RESULT" -> Icons.Default.CloudDone to RadiantGold
        "BRACKET" -> Icons.Default.AccountTree to PurpleNeon
        "PRIZE" -> Icons.Default.EmojiEvents to RadiantGold
        else -> Icons.Default.Notifications to FlameOrange
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = if (notif.isRead) EsportsCard else EsportsSurfaceVariant),
        border = BorderStroke(1.dp, if (!notif.isRead) FlameOrange.copy(alpha = 0.5f) else EsportsCardBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notif.title,
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (notif.isRead) TextPrimary else RadiantGold
                    )
                    Text(
                        text = notif.timeAgo,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Text(
                    text = notif.message,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            if (!notif.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(FlameOrange)
                )
            }
        }
    }
}
