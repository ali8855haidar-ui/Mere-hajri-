package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = MintCardTint,
    onPrimaryContainer = EmeraldDeep,
    secondary = AmberWarm,
    onSecondary = Color.White,
    secondaryContainer = AmberTint,
    onSecondaryContainer = AmberDarkInk,
    tertiary = TealLeave,
    onTertiary = Color.White,
    tertiaryContainer = TealTint,
    onTertiaryContainer = Color(0xFF084C61),
    error = CrimsonAbsent,
    onError = Color.White,
    errorContainer = CrimsonTint,
    onErrorContainer = Color(0xFF8C1D1D),
    background = MintSurface,
    onBackground = ForestInk,
    surface = Color.White,
    onSurface = ForestInk,
    surfaceVariant = MintCardTint,
    onSurfaceVariant = SageMuted,
    outline = MintBorder,
    outlineVariant = Color(0xFFE1ECE4)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkEmeraldPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkEmeraldContainer,
    onPrimaryContainer = Color(0xFFA7F3C7),
    secondary = Color(0xFFFBBF24),
    onSecondary = Color(0xFF422006),
    secondaryContainer = Color(0xFF54360B),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = Color(0xFF38BDF8),
    onTertiary = Color(0xFF082F49),
    tertiaryContainer = Color(0xFF0C4A6E),
    onTertiaryContainer = Color(0xFFBAE6FD),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA),
    background = DarkPineBg,
    onBackground = DarkMintInk,
    surface = DarkPineSurface,
    onSurface = DarkMintInk,
    surfaceVariant = DarkPineSurfaceVariant,
    onSurfaceVariant = DarkSageMuted,
    outline = DarkBorder,
    outlineVariant = Color(0xFF21352A)
)

private val MeriHajiriShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MeriHajiriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = MeriHajiriShapes,
        content = content
    )
}
