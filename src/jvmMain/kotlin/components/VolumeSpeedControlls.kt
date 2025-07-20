@file:OptIn(ExperimentalMaterial3Api::class)

package components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mouseScrollable

@Composable
fun VolumeSpeedControlls(modifier: Modifier = Modifier) {
    Column(modifier = modifier.width(200.dp).padding(horizontal = 16.dp)) {
        val volume = remember { mutableStateOf(100f) }
        val speed = remember { mutableStateOf(1f) }
        Text(
            "Volume:", color = Color.White, fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            Row(Modifier.fillMaxWidth()) {
                Spacer(
                    modifier = Modifier.weight(volume.value + 10f).height(24.dp).background(
                        Color.White.copy(0.15f),
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                    )
                )
                Spacer(
                    modifier = Modifier.weight(110f - volume.value).height(24.dp).background(
                        Color.White.copy(0.05f),
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                    )
                )
            }

            MySlider(
                Modifier,
                volume.value,
                { volume.value = it },
                valueRange = (0f..100f),
                valueFormat = "%.0f",
                onMouseValueChange = {
                    val newValue = volume.value + it * 5
                    if (newValue in 0f..100f) {
                        volume.value = newValue
                    } else if (newValue <= 5f) volume.value = 0f
                    else volume.value = 100f
                })
        }
        Text(
            "Speech speed:", color = Color.White, fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            Row(Modifier.fillMaxWidth()) {
                Spacer(
                    modifier = Modifier.weight(speed.value + 0.02f).height(24.dp).background(
                        Color.White.copy(0.15f),
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                    )
                )
                Spacer(
                    modifier = Modifier.weight(3.1f - speed.value).height(24.dp).background(
                        Color.White.copy(0.05f),
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                    )
                )
            }
            MySlider(
                Modifier,
                speed.value,
                { speed.value = it },
                valueRange = (0.2f..3f),
                valueFormat = "%.1f",
                onMouseValueChange = {
                    val newValue = speed.value + it * 0.1f
                    if (newValue in 0.2f..3f) {
                        speed.value = newValue
                    } else if (newValue <= 0.2f) speed.value = 0.2f
                    else speed.value = 3f
                })
        }

    }
}

@Composable
private fun MySlider(
    modifier: Modifier = Modifier,
    value: Float = 0f,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    valueFormat: String,
    onMouseValueChange: (Float) -> Unit = {},
    trackState: (SliderState) -> Unit = {}
) {
    Slider(
        value,
        onValueChange,
        valueRange = valueRange,
        modifier = Modifier.mouseScrollable(onMouseValueChange),
        thumb = {
            Text(
                String.format(null, valueFormat, value),
                color = Color.White,
                modifier = Modifier.size(28.dp, 26.dp)
                    .background(Color(0xff575757), RoundedCornerShape(4.dp)),
                textAlign = TextAlign.Center
            )
        },
        track = {
            Box(modifier = Modifier.fillMaxWidth())
//            Row(Modifier.fillMaxWidth()) {
//                Spacer(
//                    modifier = Modifier.weight(it.value + 0.01f).height(24.dp).background(
//                        Color.White.copy(0.15f),
//                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
//                    )
//                )
//                Spacer(
//                    modifier = Modifier.weight(it.valueRange.endInclusive + 0.01f - it.value)
//                        .height(1.dp)
//                )
//            }
        })
}
