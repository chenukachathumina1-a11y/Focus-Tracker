package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FocusColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = FocusObsidianDark,
    primaryContainer = FocusSurfaceElevated,
    onPrimaryContainer = NeonCyan,
    secondary = ElectricIndigo,
    onSecondary = TextPrimary,
    secondaryContainer = FocusSurfaceVariant,
    onSecondaryContainer = SkyBlue,
    tertiary = RadiantEmerald,
    onTertiary = FocusObsidianDark,
    background = FocusObsidianDark,
    onBackground = TextPrimary,
    surface = FocusSurface,
    onSurface = TextPrimary,
    surfaceVariant = FocusSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = FocusOutline,
    outlineVariant = FocusSurfaceElevated,
    error = RadiantRose
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = FocusObsidianDark.toArgb()
            window.navigationBarColor = FocusObsidianDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = FocusColorScheme,
        typography = Typography,
        content = content
    )
}
