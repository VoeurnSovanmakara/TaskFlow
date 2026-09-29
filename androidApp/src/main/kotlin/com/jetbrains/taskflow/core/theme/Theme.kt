package com.jetbrains.taskflow.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Colors ----------
// Palette: #222831 charcoal, #393E46 slate, #FFD369 gold, #EEEEEE light gray.
// Dark theme = charcoal background, slate cards, gold accent.
// Light theme = light gray background, white cards, charcoal buttons with gold highlights.
// Badge mapping (see TaskBadges.kt): TODO = gray, IN PROGRESS = gold, COMPLETED = green, HIGH = red.

private val LightColors = lightColorScheme(
    primary = Color(0xFF222831),
    onPrimary = Color(0xFFEEEEEE),
    primaryContainer = Color(0xFFFFD369),
    onPrimaryContainer = Color(0xFF222831),
    secondary = Color(0xFF393E46),
    onSecondary = Color(0xFFEEEEEE),
    secondaryContainer = Color(0xFFE0E2E6),
    onSecondaryContainer = Color(0xFF222831),
    tertiary = Color(0xFF2E7D4F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFCDEBD6),
    onTertiaryContainer = Color(0xFF0B2E1B),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFEEEEEE),
    onBackground = Color(0xFF222831),
    surface = Color(0xFFEEEEEE),
    onSurface = Color(0xFF222831),
    surfaceVariant = Color(0xFFDDE0E5),
    onSurfaceVariant = Color(0xFF393E46),
    outline = Color(0xFF6B717A),
    outlineVariant = Color(0xFFC5C9CF),
    inverseSurface = Color(0xFF222831),
    inverseOnSurface = Color(0xFFEEEEEE),
    inversePrimary = Color(0xFFFFD369),
    surfaceTint = Color(0xFF222831),
    surfaceDim = Color(0xFFDCDCDC),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFF8F8F8),
    surfaceContainerHigh = Color(0xFFF3F3F3),
    surfaceContainerHighest = Color(0xFFE6E6E6),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFD369),
    onPrimary = Color(0xFF222831),
    primaryContainer = Color(0xFF5A4A1C),
    onPrimaryContainer = Color(0xFFFFD369),
    secondary = Color(0xFFB9BEC6),
    onSecondary = Color(0xFF222831),
    secondaryContainer = Color(0xFF4E545D),
    onSecondaryContainer = Color(0xFFEEEEEE),
    tertiary = Color(0xFF8FD9A0),
    onTertiary = Color(0xFF0F3A20),
    tertiaryContainer = Color(0xFF2E5A3D),
    onTertiaryContainer = Color(0xFFC9F0D3),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF7A2E2E),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF222831),
    onBackground = Color(0xFFEEEEEE),
    surface = Color(0xFF222831),
    onSurface = Color(0xFFEEEEEE),
    surfaceVariant = Color(0xFF393E46),
    onSurfaceVariant = Color(0xFFC9CCD1),
    outline = Color(0xFF8A9099),
    outlineVariant = Color(0xFF4A5058),
    inverseSurface = Color(0xFFEEEEEE),
    inverseOnSurface = Color(0xFF222831),
    inversePrimary = Color(0xFF7A5C00),
    surfaceTint = Color(0xFFFFD369),
    surfaceDim = Color(0xFF1D2229),
    surfaceBright = Color(0xFF4E545D),
    surfaceContainerLowest = Color(0xFF1D2229),
    surfaceContainerLow = Color(0xFF393E46),
    surfaceContainer = Color(0xFF3F454E),
    surfaceContainerHigh = Color(0xFF454B54),
    surfaceContainerHighest = Color(0xFF4E545D),
)

// ---------- Typography ----------
// Default Material3 scale with heavier weights for headings (no custom font files needed).

private val BaseTypography = Typography()

private val AppTypography = Typography(
    headlineMedium = BaseTypography.headlineMedium.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
    ),
    headlineSmall = BaseTypography.headlineSmall.copy(
        fontWeight = FontWeight.SemiBold,
    ),
    titleLarge = BaseTypography.titleLarge.copy(
        fontWeight = FontWeight.SemiBold,
    ),
    titleMedium = BaseTypography.titleMedium.copy(
        fontWeight = FontWeight.SemiBold,
    ),
    labelLarge = BaseTypography.labelLarge.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.2.sp,
    ),
)

// ---------- Shapes ----------
// Matches the radii used in the screens (12 chips, 16 fields/buttons, 20 cards, 28 dialogs).

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

// ---------- Theme ----------

@Composable
fun TaskFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // false = always use the palette above; true = use the wallpaper colors on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}

// Selected filter chips use the gold container instead of the default gray.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun selectableChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
)