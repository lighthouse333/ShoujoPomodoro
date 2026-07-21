package com.shoujopomodoro.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Pink40,
    onPrimary = Color.White,
    primaryContainer = PinkLight,
    onPrimaryContainer = Color(0xFF880E4F),
    secondary = Purple40,
    onSecondary = Color.White,
    secondaryContainer = Purple80,
    onSecondaryContainer = Color(0xFF4A148C),
    background = SurfacePink,
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E)
)

private val DarkColorScheme = darkColorScheme(
    primary = Pink80,
    onPrimary = Color(0xFF650033),
    primaryContainer = Color(0xFF8E0045),
    onPrimaryContainer = PinkLight,
    secondary = Purple80,
    onSecondary = Color(0xFF35005A),
    secondaryContainer = Color(0xFF5C00AA),
    onSecondaryContainer = Color(0xFFEADDFF),
    background = SurfaceDark,
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF252535),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF333345),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99)
)

@Composable
fun ShoujoPomodoroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
