package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NoirColorScheme = darkColorScheme(
    primary = PoliceCyan,
    onPrimary = NoirObsidian,
    primaryContainer = NoirCard,
    onPrimaryContainer = PoliceCyan,
    secondary = BadgeGold,
    onSecondary = NoirObsidian,
    secondaryContainer = Color(0xFF2E2610),
    onSecondaryContainer = BadgeGold,
    tertiary = SirenCrimson,
    onTertiary = Color.White,
    background = NoirObsidian,
    onBackground = TextPrimary,
    surface = NoirDeepNavy,
    onSurface = TextPrimary,
    surfaceVariant = NoirSurface,
    onSurfaceVariant = TextSecondary,
    outline = NoirBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NoirColorScheme,
        typography = Typography,
        content = content
    )
}
