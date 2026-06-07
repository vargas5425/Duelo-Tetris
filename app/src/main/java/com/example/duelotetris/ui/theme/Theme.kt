package com.example.duelotetris.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00FF00),
    secondary = Color(0xFF00CC00),
    tertiary = Color(0xFF009900)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00AA00),
    secondary = Color(0xFF008800),
    tertiary = Color(0xFF006600)
)

@Composable
fun TetrisDuelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}