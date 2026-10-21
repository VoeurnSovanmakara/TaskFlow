package com.jetbrains.taskflow.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
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
// Palette: "Kinetic Focus" — #5B4DFF primary (indigo-violet), #8B5CF6 secondary (purple),
// #10B981 tertiary (emerald), #64748B neutral (slate, used for outlines/surfaces).
// Badge mapping (see TaskBadges.kt): TODO = purple, IN PROGRESS = indigo, COMPLETED = emerald, HIGH = red.

private val LightColors = lightColorScheme(
    primary = Color(0xFF5B4DFF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE1DEFF),
    onPrimaryContainer = Color(0xFF190085),
    secondary = Color(0xFF8B5CF6),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEDE1FE),
    onSecondaryContainer = Color(0xFF2C0A73),
    tertiary = Color(0xFF10B981),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC3F3DE),
    onTertiaryContainer = Color(0xFF00210F),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF8F8FC),
    onBackground = Color(0xFF1B1B23),
    surface = Color(0xFFF8F8FC),
    onSurface = Color(0xFF1B1B23),
    surfaceVariant = Color(0xFFE2E4EF),
    onSurfaceVariant = Color(0xFF45464F),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFFC7CAD6),
    inverseSurface = Color(0xFF2F3038),
    inverseOnSurface = Color(0xFFF1F0F7),
    inversePrimary = Color(0xFFC5C0FF),
    surfaceTint = Color(0xFF5B4DFF),
    surfaceDim = Color(0xFFD9D9E3),
    surfaceBright = Color(0xFFF8F8FC),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF2F1F9),
    surfaceContainer = Color(0xFFECEBF3),
    surfaceContainerHigh = Color(0xFFE6E5EE),
    surfaceContainerHighest = Color(0xFFE0DFE8),
)
private val DarkColors = darkColorScheme(
    primary = Color(0xFFC5C0FF),
    onPrimary = Color(0xFF2A1B9E),
    primaryContainer = Color(0xFF4232C7),
    onPrimaryContainer = Color(0xFFE1DEFF),
    secondary = Color(0xFFD3BBFC),
    onSecondary = Color(0xFF3E1C82),
    secondaryContainer = Color(0xFF6A3DD1),
    onSecondaryContainer = Color(0xFFEDE1FE),
    tertiary = Color(0xFF7CE0B4),
    onTertiary = Color(0xFF00391E),
    tertiaryContainer = Color(0xFF00522E),
    onTertiaryContainer = Color(0xFFC3F3DE),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF131318),
    onBackground = Color(0xFFE4E3EC),
    surface = Color(0xFF131318),
    onSurface = Color(0xFFE4E3EC),
    surfaceVariant = Color(0xFF45464F),
    onSurfaceVariant = Color(0xFFC7C5D0),
    outline = Color(0xFF919099),
    outlineVariant = Color(0xFF45464F),
    inverseSurface = Color(0xFFE4E3EC),
    inverseOnSurface = Color(0xFF2F3038),
    inversePrimary = Color(0xFF5B4DFF),
    surfaceTint = Color(0xFFC5C0FF),
    surfaceDim = Color(0xFF131318),
    surfaceBright = Color(0xFF39393F),
    surfaceContainerLowest = Color(0xFF0E0E13),
    surfaceContainerLow = Color(0xFF1B1B23),
    surfaceContainer = Color(0xFF1F1F27),
    surfaceContainerHigh = Color(0xFF2A2A32),
    surfaceContainerHighest = Color(0xFF35353D),
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

@Composable
fun selectableChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
)