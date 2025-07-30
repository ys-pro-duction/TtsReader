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
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tts.TTSState


@Preview
@Composable
fun RowScope.PlaybackController(
    modifier: Modifier = Modifier.fillMaxSize().weight(1f).clip(shape = RoundedCornerShape(16.dp))
        .background(MaterialTheme.colors.primary, shape = RoundedCornerShape(16.dp)).padding(20.dp),
    baseViewModel: BaseViewModel
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val btnColor = MaterialTheme.colors.primary
        val btnModifier = remember(MaterialTheme.colors) {
            Modifier.fillMaxSize().weight(1f).clip(shape = RoundedCornerShape(16.dp))
                .background(btnColor, shape = RoundedCornerShape(16.dp)).padding(20.dp)
        }
        PlayPauseButton(baseViewModel)
        Row(
            modifier = Modifier.fillMaxSize().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Arrow back",
                modifier = Modifier.clip(shape = RoundedCornerShape(16.dp))
                    .clickable { baseViewModel.previousSpeech() }.fillMaxSize().weight(1f)
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(16.dp))
                    .padding(20.dp),
                tint = MaterialTheme.colors.primary
            )

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Arrow forward",
                modifier = Modifier.clip(shape = RoundedCornerShape(16.dp))
                    .clickable { CoroutineScope(Dispatchers.Default).launch { baseViewModel.nextSpeech() } }
                    .fillMaxSize().weight(1f)
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(16.dp))
                    .padding(20.dp),
                tint = MaterialTheme.colors.primary

            )

            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "restart",
                modifier = Modifier.clip(shape = RoundedCornerShape(16.dp))
                    .clickable { baseViewModel.restartWholeSpeech() }.fillMaxSize().weight(1f)
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colors.onSecondary, shape = RoundedCornerShape(16.dp))
                    .padding(20.dp),
                tint = MaterialTheme.colors.primary
            )
        }
    }
}

@Composable
private fun ColumnScope.PlayPauseButton(baseViewModel: BaseViewModel) {
    val ttsState by baseViewModel.ttsState.collectAsState(TTSState.STOP)
    val animateBackgroundColor = animateColorAsState(
        if (ttsState == TTSState.PLAY) MaterialTheme.colors.onSecondary else MaterialTheme.colors.primary,
        tween()
    )
    Box(
        Modifier.clip(shape = RoundedCornerShape(16.dp)).clickable {
            if (ttsState == TTSState.PLAY) {
                baseViewModel.stopTTS()
            } else if (ttsState == TTSState.STOP) {
                baseViewModel.startTTS()
            }
        }
            .fillMaxSize().weight(1f)
            .background(animateBackgroundColor.value, shape = RoundedCornerShape(16.dp))
            .padding(20.dp), contentAlignment = Alignment.Center
    ) {
        when (ttsState) {
            TTSState.PLAY -> Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Play",
                modifier = Modifier.fillMaxSize(),
                tint = MaterialTheme.colors.primary
            )

            TTSState.STOP -> Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Pause",
                modifier = Modifier.fillMaxSize(),
                tint = MaterialTheme.colors.surface
            )

            TTSState.LOADING -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colors.surface,
                    strokeWidth = 2.5.dp,
                )
            }
        }
    }
}