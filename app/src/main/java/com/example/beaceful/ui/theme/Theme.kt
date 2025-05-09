package com.example.beaceful.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Pink40,
    secondary = Pink80,
    tertiary = Pink40,
)

private val LightColorScheme = lightColorScheme(
    primary = Color.White,
    secondary = Purple500,
    tertiary = Purple200,

//    Other default colors to override
    background = Purple700,
    surface = Purple700,
    onPrimary = Purple700,
    onSecondary = Color.White,
    onTertiary = Purple700,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),

)

@Composable
fun BeacefulTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}