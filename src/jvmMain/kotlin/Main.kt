import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import components.AudioVisualize
import components.Controller
import components.MainTextArea
import components.Modifier
import kotlinx.coroutines.launch
import utils.reconstructFromSegments
import utils.splitSmartWithDelimiters
import java.awt.Dimension
import kotlin.math.abs

@Composable
fun App() {
    MaterialTheme {
        Box(Modifier.background(Color(0xff151515)).padding(8.dp).fillMaxSize()) {
//                AnimatedWordHighlightTTS(modifier = Modifier.align(Alignment.TopCenter))
            MainTextArea(modifier = Modifier)
            Controller(modifier = Modifier.align(Alignment.BottomCenter))
//                PositionedBlueBoxExample()

        }
    }
}

@Composable
fun PositionedBlueBoxExample() {
    var blueBoxOffset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            Modifier.height(100.dp).fillMaxWidth(), verticalAlignment = Alignment.Bottom
        ) {
            Spacer(
                modifier = Modifier.background(Color.Green).size(100.dp)
            )

            Spacer(
                modifier = Modifier.background(Color.Blue).size(100.dp)
                    .onGloballyPositioned { coordinates ->
                        // Get the position of the blue box relative to the root
                        val positionInRoot = coordinates.positionInRoot()
                        blueBoxOffset = positionInRoot
                        println(positionInRoot)
                    })
        }

        // Draw *another* blue box outside Row, at the same horizontal position
        Box(
            modifier = Modifier.size(80.dp).offset {
                IntOffset(
                    blueBoxOffset.x.toInt(), blueBoxOffset.y.toInt() - 40
                )
            }.background(Color.Cyan)

        )
    }
}


fun main() = application {
    Window(
        title = "Text to speech", undecorated = true, transparent = false, onCloseRequest = {
            NonStreamingTtsKokoroEn.TTS.freeResources()
            exitApplication()
        }) {
        window.minimumSize = Dimension(600, 400)
        Surface(
            shape = RoundedCornerShape(0.dp), border = BorderStroke(
                1.dp, Brush.linearGradient(listOf(Color.Transparent, Color.Gray, Color.White))
            )
        ) {
            Column(modifier = Modifier.background(Color.Black).fillMaxSize()) {
                WindowDraggableArea {
                    Box(
//                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
//                        Text(
//                            "Text to speech",
//                            color = Color.White,
//                            fontWeight = FontWeight.ExtraBold,
//                            fontSize = MaterialTheme.typography.h6.fontSize
//                        )
                        val audioVisualViewModel = remember { AudioVisualViewModel() }
                        NonStreamingTtsKokoroEn.audioVisualViewModel = audioVisualViewModel
                        val barData = audioVisualViewModel.barData.collectAsState()


                        AudioVisualize(
                            modifier = Modifier
                                .offset(5.dp)
                                .width(90.dp)
                                .background(Color.Green)
                                ,
                            barData = barData.value.toList()
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    App()
                }
            }
        }
    }

    val input = """
        it's me. this is another sentence.
        this is another title:
        and this is line
        """.trimIndent()


    val segments = splitSmartWithDelimiters(input)
    println("Segments:")
    segments.forEach { println("• '${it.text}' [${it.delimiter.replace("\n", "\\n")}]") }

    val reconstructed = reconstructFromSegments(segments)
    println("\nReconstructed:")
    println(reconstructed)
}