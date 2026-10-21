package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = FlameOrange,
    onPrimary = OnPrimary,
    primaryContainer = EsportsSurfaceVariant,
    onPrimaryContainer = RadiantGold,
    secondary = RadiantGold,
    onSecondary = OnSecondary,
    secondaryContainer = EsportsCard,
    onSecondaryContainer = RadiantGold,
    tertiary = CyberCyan,
    onTertiary = OnSecondary,
    background = EsportsBackground,
    onBackground = TextPrimary,
    surface = EsportsSurface,
    onSurface = TextPrimary,
    surfaceVariant = EsportsSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = EsportsCardBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
