package theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette: Colors = lightColors(
    primary = Color(4278190080L),
    secondary = Color(4292927712L),
    background = Color(4294309365L),
    surface = Color(4294967295L),
    onPrimary = Color(4294967295L),
    onSecondary = Color(4292467161L),
)

private val DarkColorPalette: Colors = darkColors(
    primary = Color(4294967295L),
    secondary = Color(4281150765L),
    background = Color(4279571733L),
    surface = Color(4278190080L),
    onPrimary = Color(4281150765L),
    onSecondary = Color(4281150765L),
)

@Composable
fun TTSReaderTheme(
    isDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colors = if (isDarkTheme) DarkColorPalette else LightColorPalette
    MaterialTheme(
        colors = colors,
        content = content
    )
}