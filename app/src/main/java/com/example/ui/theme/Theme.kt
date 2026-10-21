package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MellatRedPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = MellatGoldDark,
    onSecondary = Color.White,
    secondaryContainer = MellatGoldLight,
    onSecondaryContainer = Color(0xFF412D00),
    tertiary = GreenSuccess,
    onTertiary = Color.White,
    tertiaryContainer = GreenSuccessLight,
    onTertiaryContainer = Color(0xFF003921),
    background = SoftBackground,
    onBackground = TextPrimaryDark,
    surface = CardSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFFF1F3F5),
    onSurfaceVariant = TextSecondaryMuted,
    outline = CardBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkMellatRed,
    onPrimary = Color.White,
    primaryContainer = DarkMellatRedContainer,
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = DarkMellatGold,
    onSecondary = Color.Black,
    secondaryContainer = DarkMellatGoldContainer,
    onSecondaryContainer = Color(0xFFFFDE9C),
    tertiary = GreenSuccess,
    onTertiary = Color.Black,
    tertiaryContainer = DarkGreenSuccessContainer,
    onTertiaryContainer = Color(0xFFB4FAD4),
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
