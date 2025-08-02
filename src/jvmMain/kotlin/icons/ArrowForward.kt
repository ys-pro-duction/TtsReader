package icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ArrowForward: ImageVector
    get() {
        if (_arrowForward != null) {
            return _arrowForward!!
        }
        _arrowForward = ImageVector.Builder(
            name = "IconName",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE3E3E3))) {
                moveTo(647f, 520f)
                lineTo(160f, 520f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(487f)
                lineTo(423f, 216f)
                lineToRelative(57f, -56f)
                lineToRelative(320f, 320f)
                lineToRelative(-320f, 320f)
                lineToRelative(-57f, -56f)
                lineToRelative(224f, -224f)
                close()
            }
        }.build()

        return _arrowForward!!
    }

@Suppress("ObjectPropertyName")
private var _arrowForward: ImageVector? = null
