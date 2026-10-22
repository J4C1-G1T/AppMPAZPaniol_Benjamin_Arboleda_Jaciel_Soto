package com.proyecto.appmpazpaniol.ui.theme

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
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Color(0xFF003733),
    primaryContainer = Color(0xFF00504B),
    onPrimaryContainer = Color(0xFFB9DCD8),
    secondary = Pink80,
    onSecondary = Color(0xFF4A2509),
    secondaryContainer = Color(0xFF653A1D),
    onSecondaryContainer = Color(0xFFFFDBCA),
    tertiary = Color(0xFFB3C9FF),
    background = Color(0xFF101918),
    surface = Color(0xFF182321),
    surfaceVariant = Color(0xFF3F4947),
    onSurfaceVariant = Color(0xFFBEC9C6)
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB9EEE8),
    onPrimaryContainer = Color(0xFF00201D),
    secondary = Pink40,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBCA),
    onSecondaryContainer = Color(0xFF351000),
    tertiary = Color(0xFF425E91),
    background = Color(0xFFEEF4F1),
    surface = Color(0xFFFCFFFD),
    surfaceVariant = Color(0xFFDCE5E2),
    onSurfaceVariant = Color(0xFF404946)
)

@Composable
fun AppMPAZPaniolTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}