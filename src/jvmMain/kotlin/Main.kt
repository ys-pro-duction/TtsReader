import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import components.AudioVisualize
import components.Controller
import components.DownloadModelDialog
import components.MainTextArea
import tts.Server
import tts.TTSModel
import tts.TTSState
import java.awt.Dimension
import java.io.File
import java.io.InputStream
import java.net.URL
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

@Composable
fun App(baseViewModel: BaseViewModel) {
    MaterialTheme {
        Box(Modifier.background(Color(0xff151515)).padding(8.dp).fillMaxSize()) {
//                AnimatedWordHighlightTTS(modifier = Modifier.align(Alignment.TopCenter))
            MainTextArea(modifier = Modifier, baseViewModel = baseViewModel)
            Controller(
                modifier = Modifier.align(Alignment.BottomCenter),
                baseViewModel = baseViewModel
            )
//                PositionedBlueBoxExample()

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    Window(
        title = "Text to speech", undecorated = true, transparent = false, onCloseRequest = {
            TTSModel.freeResources()
            exitApplication()
        }) {
        window.minimumSize = Dimension(600, 400)
        Surface(
            shape = RoundedCornerShape(0.dp), border = BorderStroke(
                1.dp, Brush.linearGradient(listOf(Color.Transparent, Color.Gray, Color.White))
            )
        ) {
            Column(modifier = Modifier.background(Color.Black).fillMaxSize()) {
                val baseViewModel = remember { BaseViewModel() }
                val showDownloadModelDialog = remember { mutableStateOf(false) }
                WindowDraggableArea {
                    Box(
//                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
//                        Text(
//                            "Text to speech",
//                            color = Color.White,
//                            fontWeight = FontWeight.ExtraBold,
//                            fontSize = MaterialTheme.typography.h6.fontSize
//                        )
                        val barData = baseViewModel.barData.collectAsState()
                        AudioVisualize(
                            modifier = Modifier
                                .offset(5.dp)
                                .width(90.dp), barData = barData.value.toList(),
                            barColor = MaterialTheme.colors.primary
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    App(baseViewModel = baseViewModel)
                    if (showDownloadModelDialog.value) DownloadModelDialog(TTSModel.url,{showDownloadModelDialog.value = false})
                }
                LaunchedEffect(Unit) {
                    if (TTSModel.isModelExist()){
                        baseViewModel.setTTSState(TTSState.STOP)
                    }else{
                        showDownloadModelDialog.value = true
                    }
                    Server(baseViewModel).startSimpleTtsServer()
                }
            }
        }

    }
}