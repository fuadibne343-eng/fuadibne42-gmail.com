package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.TournamentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TournamentViewModel,
    onNavigateToTournament: (String) -> Unit,
    onNavigateToMatchCenter: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tournaments by viewModel.tournaments.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val userTeam by viewModel.userTeam.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedQuickFilter by remember { mutableStateOf("ALL") }

    val featuredTournament = remember(tournaments) {
        tournaments.find { it.status == TournamentStatus.REGISTRATION_OPEN }
            ?: tournaments.firstOrNull()
    }

    val liveTournaments = remember(tournaments) {
        tournaments.filter { it.status == TournamentStatus.LIVE }
    }

    val registrationOpenTournaments = remember(tournaments) {
        tournaments.filter { it.status == TournamentStatus.REGISTRATION_OPEN }
    }

    val upcomingTournaments = remember(tournaments) {
        tournaments.filter { it.status == TournamentStatus.UPCOMING }
    }

    val completedTournaments = remember(tournaments) {
        tournaments.filter { it.status == TournamentStatus.COMPLETED }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Search & Greeting Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "WELCOME BACK,",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = TextMuted,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = currentUser.ffIgn,
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = RadiantGold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EsportsCard,
                        border = BorderStroke(1.dp, EsportsCardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = RadiantGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${currentUser.walletBalance}",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = {
                        Text(
                            "Search Free Fire tournament, map, or ID...",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = FlameOrange
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = EsportsCard,
                        unfocusedContainerColor = EsportsCard,
                        focusedBorderColor = FlameOrange,
                        unfocusedBorderColor = EsportsCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_home")
                )
            }
        }

        // Quick Category Filter Chips
        item {
            val filterOptions = listOf(
                "ALL" to "All Tourneys",
                "BR_RANK" to "BR Rank",
                "LONE_WOLF" to "Lone Wolf",
                "CUSTOM" to "Custom Room",
                "FREE" to "Free Entry"
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { (key, label) ->
                    val isSelected = selectedQuickFilter == key
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) FlameOrange else EsportsCard,
                        border = BorderStroke(1.dp, if (isSelected) FlameOrange else EsportsCardBorder),
                        modifier = Modifier
                            .clickable {
                                selectedQuickFilter = key
                                when (key) {
                                    "ALL" -> {
                                        viewModel.filterMatchType.value = null
                                        viewModel.filterFeeType.value = "ALL"
                                    }
                                    "BR_RANK" -> {
                                        viewModel.filterMatchType.value = MatchType.BR_RANK
                                        viewModel.filterFeeType.value = "ALL"
                                    }
                                    "LONE_WOLF" -> {
                                        viewModel.filterMatchType.value = MatchType.LONE_WOLF
                                        viewModel.filterFeeType.value = "ALL"
                                    }
                                    "CUSTOM" -> {
                                        viewModel.filterMatchType.value = MatchType.CUSTOM
                                        viewModel.filterFeeType.value = "ALL"
                                    }
                                    "FREE" -> {
                                        viewModel.filterMatchType.value = null
                                        viewModel.filterFeeType.value = "FREE"
                                    }
                                }
                            }
                            .testTag("filter_chip_$key")
                    ) {
                        Text(
                            text = label,
                            fontFamily = RajdhaniFamily,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else TextSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Featured Tournament Hero Banner
        if (featuredTournament != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onNavigateToTournament(featuredTournament.id) }
                            .testTag("card_featured_hero"),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.7f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                        ) {
                            // Background Image
                            Image(
                                painter = painterResource(id = R.drawable.hero_banner),
                                contentDescription = "Hero Banner",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Gradient overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.Transparent,
                                                EsportsBackground.copy(alpha = 0.5f),
                                                EsportsBackground.copy(alpha = 0.95f)
                                            )
                                        )
                                    )
                            )

                            // Content inside hero
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DangerCrimson
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocalFireDepartment,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "FEATURED TOURNAMENT",
                                                fontFamily = RajdhaniFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                letterSpacing = 0.5.sp
                                            )
                                        }
                                    }

                                    StatusBadge(status = featuredTournament.status)
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        MatchTypeBadge(type = featuredTournament.matchType)
                                        ModeBadge(mode = featuredTournament.teamMode)
                                    }

                                    Text(
                                        text = featuredTournament.title,
                                        fontFamily = RajdhaniFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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
                                            Icon(
                                                imageVector = Icons.Default.EmojiEvents,
                                                contentDescription = null,
                                                tint = RadiantGold,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = "Prize: $${featuredTournament.prizePool}",
                                                fontFamily = RajdhaniFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = RadiantGold
                                            )
                                        }

                                        Button(
                                            onClick = { onNavigateToTournament(featuredTournament.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text(
                                                text = "JOIN NOW",
                                                fontFamily = RajdhaniFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color.White
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

        // Live Matches Banner (if any)
        if (liveTournaments.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Live Battles",
                    actionText = "Match Center",
                    onActionClick = onNavigateToMatchCenter
                )
            }
            items(liveTournaments) { t ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    TournamentCard(
                        tournament = t,
                        isRegistered = viewModel.isUserRegisteredIn(t.id),
                        onClick = { onNavigateToTournament(t.id) }
                    )
                }
            }
        }

        // Registration Open Tournaments
        if (registrationOpenTournaments.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Registration Open",
                    actionText = "See All",
                    onActionClick = { viewModel.navigateTo(AppScreen.TOURNAMENTS) }
                )
            }
            items(registrationOpenTournaments) { t ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    TournamentCard(
                        tournament = t,
                        isRegistered = viewModel.isUserRegisteredIn(t.id),
                        onClick = { onNavigateToTournament(t.id) }
                    )
                }
            }
        }

        // Upcoming Tournaments
        if (upcomingTournaments.isNotEmpty()) {
            item {
                SectionHeader(title = "Upcoming Matches")
            }
            items(upcomingTournaments) { t ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    TournamentCard(
                        tournament = t,
                        isRegistered = viewModel.isUserRegisteredIn(t.id),
                        onClick = { onNavigateToTournament(t.id) }
                    )
                }
            }
        }

        // Recently Completed Tournaments
        if (completedTournaments.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Recently Completed",
                    actionText = "View Winners",
                    onActionClick = { viewModel.navigateTo(AppScreen.LEADERBOARDS) }
                )
            }
            items(completedTournaments) { t ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    TournamentCard(
                        tournament = t,
                        isRegistered = viewModel.isUserRegisteredIn(t.id),
                        onClick = { onNavigateToTournament(t.id) }
                    )
                }
            }
        }
    }
}
