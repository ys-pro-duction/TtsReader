import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.MouseInfo
import java.awt.Point
import java.awt.geom.RoundRectangle2D

@Composable
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.clip(RoundedCornerShape(10.dp))) {

            Column(Modifier.background(Color.Black.copy(.9f)).padding(8.dp).fillMaxSize()) {
                AnimatedWordHighlightTTS()
                Row(Modifier.fillMaxSize()) {
                    Column {
                        VoiceSpeedControl()
                        SpeakerChooser()
                    }
                }
            }
        }
    }
}

fun main() = application {

//    Window(undecorated = false, title = "Text-to-speech", onCloseRequest = {
//        NonStreamingTtsKokoroEn.TTS.freeResources()
//        exitApplication()
//    }) {
//        App()
//    }

    Window(title = "Text to speech", undecorated = true, transparent = false, state = rememberWindowState(), onCloseRequest = {
        NonStreamingTtsKokoroEn.TTS.freeResources()
        exitApplication()
    }) {
        window.addComponentListener(object : java.awt.event.ComponentAdapter() {
            override fun componentResized(e: java.awt.event.ComponentEvent?) {
                window.shape = RoundRectangle2D.Double(
                    0.0,
                    0.0,
                    window.width.toDouble(),
                    window.height.toDouble(),
                    20.0,
                    20.0
                )

            }
        })
        Column(modifier = Modifier.background(Color.Black).fillMaxSize().clip(RoundedCornerShape(10.dp)) ) {
            WindowDraggableArea {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Text to speech", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = MaterialTheme.typography.h6.fontSize)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize().clip(RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                App()
            }
        }
    }
}
