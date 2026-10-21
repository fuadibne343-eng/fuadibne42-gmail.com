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
import com.example.ui.components.MatchTypeBadge
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.TournamentViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: TournamentViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.adminStats.collectAsState()
    val tournaments by viewModel.tournaments.collectAsState()
    val registrations by viewModel.registrations.collectAsState()
    val submissions by viewModel.resultSubmissions.collectAsState()
    val loneWolfMatches by viewModel.loneWolfMatches.collectAsState()
    val moderationActions by viewModel.moderationActions.collectAsState()

    var selectedAdminTab by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
    ) {
        // Admin Top Bar
        Surface(
            color = EsportsSurface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
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
                        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(imageVector = Icons.Default.AdminPanelSettings, contentDescription = null, tint = FlameOrange, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "ADMIN COMMAND CENTER",
                                    fontFamily = RajdhaniFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = "FULL FREE FIRE TOURNAMENT LIFECYCLE MANAGEMENT",
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 9.sp,
                                color = RadiantGold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Button(
                        onClick = { selectedAdminTab = 0 }, // Open Wizard
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp).testTag("btn_admin_create_wizard")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("NEW TOURNAMENT", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
                Divider(color = EsportsCardBorder)
            }
        }

        // Admin Navigation Tabs
        val adminTabs = listOf(
            "Wizard",
            "Rooms & Status",
            "Registrations (${registrations.count { it.status == RegistrationStatus.PENDING }})",
            "BR Scoring",
            "Lone Wolf",
            "Submissions (${submissions.count { it.status == SubmissionStatus.SUBMITTED }})",
            "Fair Play"
        )

        ScrollableTabRow(
            selectedTabIndex = selectedAdminTab,
            containerColor = EsportsSurface,
            contentColor = FlameOrange,
            edgePadding = 16.dp,
            divider = { Divider(color = EsportsCardBorder) }
        ) {
            adminTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedAdminTab == index,
                    onClick = { selectedAdminTab = index },
                    text = {
                        Text(
                            text = title.uppercase(),
                            fontFamily = RajdhaniFamily,
                            fontWeight = if (selectedAdminTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            color = if (selectedAdminTab == index) FlameOrange else TextSecondary
                        )
                    }
                )
            }
        }

        // Admin Content
        when (selectedAdminTab) {
            0 -> TournamentWizardTab(viewModel = viewModel, onCreated = { selectedAdminTab = 1 })
            1 -> RoomAndStatusTab(tournaments = tournaments, viewModel = viewModel)
            2 -> RegistrationsManagerTab(registrations = registrations, tournaments = tournaments, viewModel = viewModel)
            3 -> BRScoringTab(tournaments = tournaments, viewModel = viewModel)
            4 -> LoneWolfAdminTab(tournaments = tournaments, loneWolfMatches = loneWolfMatches, viewModel = viewModel)
            5 -> SubmissionsReviewTab(submissions = submissions, viewModel = viewModel)
            6 -> FairPlayTab(tournaments = tournaments, actions = moderationActions, viewModel = viewModel)
        }
    }
}

