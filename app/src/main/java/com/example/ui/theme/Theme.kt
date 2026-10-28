package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = LuxuryBlack,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = EmeraldLight,
    onSecondary = Color.White,
    secondaryContainer = EmeraldContainer,
    onSecondaryContainer = TextSilver,
    tertiary = GoldLight,
    onTertiary = LuxuryBlack,
    background = LuxuryDarkBg,
    onBackground = TextWhite,
    surface = LuxurySurface,
    onSurface = TextWhite,
    surfaceVariant = LuxurySurfaceElevated,
    onSurfaceVariant = TextSilver,
    outline = LuxurySurfaceBorder,
    error = LossRed,
    onError = Color.White
)

private val LightColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = LuxuryBlack,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = EmeraldLight,
    onSecondary = Color.White,
    background = LuxuryDarkBg,
    onBackground = TextWhite,
    surface = LuxurySurface,
    onSurface = TextWhite,
    surfaceVariant = LuxurySurfaceElevated,
    onSurfaceVariant = TextSilver,
    outline = LuxurySurfaceBorder,
    error = LossRed,
    onError = Color.White
)

@Composable
fun JewelleryProfitMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Both dark and light use the opulent jewellery dark theme to honor user's request for
    // "a luxurious gold, black, white and dark green theme"
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
