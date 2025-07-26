@file:OptIn(ExperimentalMaterial3Api::class)

package components

import BaseViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.Modifier as Modi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun VolumeSpeedControlls(modifier: Modi = Modifier.width(200.dp).padding(horizontal = 16.dp), baseViewModel: BaseViewModel) {
    Column(modifier = modifier) {
        VolumeSlider(baseViewModel)
        SpeedSlider(baseViewModel)
    }
}

@Composable
private fun MySlider(
    modifier: Modi = Modifier,
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
        })
}
fun Modi.mouseScrollable(onScroll: (Float) -> Unit): Modi = composed {
    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                val scrollDelta = event.changes.firstOrNull()?.scrollDelta?.y ?: 0f
                if (scrollDelta != 0f) {
                    onScroll(scrollDelta * -1)
                }
            }
        }
    }
}


@Composable
private fun ColumnScope.VolumeSlider(baseViewModel: BaseViewModel){
    val volume = baseViewModel.volume.collectAsState()
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
            { baseViewModel.updateVolume(it) },
            valueRange = (0f..100f),
            valueFormat = "%.0f",
            onMouseValueChange = {
                val newValue = volume.value + it * 5
                if (newValue in 0f..100f) {
                    baseViewModel.updateVolume(newValue)
                } else if (newValue <= 5f) baseViewModel.updateVolume(0f)
                else baseViewModel.updateVolume(100f)
            })
    }
}
@Composable
private fun ColumnScope.SpeedSlider(baseViewModel: BaseViewModel){
    val speed = baseViewModel.speechSpeed.collectAsState()
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
            { baseViewModel.updateSpeed(it) },
            valueRange = (0.2f..3f),
            valueFormat = "%.1f",
            onMouseValueChange = {
                val newValue = speed.value + it * 0.1f
                if (newValue in 0.2f..3f) {
                    baseViewModel.updateSpeed(newValue)
                } else if (newValue <= 0.2f) baseViewModel.updateSpeed(0.2f)
                else baseViewModel.updateSpeed(3f)
            })
    }
}