// -------------------------------------------------------------------------------------
// 1. 8-STEP TOURNAMENT CREATION WIZARD (Requirement 20 & 21)
// -------------------------------------------------------------------------------------
@Composable
fun TournamentWizardTab(
    viewModel: TournamentViewModel,
    onCreated: () -> Unit
) {
    var step by remember { mutableStateOf(1) }

    // Form States
    var title by remember { mutableStateOf("FXR PRO BATTLE ROYALE S5") }
    var description by remember { mutableStateOf("Official 4-round Free Fire BR tournament in Bermuda and Purgatory.") }
    var matchType by remember { mutableStateOf(MatchType.BR_RANK) }
    var teamMode by remember { mutableStateOf(TeamMode.SQUAD) }
    var selectedMap by remember { mutableStateOf(FFMap.BERMUDA) }
    var roundsCount by remember { mutableStateOf("4") }
    var maxSlots by remember { mutableStateOf("12") }
    var matchDateTime by remember { mutableStateOf("Tomorrow, 20:00") }
    var entryFee by remember { mutableStateOf("100") }
    var prizePool by remember { mutableStateOf("10000") }
    var prize1st by remember { mutableStateOf("6000") }
    var prize2nd by remember { mutableStateOf("3000") }
    var prize3rd by remember { mutableStateOf("1000") }
    var killPoints by remember { mutableStateOf("1") }
    var customRules by remember { mutableStateOf("Mobile devices only. No emulators or triggers. Gun attributes OFF.") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Wizard Step Indicator
            Card(
                colors = CardDefaults.cardColors(containerColor = EsportsCard),
                border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STEP $step OF 8: ${
                                when (step) {
                                    1 -> "BASIC INFORMATION"
                                    2 -> "MATCH FORMAT TYPE"
                                    3 -> "TEAM MODE"
                                    4 -> "MAP & SCHEDULE"
                                    5 -> "ENTRY FEE & PRIZES"
                                    6 -> "SCORING & RULES"
                                    7 -> "PREVIEW"
                                    else -> "PUBLISH"
                                }
                            }",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = RadiantGold
                        )
                        Text(
                            text = "${(step * 100) / 8}%",
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = FlameOrange
                        )
                    }

                    LinearProgressIndicator(
                        progress = { step.toFloat() / 8f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = FlameOrange,
                        trackColor = EsportsSurfaceVariant
                    )
                }
            }
        }

        // Step Content
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EsportsCard),
                border = BorderStroke(1.dp, EsportsCardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    when (step) {
                        1 -> {
                            Text("STEP 1: TOURNAMENT INFORMATION", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Tournament Title") },
                                modifier = Modifier.fillMaxWidth().testTag("wizard_input_title")
                            )
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Description & Lore") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        2 -> {
                            Text("STEP 2: CHOOSE MATCH TYPE", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
                            MatchType.values().forEach { type ->
                                val isSelected = matchType == type
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) FlameOrange.copy(alpha = 0.2f) else EsportsSurfaceVariant,
                                    border = BorderStroke(1.dp, if (isSelected) FlameOrange else EsportsCardBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { matchType = type }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        RadioButton(selected = isSelected, onClick = { matchType = type })
                                        Column {
                                            Text(type.displayName, fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            Text(
                                                text = when (type) {
                                                    MatchType.BR_RANK -> "Classic 48-player Free Fire survival with placement & kill points"
                                                    MatchType.LONE_WOLF -> "Fast-paced knockout duels with auto bracket generation"
                                                    MatchType.CUSTOM -> "Fully customizable room settings, guns, Gloo walls and formats"
                                                },
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        3 -> {
                            Text("STEP 3: CHOOSE TEAM MODE", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
                            val allowedModes = if (matchType == MatchType.LONE_WOLF) listOf(TeamMode.SOLO, TeamMode.DUO) else TeamMode.values().toList()
                            allowedModes.forEach { mode ->
                                val isSelected = teamMode == mode
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) FlameOrange.copy(alpha = 0.2f) else EsportsSurfaceVariant,
                                    border = BorderStroke(1.dp, if (isSelected) FlameOrange else EsportsCardBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { teamMode = mode }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        RadioButton(selected = isSelected, onClick = { teamMode = mode })
                                        Text(
                                            text = "${mode.displayName.uppercase()} (${mode.maxPlayers} ${if (mode.maxPlayers == 1) "Player" else "Players"})",
                                            fontFamily = RajdhaniFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                        4 -> {
                            Text("STEP 4: MAP & SCHEDULE", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
                            Text("Select Available Free Fire Map:", fontSize = 12.sp, color = TextSecondary)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(FFMap.values()) { map ->
                                    val isSelected = selectedMap == map
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) FlameOrange else EsportsSurfaceVariant,
                                        modifier = Modifier.clickable { selectedMap = map }
                                    ) {
                                        Text(
                                            text = map.mapName,
                                            fontFamily = RajdhaniFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else TextPrimary,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = roundsCount,
                                    onValueChange = { roundsCount = it },
                                    label = { Text("Number of Rounds") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = maxSlots,
                                    onValueChange = { maxSlots = it },
                                    label = { Text("Maximum Slots") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            OutlinedTextField(
                                value = matchDateTime,
                                onValueChange = { matchDateTime = it },
                                label = { Text("Match Date & Time") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        5 -> {
                            Text("STEP 5: ENTRY FEE & PRIZES", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = entryFee,
                                    onValueChange = { entryFee = it },
                                    label = { Text("Entry Fee ($0 for Free)") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = prizePool,
                                    onValueChange = { prizePool = it },
                                    label = { Text("Total Prize Pool ($)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = prize1st,
                                    onValueChange = { prize1st = it },
                                    label = { Text("1st Place Prize") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = prize2nd,
                                    onValueChange = { prize2nd = it },
                                    label = { Text("2nd Place Prize") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            OutlinedTextField(
                                value = prize3rd,
                                onValueChange = { prize3rd = it },
                                label = { Text("3rd Place Prize") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        6 -> {
                            Text("STEP 6: SCORING & CUSTOM RULES", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
                            OutlinedTextField(
                                value = killPoints,
                                onValueChange = { killPoints = it },
                                label = { Text("Points per Elimination / Kill") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = customRules,
                                onValueChange = { customRules = it },
                                label = { Text("Special Tournament Rules") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        7 -> {
                            Text("STEP 7: PREVIEW TOURNAMENT", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
                            Text("Title: $title", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Format: ${matchType.displayName} • ${teamMode.displayName}", color = FlameOrange)
                            Text("Map: ${selectedMap.mapName} • $roundsCount Rounds • $maxSlots Slots", color = TextSecondary)
                            Text("Entry Fee: ${if (entryFee == "0" || entryFee.isBlank()) "FREE" else "$$entryFee"} | Prize: $$prizePool", color = RadiantGold, fontWeight = FontWeight.Bold)
                            Text("Rules: $customRules", fontSize = 12.sp, color = TextMuted)
                        }
                        8 -> {
                            Text("STEP 8: READY TO PUBLISH!", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = NeonGreen)
                            Text(
                                "Publishing will immediately open registrations on the platform and initialize the scoring engine or knockout bracket.",
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Navigation Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = { step-- },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("PREVIOUS")
                    }
                } else {
                    Spacer(modifier = Modifier.width(10.dp))
                }

                if (step < 8) {
                    Button(
                        onClick = { step++ },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("wizard_btn_next")
                    ) {
                        Text("NEXT STEP")
                    }
                } else {
                    Button(
                        onClick = {
                            val fee = entryFee.toIntOrNull() ?: 0
                            val pool = prizePool.toIntOrNull() ?: 1000
                            val p1 = prize1st.toIntOrNull() ?: (pool * 60 / 100)
                            val p2 = prize2nd.toIntOrNull() ?: (pool * 30 / 100)
                            val p3 = prize3rd.toIntOrNull() ?: (pool * 10 / 100)
                            val rounds = roundsCount.toIntOrNull() ?: 1
                            val slots = maxSlots.toIntOrNull() ?: 12
                            val kPts = killPoints.toIntOrNull() ?: 1

                            val newTourney = Tournament(
                                id = "t_custom_${UUID.randomUUID().toString().take(6)}",
                                title = title,
                                description = description,
                                matchType = matchType,
                                teamMode = teamMode,
                                map = selectedMap,
                                roundsCount = rounds,
                                entryFee = fee,
                                prizePool = pool,
                                prize1st = p1,
                                prize2nd = p2,
                                prize3rd = p3,
                                registrationDeadline = "Today, 18:00",
                                matchDateTime = matchDateTime,
                                maxSlots = slots,
                                joinedSlots = 0,
                                status = TournamentStatus.REGISTRATION_OPEN,
                                rules = customRules,
                                killPoints = kPts,
                                isKnockoutBracket = (matchType == MatchType.LONE_WOLF)
                            )

                            viewModel.adminCreateTournament(newTourney)
                            onCreated()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("wizard_btn_publish")
                    ) {
                        Text("PUBLISH TOURNAMENT", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 2. ROOMS & STATUS TAB (Requirement 11 & 20)
// -------------------------------------------------------------------------------------
@Composable
fun RoomAndStatusTab(
    tournaments: List<Tournament>,
    viewModel: TournamentViewModel
) {
    var selectedTournament by remember { mutableStateOf(tournaments.firstOrNull()) }
    var roomIdInput by remember { mutableStateOf(selectedTournament?.roomId ?: "FF-ROOM-88231") }
    var passwordInput by remember { mutableStateOf(selectedTournament?.roomPassword ?: "FXR#2026") }
    var instructionsInput by remember { mutableStateOf(selectedTournament?.roomInstructions ?: "") }

    LaunchedEffect(selectedTournament) {
        if (selectedTournament != null) {
            roomIdInput = selectedTournament!!.roomId
            passwordInput = selectedTournament!!.roomPassword
            instructionsInput = selectedTournament!!.roomInstructions
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("SELECT TOURNAMENT TO CONFIGURE", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tournaments) { t ->
                    val isSelected = selectedTournament?.id == t.id
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) FlameOrange else EsportsCard,
                        border = BorderStroke(1.dp, if (isSelected) FlameOrange else EsportsCardBorder),
                        modifier = Modifier.clickable { selectedTournament = t }
                    ) {
                        Text(
                            text = t.title,
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        if (selectedTournament != null) {
            val t = selectedTournament!!
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EsportsCard),
                    border = BorderStroke(1.dp, EsportsCardBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("TOURNAMENT STATUS CONTROLLER", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                TournamentStatus.REGISTRATION_OPEN,
                                TournamentStatus.LIVE,
                                TournamentStatus.COMPLETED
                            ).forEach { st ->
                                val isCur = t.status == st
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isCur) FlameOrange else EsportsSurfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.adminUpdateStatus(t.id, st) }
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                        Text(st.label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isCur) Color.White else TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EsportsCard),
                    border = BorderStroke(1.dp, if (t.roomReleased) NeonGreen else EsportsCardBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("PRIVATE MATCH ROOM CREDENTIALS", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (t.roomReleased) NeonGreen.copy(alpha = 0.2f) else DangerCrimson.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (t.roomReleased) "RELEASED TO TEAMS" else "HIDDEN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (t.roomReleased) NeonGreen else DangerCrimson,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = roomIdInput,
                                onValueChange = { roomIdInput = it },
                                label = { Text("Room ID") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Password") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = instructionsInput,
                            onValueChange = { instructionsInput = it },
                            label = { Text("Room Instructions & Slot Numbers") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.adminSetRoomCredentials(t.id, roomIdInput, passwordInput, instructionsInput, releaseNow = true)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("RELEASE ROOM NOW", fontWeight = FontWeight.Bold, color = Color.Black)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.adminSetRoomCredentials(t.id, roomIdInput, passwordInput, instructionsInput, releaseNow = false)
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("SAVE AS HIDDEN")
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 3. REGISTRATIONS MANAGER (Requirement 6 & 20)
// -------------------------------------------------------------------------------------
@Composable
fun RegistrationsManagerTab(
    registrations: List<Registration>,
    tournaments: List<Tournament>,
    viewModel: TournamentViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "PLAYER & TEAM REGISTRATIONS (${registrations.size})",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = RadiantGold
            )
        }

        if (registrations.isEmpty()) {
            item {
                Text("No registrations yet.", color = TextSecondary)
            }
        } else {
            items(registrations) { reg ->
                val tournament = tournaments.find { it.id == reg.tournamentId }
                Card(
                    colors = CardDefaults.cardColors(containerColor = EsportsCard),
                    border = BorderStroke(
                        1.dp,
                        when (reg.status) {
                            RegistrationStatus.APPROVED -> NeonGreen.copy(alpha = 0.5f)
                            RegistrationStatus.PENDING -> RadiantGold.copy(alpha = 0.5f)
                            RegistrationStatus.REJECTED -> DangerCrimson.copy(alpha = 0.5f)
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = reg.teamName,
                                fontFamily = RajdhaniFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (reg.status) {
                                    RegistrationStatus.APPROVED -> NeonGreen.copy(alpha = 0.2f)
                                    RegistrationStatus.PENDING -> RadiantGold.copy(alpha = 0.2f)
                                    RegistrationStatus.REJECTED -> DangerCrimson.copy(alpha = 0.2f)
                                }
                            ) {
                                Text(
                                    text = reg.status.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (reg.status) {
                                        RegistrationStatus.APPROVED -> NeonGreen
                                        RegistrationStatus.PENDING -> RadiantGold
                                        RegistrationStatus.REJECTED -> DangerCrimson
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "Tournament: ${tournament?.title ?: reg.tournamentId}",
                            fontSize = 12.sp,
                            color = FlameOrange
                        )

                        Text(
                            text = "Captain: ${reg.captainIgn} (UID: ${reg.captainUid})",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        if (reg.player2Ign != null) {
                            Text(
                                text = "Roster: P1: ${reg.player1Ign} | P2: ${reg.player2Ign}${if (reg.player3Ign != null) " | P3: ${reg.player3Ign}" else ""}${if (reg.player4Ign != null) " | P4: ${reg.player4Ign}" else ""}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        if (reg.status == RegistrationStatus.PENDING) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.adminApproveReg(reg.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("APPROVE", fontWeight = FontWeight.Bold, color = Color.Black)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.adminRejectReg(reg.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("REJECT", color = DangerCrimson)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 4. BR SCORING CONTROLLER (Requirement 8 & 20)
// -------------------------------------------------------------------------------------
@Composable
fun BRScoringTab(
    tournaments: List<Tournament>,
    viewModel: TournamentViewModel
) {
    val brTourneys = tournaments.filter { it.matchType == MatchType.BR_RANK }
    var selectedTourney by remember { mutableStateOf(brTourneys.firstOrNull()) }

    var teamNameInput by remember { mutableStateOf("FXR PRIME ESPORTS") }
    var matchesPlayedInput by remember { mutableStateOf("2") }
    var booyahsInput by remember { mutableStateOf("1") }
    var placementPtsInput by remember { mutableStateOf("21") }
    var eliminationsInput by remember { mutableStateOf("14") }
    var penaltyPtsInput by remember { mutableStateOf("0") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("SELECT BR TOURNAMENT", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(brTourneys) { t ->
                    val isSel = selectedTourney?.id == t.id
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) FlameOrange else EsportsCard,
                        modifier = Modifier.clickable { selectedTourney = t }
                    ) {
                        Text(
                            text = t.title,
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSel) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (selectedTourney != null) {
            val t = selectedTourney!!
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EsportsCard),
                    border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("ENTER / UPDATE TEAM MATCH SCORES", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)

                        OutlinedTextField(
                            value = teamNameInput,
                            onValueChange = { teamNameInput = it },
                            label = { Text("Team Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = matchesPlayedInput,
                                onValueChange = { matchesPlayedInput = it },
                                label = { Text("Matches Played") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = booyahsInput,
                                onValueChange = { booyahsInput = it },
                                label = { Text("Booyahs (Wins)") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = placementPtsInput,
                                onValueChange = { placementPtsInput = it },
                                label = { Text("Placement Points") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = eliminationsInput,
                                onValueChange = { eliminationsInput = it },
                                label = { Text("Team Eliminations (Kills)") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = penaltyPtsInput,
                            onValueChange = { penaltyPtsInput = it },
                            label = { Text("Penalty Points Deduction") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val m = matchesPlayedInput.toIntOrNull() ?: 1
                                val b = booyahsInput.toIntOrNull() ?: 0
                                val p = placementPtsInput.toIntOrNull() ?: 0
                                val k = eliminationsInput.toIntOrNull() ?: 0
                                val pen = penaltyPtsInput.toIntOrNull() ?: 0
                                val kPts = k * t.killPoints

                                viewModel.adminAddBRScores(t.id, teamNameInput, m, b, p, k, kPts, pen)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("UPDATE LEADERBOARD", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 5. LONE WOLF BRACKET CONTROLLER (Requirement 9 & 20)
// -------------------------------------------------------------------------------------
@Composable
fun LoneWolfAdminTab(
    tournaments: List<Tournament>,
    loneWolfMatches: Map<String, List<LoneWolfMatch>>,
    viewModel: TournamentViewModel
) {
    val lwTourneys = tournaments.filter { it.matchType == MatchType.LONE_WOLF }
    var selectedTourney by remember { mutableStateOf(lwTourneys.firstOrNull()) }

    var selectedMatchToEdit by remember { mutableStateOf<LoneWolfMatch?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("SELECT LONE WOLF TOURNAMENT", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(lwTourneys) { t ->
                    val isSel = selectedTourney?.id == t.id
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSel) FlameOrange else EsportsCard,
                        modifier = Modifier.clickable { selectedTourney = t }
                    ) {
                        Text(
                            text = t.title,
                            fontFamily = RajdhaniFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isSel) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (selectedTourney != null) {
            val t = selectedTourney!!
            val matches = loneWolfMatches[t.id] ?: emptyList()

            item {
                Text("TAP A MATCH TO ENTER SCORES & ADVANCE WINNER:", fontSize = 12.sp, color = TextSecondary)
            }

            items(matches) { m ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = EsportsCard),
                    border = BorderStroke(1.dp, if (m.isCompleted) NeonGreen.copy(alpha = 0.5f) else EsportsCardBorder),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.clickable { selectedMatchToEdit = m }
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(m.roundName.uppercase(), fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = FlameOrange)
                            Text(if (m.isCompleted) "WINNER: ${m.winnerName}" else "INCOMPLETE", fontSize = 11.sp, color = if (m.isCompleted) NeonGreen else RadiantGold, fontWeight = FontWeight.Bold)
                        }
                        Text("${m.player1Name} (${m.player1Score}) vs ${m.player2Name} (${m.player2Score})", fontSize = 14.sp, color = TextPrimary)
                    }
                }
            }
        }
    }

    // Edit Match Dialog
    if (selectedMatchToEdit != null && selectedTourney != null) {
        val m = selectedMatchToEdit!!
        val t = selectedTourney!!
        var s1 by remember { mutableStateOf(m.player1Score.toString()) }
        var s2 by remember { mutableStateOf(m.player2Score.toString()) }
        var winnerChoice by remember { mutableStateOf(m.player1Name) }

        AlertDialog(
            onDismissRequest = { selectedMatchToEdit = null },
            title = {
                Text("UPDATE ${m.roundName.uppercase()}", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = FlameOrange)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = s1,
                            onValueChange = { s1 = it },
                            label = { Text("${m.player1Name} Score") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = s2,
                            onValueChange = { s2 = it },
                            label = { Text("${m.player2Name} Score") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text("SELECT ADVANCING WINNER:", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { winnerChoice = m.player1Name },
                            colors = ButtonDefaults.buttonColors(containerColor = if (winnerChoice == m.player1Name) FlameOrange else EsportsSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(m.player1Name, fontSize = 11.sp)
                        }
                        Button(
                            onClick = { winnerChoice = m.player2Name },
                            colors = ButtonDefaults.buttonColors(containerColor = if (winnerChoice == m.player2Name) FlameOrange else EsportsSurfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(m.player2Name, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p1 = s1.toIntOrNull() ?: 0
                        val p2 = s2.toIntOrNull() ?: 0
                        viewModel.adminUpdateLoneWolfWinner(t.id, m.id, p1, p2, winnerChoice)
                        selectedMatchToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Text("SAVE & ADVANCE")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMatchToEdit = null }) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            containerColor = EsportsSurface,
            shape = RoundedCornerShape(14.dp)
        )
    }
}

// -------------------------------------------------------------------------------------
// 6. RESULT SUBMISSION AUDIT & REVIEW (Requirement 13 & 20)
// -------------------------------------------------------------------------------------
@Composable
fun SubmissionsReviewTab(
    submissions: List<ResultSubmission>,
    viewModel: TournamentViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "PLAYER EVIDENCE SUBMISSIONS (${submissions.size})",
                fontFamily = RajdhaniFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = RadiantGold
            )
        }

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
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(sub.teamName, fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                        Text(sub.status.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RadiantGold)
                    }

                    Text("Tournament: ${sub.tournamentTitle} • Round #${sub.roundNumber}", fontSize = 12.sp, color = FlameOrange)
                    Text("Claim: Placement #${sub.claimedPlacement} • ${sub.claimedEliminations} Eliminations", fontSize = 12.sp, color = TextPrimary)
                    Text("Screenshot note: ${sub.screenshotNote}", fontSize = 11.sp, color = TextSecondary)

                    if (sub.status == SubmissionStatus.SUBMITTED || sub.status == SubmissionStatus.UNDER_REVIEW) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.adminReviewSubmission(sub.id, approved = true, note = "Verified and approved by referee.")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("APPROVE & CREDIT PTS", fontWeight = FontWeight.Bold, color = Color.Black)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.adminReviewSubmission(sub.id, approved = false, note = "Screenshot invalid or mismatched stats.")
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("REJECT", color = DangerCrimson)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 7. FAIR PLAY & MODERATION SYSTEM (Requirement 19 & 20)
// -------------------------------------------------------------------------------------
@Composable
fun FairPlayTab(
    tournaments: List<Tournament>,
    actions: List<ModerationAction>,
    viewModel: TournamentViewModel
) {
    var selectedTournament by remember { mutableStateOf(tournaments.firstOrNull()) }
    var targetTeamName by remember { mutableStateOf("PHANTOM GANG") }
    var selectedModType by remember { mutableStateOf(ModerationType.PENALTY_POINTS) }
    var penaltyPoints by remember { mutableStateOf("5") }
    var reasonInput by remember { mutableStateOf("Late joining / rule infraction") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EsportsCard),
                border = BorderStroke(1.dp, DangerCrimson.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("ISSUE FAIR PLAY MODERATION ACTION", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = DangerCrimson)

                    OutlinedTextField(
                        value = targetTeamName,
                        onValueChange = { targetTeamName = it },
                        label = { Text("Target Team or Player IGN") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ModerationType.values().forEach { type ->
                            val isSel = selectedModType == type
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) FlameOrange else EsportsSurfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedModType = type }
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
                                    Text(type.label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else TextSecondary)
                                }
                            }
                        }
                    }

                    if (selectedModType == ModerationType.PENALTY_POINTS) {
                        OutlinedTextField(
                            value = penaltyPoints,
                            onValueChange = { penaltyPoints = it },
                            label = { Text("Penalty Points to Deduct") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OutlinedTextField(
                        value = reasonInput,
                        onValueChange = { reasonInput = it },
                        label = { Text("Official Reason & Evidence Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val pts = penaltyPoints.toIntOrNull() ?: 5
                            viewModel.adminApplyModeration(
                                selectedTournament?.id ?: "t_br_squad_1",
                                targetTeamName,
                                selectedModType,
                                pts,
                                reasonInput
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerCrimson),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("APPLY PENALTY / ACTION", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("AUDIT LOG: RECENT MODERATION ACTIONS", fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, color = RadiantGold)
        }

        items(actions) { action ->
            Card(
                colors = CardDefaults.cardColors(containerColor = EsportsCard),
                border = BorderStroke(1.dp, EsportsCardBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(action.targetName, fontFamily = RajdhaniFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        Text(action.actionType.label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DangerCrimson)
                    }
                    Text("Tournament: ${action.tournamentTitle}", fontSize = 11.sp, color = TextMuted)
                    Text("Reason: ${action.reason}", fontSize = 12.sp, color = TextSecondary)
                    Text("Enforced by ${action.adminName} on ${action.date}", fontSize = 10.sp, color = TextMuted)
                }
            }
        }
    }
}
