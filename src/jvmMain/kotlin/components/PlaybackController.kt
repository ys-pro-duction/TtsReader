package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import components.Modifier
import kotlinx.coroutines.delay


@Preview
@Composable
fun RowScope.PlaybackController(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    onGeneratePlayClick: () -> Unit,
    onResetClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceEvenly
    ) {
//        val m = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
//        Button(
//            onClick = onPreviousClick,
//            enabled = enabled,
//            modifier = m,
//            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
//        ) {
//            Text("Generate & Play", color = Color.White)
//        }
//
//        Row() {
//            Button(
//                onClick = onPreviousClick,
//                enabled = enabled,
//                modifier = m.weight(1f),
//                colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
//            ) {
//                Text("Previous", color = Color.White)
//            }
//            Button(
//                onClick = onNextClick,
//                enabled = enabled,
//                modifier = m.weight(1f),
//                colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
//            ) {
//                Text("Next", color = Color.White)
//            }
//        }
//        Button(
//            onClick = onResetClick,
//            enabled = enabled,
//            modifier = m,
//            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
//        ) {
//            Text("Restart", color = Color.White)
//        }

        val play = remember { mutableStateOf(false) }
        val btnModifier = Modifier.fillMaxSize().weight(1f).padding(4.dp)
            .background(Color.DarkGray, shape = RoundedCornerShape(16.dp)).padding(20.dp)
//        IconButton({},modifier = Modifier.fillMaxWidth().weight(1f).background(Color.DarkGray, shape = RoundedCornerShape(16.dp))){
        Box(btnModifier, contentAlignment = Alignment.Center) {
            if (!play.value) Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                modifier = Modifier.fillMaxSize(),
                tint = Color.White
            ) else Icon(
                imageVector = Icons.Default.Pause,
                contentDescription = "Pause",
                modifier = Modifier.fillMaxSize(),
                tint = Color.White
            )
        }
//        }
        Row(modifier = Modifier.fillMaxSize().weight(1f)) {
//            IconButton({},modifier = Modifier.fillMaxSize().weight(1f).background(Color.DarkGray, shape = RoundedCornerShape(16.dp))){
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Arrow back",
                modifier = Modifier.then(btnModifier),
                tint = Color.White
            )
//            }
//            IconButton({},modifier = Modifier.fillMaxSize().weight(1f).background(Color.DarkGray, shape = RoundedCornerShape(16.dp))){
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Arrow forward",
                modifier = Modifier.then(btnModifier),
                tint = Color.White
            )
//            }
//            IconButton({},modifier = Modifier.fillMaxSize().weight(1f).background(Color.DarkGray, shape = RoundedCornerShape(16.dp))){
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "restart",
                modifier = Modifier.then(btnModifier),
                tint = Color.White
            )
//            }
        }
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
                play.value = !play.value
            }
        }
    }
}