package components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState
import kotlin.random.Random
import kotlin.random.nextInt
import androidx.compose.ui.Modifier as Modi

val Modifier
    get() = Modi
//        .border(
//            Random.nextInt(1..2).dp,
//        Color(Random.nextInt(100..255), Random.nextInt(100..255), Random.nextInt(100..255),Random.nextInt(100..200))
//    )

@Composable
fun BoxScope.Controller(modifier: Modi) {
    val speakerOffset = remember { mutableStateOf(Offset.Zero) }
    Row(
        modifier = modifier.height(150.dp).padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        PlaybackController(
            modifier = Modifier.widthIn(200.dp, 300.dp),
            onGeneratePlayClick = {},
            isPlaying = false,
            onResetClick = {},
            onPreviousClick = {},
            onNextClick = {},
            enabled = true
        )
        Row(modifier = Modifier.onGloballyPositioned({
            speakerOffset.value = it.boundsInWindow().topLeft
        }), horizontalArrangement = Arrangement.End) {
            VolumeSpeedControlls()
            Spacer(modifier = Modifier.size(108.dp, 120.dp))
        }
    }
    SpeakerSelecter(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .offset(-(speakerOffset.value.x-200-108).dp , (-24).dp)
    )

}