package theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlin.Unit

val LightColorPalette = lightColors(
    primary = Color(0xFF000000), // Black
    primaryVariant = Color(0xFF000000), // Black (default variant)
    secondary = Color(0xFFE6E6FA), // Lavender
    secondaryVariant = Color(0xFFE6E6FA), // Lavender (default variant)
    background = Color(0xFFFFE4E1), // MistyRose
    surface = Color(0xFFFFFFFF), // White
    onPrimary = Color(0xFFFFFFFF), // White
    onSecondary = Color(0xFFD8BFD8), // Thistle
    onBackground = Color(0xFFD8BFD8), // Thistle (default)
    onSurface = Color(0xFF000000) // Black (default)
)

private val DarkColorPalette = darkColors(
    primary = Color(0xFFFFFFFF), // White
    primaryVariant = Color(0xFFFFFFFF), // White (default variant)
    secondary = Color(0xFF4B0082), // Indigo
    secondaryVariant = Color(0xFF4B0082), // Indigo (default variant)
    background = Color(0xFF1E90FF), // DodgerBlue
    surface = Color(0xFF000000), // Black
    onPrimary = Color(0xFF4B0082), // Indigo
    onSecondary = Color(0xFF4B0082), // Indigo (default)
    onBackground = Color(0xFF4B0082), // Indigo (default)
    onSurface = Color(0xFF000000) // Black (default)
)

@Composable
fun TTSReaderTheme(
    isDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme) DarkColorPalette else LightColorPalette
    MaterialTheme(
        colors = colors,
        content = content
    )
}