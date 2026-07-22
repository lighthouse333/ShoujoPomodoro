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

// ═══════════════════════════════════════════════
//  Light Color Scheme — 春日午后花园
// ═══════════════════════════════════════════════
private val LightColorScheme = lightColorScheme(
    primary = SakuraDeep,
    onPrimary = Color.White,
    primaryContainer = SakuraGlow,
    onPrimaryContainer = Color(0xFF880E4F),

    secondary = WisteriaDeep,
    onSecondary = Color.White,
    secondaryContainer = WisteriaGlow,
    onSecondaryContainer = Color(0xFF4A148C),

    tertiary = SkyBlue,
    onTertiary = Color(0xFF003541),
    tertiaryContainer = IceBlue,
    onTertiaryContainer = Color(0xFF014361),

    background = LightPinkBg,
    onBackground = Color(0xFF1C1B1F),
    surface = LightCard,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF5F0F5),
    onSurfaceVariant = Color(0xFF49454F),

    outline = WisteriaLavender.copy(alpha = 0.6f),
    outlineVariant = WisteriaLight.copy(alpha = 0.4f),

    error = CoralRose,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A),
)

// ═══════════════════════════════════════════════
//  Dark Color Scheme — 星夜魔法少女
// ═══════════════════════════════════════════════
private val DarkColorScheme = darkColorScheme(
    primary = SakuraPink,
    onPrimary = Color(0xFF650033),
    primaryContainer = Color(0xFF8E0045),
    onPrimaryContainer = SakuraLight,

    secondary = WisteriaLavender,
    onSecondary = Color(0xFF35005A),
    secondaryContainer = Color(0xFF5C00AA),
    onSecondaryContainer = WisteriaGlow,

    tertiary = MermaidBlue,
    onTertiary = Color(0xFF003541),
    tertiaryContainer = Color(0xFF014361),
    onTertiaryContainer = IceBlue,

    background = DarkNavy,
    onBackground = Color(0xFFE6E1E5),
    surface = DarkIndigo,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = DarkCard,
    onSurfaceVariant = Color(0xFFCAC4D0),

    outline = WisteriaLavender.copy(alpha = 0.3f),
    outlineVariant = WisteriaDeep.copy(alpha = 0.2f),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
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
            // Edge-to-edge with transparent system bars
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ShoujoTypography,
        shapes = ShoujoShapes,
        content = content
    )
}
