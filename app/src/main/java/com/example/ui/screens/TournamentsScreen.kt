package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.TournamentCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.TournamentViewModel

@Composable
fun TournamentsScreen(
    viewModel: TournamentViewModel,
    onNavigateToTournament: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredList by viewModel.filteredTournaments.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val matchTypeFilter by viewModel.filterMatchType.collectAsState()
    val teamModeFilter by viewModel.filterTeamMode.collectAsState()
    val feeFilter by viewModel.filterFeeType.collectAsState()
    val statusFilter by viewModel.filterStatus.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
    ) {
        // Top Filter Bar
        Surface(
            color = EsportsSurface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Filter by title or Free Fire map...", fontSize = 13.sp, color = TextMuted) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = FlameOrange)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
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
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("tournaments_search_field")
                )

                // Match Type Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChipItem(
                            label = "ALL FORMATS",
                            isSelected = matchTypeFilter == null,
                            onClick = { viewModel.filterMatchType.value = null }
                        )
                    }
                    items(MatchType.values()) { type ->
                        FilterChipItem(
                            label = type.displayName.uppercase(),
                            isSelected = matchTypeFilter == type,
                            onClick = { viewModel.filterMatchType.value = if (matchTypeFilter == type) null else type }
                        )
                    }
                }

                // Team Mode and Fee Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChipItem(
                            label = "MODE: ALL",
                            isSelected = teamModeFilter == null,
                            onClick = { viewModel.filterTeamMode.value = null },
                            compact = true
                        )
                    }
                    items(TeamMode.values()) { mode ->
                        FilterChipItem(
                            label = mode.displayName.uppercase(),
                            isSelected = teamModeFilter == mode,
                            onClick = { viewModel.filterTeamMode.value = if (teamModeFilter == mode) null else mode },
                            compact = true
                        )
                    }
                    item {
                        FilterChipItem(
                            label = if (feeFilter == "FREE") "FREE ONLY" else "ALL FEES",
                            isSelected = feeFilter == "FREE",
                            onClick = {
                                viewModel.filterFeeType.value = if (feeFilter == "FREE") "ALL" else "FREE"
                            },
                            compact = true
                        )
                    }
                    items(listOf(TournamentStatus.REGISTRATION_OPEN, TournamentStatus.LIVE, TournamentStatus.COMPLETED)) { st ->
                        FilterChipItem(
                            label = st.label,
                            isSelected = statusFilter == st,
                            onClick = { viewModel.filterStatus.value = if (statusFilter == st) null else st },
                            compact = true
                        )
                    }
                }
            }
        }

        // Tournaments List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterListOff,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No tournaments match your filters",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Try clearing search or changing the match type filters.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Button(
                        onClick = {
                            viewModel.searchQuery.value = ""
                            viewModel.filterMatchType.value = null
                            viewModel.filterTeamMode.value = null
                            viewModel.filterFeeType.value = "ALL"
                            viewModel.filterStatus.value = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                    ) {
                        Text("RESET FILTERS")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "${filteredList.size} TOURNAMENTS FOUND",
                        fontFamily = RajdhaniFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = RadiantGold,
                        letterSpacing = 1.sp
                    )
                }

                items(filteredList) { t ->
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

@Composable
fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    compact: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) FlameOrange else EsportsCard,
        border = BorderStroke(1.dp, if (isSelected) FlameOrange else EsportsCardBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontFamily = RajdhaniFamily,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = if (compact) 10.sp else 11.sp,
            color = if (isSelected) Color.White else TextSecondary,
            modifier = Modifier.padding(horizontal = if (compact) 10.dp else 12.dp, vertical = if (compact) 4.dp else 6.dp)
        )
    }
}
