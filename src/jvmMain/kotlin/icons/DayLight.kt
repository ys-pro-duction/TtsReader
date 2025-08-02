package icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val DayLight: ImageVector
    get() {
        if (_DayLight != null) {
            return _DayLight!!
        }
        _DayLight = ImageVector.Builder(
            name = "DayLight",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE3E3E3))) {
                moveTo(480f, 680f)
                quadToRelative(-83f, 0f, -141.5f, -58.5f)
                reflectiveQuadTo(280f, 480f)
                quadToRelative(0f, -83f, 58.5f, -141.5f)
                reflectiveQuadTo(480f, 280f)
                quadToRelative(83f, 0f, 141.5f, 58.5f)
                reflectiveQuadTo(680f, 480f)
                quadToRelative(0f, 83f, -58.5f, 141.5f)
                reflectiveQuadTo(480f, 680f)
                close()
                moveTo(200f, 520f)
                lineTo(40f, 520f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(80f)
                close()
                moveTo(920f, 520f)
                lineTo(760f, 520f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(160f)
                verticalLineToRelative(80f)
                close()
                moveTo(440f, 200f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(160f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(440f, 920f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(160f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(256f, 310f)
                lineToRelative(-101f, -97f)
                lineToRelative(57f, -59f)
                lineToRelative(96f, 100f)
                lineToRelative(-52f, 56f)
                close()
                moveTo(748f, 806f)
                lineTo(651f, 705f)
                lineTo(704f, 650f)
                lineTo(805f, 747f)
                lineTo(748f, 806f)
                close()
                moveTo(650f, 256f)
                lineTo(747f, 155f)
                lineTo(806f, 212f)
                lineTo(706f, 308f)
                lineTo(650f, 256f)
                close()
                moveTo(154f, 748f)
                lineToRelative(101f, -97f)
                lineToRelative(55f, 53f)
                lineToRelative(-97f, 101f)
                lineToRelative(-59f, -57f)
                close()
            }
        }.build()

        return _DayLight!!
    }

@Suppress("ObjectPropertyName")
private var _DayLight: ImageVector? = null
