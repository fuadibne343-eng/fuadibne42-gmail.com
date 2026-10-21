package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppScreen
import com.example.data.model.MatchType
import com.example.data.model.TeamMode
import com.example.data.model.TournamentStatus
import com.example.ui.theme.*

@Composable
fun AppTopBar(
    currentScreen: AppScreen,
    isAdmin: Boolean,
    unreadNotifCount: Int,
    onAdminToggle: () -> Unit,
    onNotifClick: () -> Unit,
    onBackClick: (() -> Unit)? = null
) {
    Surface(
        color = EsportsSurface,
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("btn_back")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    }

                    // Logo badge with flame accent
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(FlameOrange, RadiantGold)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "FXR Logo",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "FXR TOURNAMENT",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "FREE FIRE ESPORTS",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = FlameOrange,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Admin Toggle Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isAdmin) FlameOrange.copy(alpha = 0.2f) else EsportsCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isAdmin) FlameOrange else EsportsCardBorder
                        ),
                        modifier = Modifier
                            .clickable { onAdminToggle() }
                            .testTag("btn_toggle_admin")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.SportsEsports,
                                contentDescription = "Role Mode",
                                tint = if (isAdmin) FlameOrange else RadiantGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isAdmin) "ADMIN" else "GAMER",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (isAdmin) FlameOrange else RadiantGold
                            )
                        }
                    }

                    // Notification Icon with Unread Badge
                    Box {
                        IconButton(
                            onClick = onNotifClick,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("btn_notifications")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = if (unreadNotifCount > 0) RadiantGold else TextSecondary
                            )
                        }
                        if (unreadNotifCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(DangerCrimson),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (unreadNotifCount > 9) "9+" else unreadNotifCount.toString(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            Divider(color = EsportsCardBorder, thickness = 1.dp)
        }
    }
}

@Composable
fun StatusBadge(status: TournamentStatus) {
    val (bgColor, textColor) = when (status) {
        TournamentStatus.REGISTRATION_OPEN -> Color(0xFF00E676).copy(alpha = 0.15f) to Color(0xFF00E676)
        TournamentStatus.LIVE -> DangerCrimson.copy(alpha = 0.2f) to DangerCrimson
        TournamentStatus.UPCOMING -> CyberCyan.copy(alpha = 0.15f) to CyberCyan
        TournamentStatus.FULL -> RadiantGold.copy(alpha = 0.15f) to RadiantGold
        TournamentStatus.COMPLETED -> Color(0xFF94A3B8).copy(alpha = 0.15f) to Color(0xFF94A3B8)
        TournamentStatus.CANCELLED -> Color(0xFF64748B).copy(alpha = 0.15f) to Color(0xFF64748B)
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (status == TournamentStatus.LIVE) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(DangerCrimson)
                )
            }
            Text(
                text = status.label,
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = textColor,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun MatchTypeBadge(type: MatchType) {
    val (color, icon) = when (type) {
        MatchType.BR_RANK -> FlameOrange to Icons.Default.MilitaryTech
        MatchType.LONE_WOLF -> CyberCyan to Icons.Default.FlashOn
        MatchType.CUSTOM -> RadiantGold to Icons.Default.MeetingRoom
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = type.displayName.uppercase(),
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = color
            )
        }
    }
}

@Composable
fun ModeBadge(mode: TeamMode) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = EsportsSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, EsportsCardBorder)
    ) {
        Text(
            text = mode.displayName.uppercase(),
            fontFamily = RajdhaniFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(18.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(FlameOrange)
            )
            Text(
                text = title.uppercase(),
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimary,
                letterSpacing = 1.sp
            )
        }

        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = RadiantGold,
                modifier = Modifier
                    .clickable { onActionClick() }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = EsportsCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, EsportsCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = value,
                    fontFamily = RajdhaniFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun BannerToastMessage(
    message: String?,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        if (message != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                color = EsportsSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, FlameOrange),
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = FlameOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = message,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
