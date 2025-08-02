package components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.util.fastForEachIndexed
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun BoxScope.AudioVisualize(
    modifier: Modifier = Modifier,
    barData: List<Float>,
    gap: Float = 20f,
    barWidth: Float = 10f,
    height: Float = 100f,
    barColor: Color
) {
    val animatables = remember(barData.size) {
        List(barData.size) { Animatable(0f) }
    }
    val oldData = remember { mutableStateOf(barData) }

    LaunchedEffect(barData) {

        barData.forEachIndexed { index, value ->
            launch {
                if (value > 0) {
                    animatables[index].animateTo(
                        targetValue = value, animationSpec = tween(
                            delayMillis = 0,
                            durationMillis = abs(((oldData.value[index] - value) / 40) * 300).toInt()
                        )
                    )
                }
                animatables[index].animateTo(
                    targetValue = 0f, animationSpec = tween(
                        delayMillis = abs((value / 40) * 200).toInt(),
                        durationMillis = abs((value / 40) * 500).toInt(),
                        easing = LinearEasing
                    )
                )
                oldData.value = barData
            }
        }
    }
    Canvas(modifier = modifier.align(Alignment.Center)) {
        animatables.fastForEachIndexed { index, animatable ->
            drawLine(
                color = barColor,
                start = Offset(gap * index, -animatable.value / 2),
                end = Offset(gap * index, animatable.value),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}