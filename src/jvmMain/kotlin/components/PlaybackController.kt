@file:OptIn(ExperimentalAnimationApi::class)

package components

import BaseViewModel
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier as Modi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tts.TTSState


@Preview
@Composable
fun RowScope.PlaybackController(
    modifier: Modi = Modifier,
    baseViewModel: BaseViewModel,
    onGeneratePlayClick: () -> Unit,
    onResetClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceEvenly
    ) {
        val btnModifier = remember {
            Modifier.fillMaxSize().weight(1f).padding(4.dp)
                .background(Color.DarkGray, shape = RoundedCornerShape(16.dp)).padding(20.dp)
        }
        PlayPauseButton(btnModifier = btnModifier, baseViewModel, { onGeneratePlayClick() })
        Row(modifier = Modifier.fillMaxSize().weight(1f)) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Arrow back",
                modifier = Modifier.clickable{baseViewModel.previousSpeech()}.then(btnModifier),
                tint = Color.White
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Arrow forward",
                modifier = Modifier.clickable{ CoroutineScope(Dispatchers.Default).launch { baseViewModel.nextSpeech() } }.then(btnModifier),
                tint = Color.White

            )

            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "restart",
                modifier = Modifier.clickable{baseViewModel.restartWholeSpeech()}.then(btnModifier),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ColumnScope.PlayPauseButton(btnModifier: Modi, baseViewModel: BaseViewModel, onGeneratePlayClick: () -> Unit) {
    val ttsState by baseViewModel.ttsState.collectAsState(TTSState.STOP)
    val animateBackgroundColor = animateColorAsState(if (ttsState == TTSState.PLAY) Color.DarkGray else Color.White,
        tween()
    )
    Box(Modifier.clickable {
        onGeneratePlayClick()
        if (ttsState == TTSState.PLAY) {
            baseViewModel.stopTTS()
        } else if (ttsState == TTSState.STOP) {
            baseViewModel.startTTS()
        }
    }.then(Modifier.fillMaxSize().weight(1f).padding(4.dp)
        .background(animateBackgroundColor.value, shape = RoundedCornerShape(16.dp)).padding(20.dp)), contentAlignment = Alignment.Center) {
        when (ttsState) {
            TTSState.PLAY -> Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Play",
                modifier = Modifier.fillMaxSize(),
                tint = Color.White
            )

            TTSState.STOP -> Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Pause",
                modifier = Modifier.fillMaxSize(),
                tint = Color.DarkGray
            )

            TTSState.LOADING -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color.DarkGray,
                    strokeWidth = 2.5.dp,
                )
            }
        }
    }
}