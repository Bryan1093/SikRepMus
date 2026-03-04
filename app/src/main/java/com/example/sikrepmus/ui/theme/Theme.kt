package com.example.sikrepmus.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = BlackBackground,
    secondary = LightGrey,
    onSecondary = TextPrimary,
    tertiary = AccentBlue,
    background = BlackBackground,
    surface = SurfaceGrey,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = LightGrey,
    error = ErrorColor
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    secondary = Color(0xFFE0E0E0),
    background = Color.White,
    surface = Color.White,
    onSurface = Color.Black,
    onSurfaceVariant = Color.DarkGray
)

@Composable
fun SikRepMusTheme(
    darkTheme: Boolean = true, // Force dark theme for music apps usually looks better
    dynamicColor: Boolean = false, // Disable dynamic color to maintain the "SikRep" branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        window.statusBarColor = colorScheme.background.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}