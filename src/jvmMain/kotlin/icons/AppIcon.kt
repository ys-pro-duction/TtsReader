package icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val AppIcon: ImageVector
    get() {
        if (_AppIcon != null) {
            return _AppIcon!!
        }
        _AppIcon = ImageVector.Builder(
            name = "AppIcon",
            defaultWidth = 15.64.dp,
            defaultHeight = 12.5.dp,
            viewportWidth = 15.64f,
            viewportHeight = 12.5f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(0.457f, 5.825f)
                lineTo(3.457f, 5.825f)
                curveTo(3.657f, 5.825f, 3.657f, 5.725f, 3.657f, 5.525f)
                lineTo(3.657f, 4.625f)
                curveTo(3.957f, 3.125f, 5.757f, 3.125f, 6.057f, 4.625f)
                lineTo(6.057f, 11.025f)
                curveTo(6.157f, 11.725f, 7.057f, 11.725f, 7.157f, 11.025f)
                lineTo(7.157f, 1.425f)
                curveTo(7.457f, -0.475f, 9.757f, -0.475f, 10.057f, 1.425f)
                lineTo(10.057f, 5.625f)
                curveTo(10.157f, 6.858f, 10.057f, 8.125f, 10.357f, 9.325f)
                curveTo(10.457f, 9.825f, 10.857f, 9.925f, 10.957f, 9.325f)
                lineTo(11.957f, 4.325f)
                curveTo(12.257f, 3.225f, 13.257f, 3.425f, 13.457f, 4.325f)
                lineTo(13.857f, 6.525f)
                curveTo(13.897f, 6.715f, 14.027f, 6.825f, 14.087f, 6.515f)
                curveTo(14.167f, 6.125f, 14.557f, 5.725f, 15.237f, 5.795f)
                curveTo(15.767f, 5.865f, 15.777f, 6.515f, 15.227f, 6.535f)
                curveTo(15.007f, 6.545f, 14.957f, 6.535f, 14.827f, 6.675f)
                lineTo(14.647f, 7.095f)
                curveTo(14.247f, 7.895f, 13.447f, 7.765f, 13.247f, 7.095f)
                lineTo(12.737f, 5.145f)
                curveTo(12.707f, 4.975f, 12.697f, 4.965f, 12.647f, 5.145f)
                curveTo(12.347f, 6.605f, 12.047f, 8.065f, 11.747f, 9.525f)
                curveTo(11.407f, 10.935f, 9.907f, 10.955f, 9.517f, 9.515f)
                curveTo(9.317f, 8.495f, 9.227f, 7.395f, 9.147f, 6.345f)
                lineTo(9.137f, 1.645f)
                curveTo(9.107f, 0.725f, 8.087f, 0.815f, 8.107f, 1.655f)
                lineTo(8.127f, 11.095f)
                curveTo(7.957f, 12.985f, 5.167f, 12.965f, 5.127f, 11.075f)
                lineTo(5.117f, 4.885f)
                curveTo(5.117f, 4.405f, 4.567f, 4.425f, 4.567f, 4.895f)
                lineTo(4.557f, 5.875f)
                curveTo(4.537f, 6.275f, 4.247f, 6.565f, 3.867f, 6.635f)
                lineTo(0.457f, 6.635f)
                curveTo(-0.253f, 6.575f, -0.043f, 5.815f, 0.457f, 5.825f)
            }
        }.build()

        return _AppIcon!!
    }

@Suppress("ObjectPropertyName")
private var _AppIcon: ImageVector? = null
