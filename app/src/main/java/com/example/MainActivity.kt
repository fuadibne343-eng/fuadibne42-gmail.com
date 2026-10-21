package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppScreen
import com.example.ui.components.AppTopBar
import com.example.ui.components.BannerToastMessage
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.TournamentViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TournamentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScaffold(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScaffold(viewModel: TournamentViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val unreadNotifCount by viewModel.unreadNotificationCount.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val selectedTournamentId by viewModel.selectedTournamentId.collectAsState()

    // Handle back button for secondary screens
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        if (currentScreen == AppScreen.ADMIN_DASHBOARD || currentScreen == AppScreen.NOTIFICATIONS || currentScreen == AppScreen.TOURNAMENT_DETAIL || currentScreen == AppScreen.TEAM_MANAGEMENT) {
            viewModel.navigateTo(AppScreen.HOME)
        } else {
            viewModel.navigateTo(AppScreen.HOME)
        }
    }

    val isSubScreen = currentScreen == AppScreen.ADMIN_DASHBOARD ||
            currentScreen == AppScreen.NOTIFICATIONS ||
            currentScreen == AppScreen.TOURNAMENT_DETAIL ||
            currentScreen == AppScreen.TEAM_MANAGEMENT

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground),
        topBar = {
            if (currentScreen != AppScreen.ADMIN_DASHBOARD) {
                AppTopBar(
                    currentScreen = currentScreen,
                    isAdmin = currentUser.isAdmin,
                    unreadNotifCount = unreadNotifCount,
                    onAdminToggle = { viewModel.toggleAdminMode() },
                    onNotifClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) },
                    onBackClick = if (isSubScreen) {
                        { viewModel.navigateTo(AppScreen.HOME) }
                    } else null
                )
            }
        },
        bottomBar = {
            if (!isSubScreen) {
                EsportsBottomNavBar(
                    currentScreen = currentScreen,
                    onScreenSelected = { viewModel.navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToTournament = { id ->
                            viewModel.navigateTo(AppScreen.TOURNAMENT_DETAIL, id)
                        },
                        onNavigateToMatchCenter = { viewModel.navigateTo(AppScreen.MATCHES) },
                        onNavigateToAdmin = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
                    )
                }
                AppScreen.TOURNAMENTS -> {
                    TournamentsScreen(
                        viewModel = viewModel,
                        onNavigateToTournament = { id ->
                            viewModel.navigateTo(AppScreen.TOURNAMENT_DETAIL, id)
                        }
                    )
                }
                AppScreen.MATCHES -> {
                    MatchCenterScreen(
                        viewModel = viewModel,
                        onNavigateToTournament = { id ->
                            viewModel.navigateTo(AppScreen.TOURNAMENT_DETAIL, id)
                        }
                    )
                }
                AppScreen.LEADERBOARDS -> {
                    LeaderboardScreen(
                        viewModel = viewModel,
                        onNavigateToTournament = { id ->
                            viewModel.navigateTo(AppScreen.TOURNAMENT_DETAIL, id)
                        }
                    )
                }
                AppScreen.PROFILE -> {
                    ProfileScreen(
                        viewModel = viewModel,
                        onNavigateToAdmin = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) }
                    )
                }
                AppScreen.ADMIN_DASHBOARD -> {
                    AdminDashboardScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppScreen.HOME) }
                    )
                }
                AppScreen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        viewModel = viewModel,
                        onNavigateToTournament = { id ->
                            viewModel.navigateTo(AppScreen.TOURNAMENT_DETAIL, id)
                        },
                        onBack = { viewModel.navigateTo(AppScreen.HOME) }
                    )
                }
                AppScreen.TOURNAMENT_DETAIL -> {
                    TournamentDetailScreen(
                        tournamentId = selectedTournamentId ?: "t_br_squad_1",
                        viewModel = viewModel,
                        onBack = { viewModel.navigateTo(AppScreen.HOME) }
                    )
                }
                AppScreen.TEAM_MANAGEMENT -> {
                    TeamScreen(viewModel = viewModel)
                }
            }

            // Global floating feedback message
            BannerToastMessage(
                message = userMessage,
                onDismiss = { viewModel.clearMessage() }
            )
        }
    }
}

data class NavItem(
    val screen: AppScreen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun EsportsBottomNavBar(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit
) {
    val items = listOf(
        NavItem(AppScreen.HOME, "HOME", Icons.Default.Home, Icons.Outlined.Home),
        NavItem(AppScreen.TOURNAMENTS, "TOURNAMENT", Icons.Default.EmojiEvents, Icons.Outlined.EmojiEvents),
        NavItem(AppScreen.MATCHES, "MATCH", Icons.Default.SportsEsports, Icons.Outlined.SportsEsports),
        NavItem(AppScreen.LEADERBOARDS, "LEADERBOARD", Icons.Default.Leaderboard, Icons.Outlined.Leaderboard),
        NavItem(AppScreen.PROFILE, "PROFILE", Icons.Default.Person, Icons.Outlined.Person)
    )

    NavigationBar(
        containerColor = EsportsSurface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .testTag("bottom_nav_bar")
    ) {
        items.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onScreenSelected(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        tint = if (isSelected) FlameOrange else TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontFamily = RajdhaniFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 10.sp,
                        color = if (isSelected) FlameOrange else TextSecondary
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = FlameOrange.copy(alpha = 0.15f)
                ),
                modifier = Modifier.testTag("nav_item_${item.title.lowercase()}")
            )
        }
    }
}
