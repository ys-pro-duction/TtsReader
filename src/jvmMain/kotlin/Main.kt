import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import components.AudioVisualize
import components.Controller
import components.DownloadModelDialog
import components.MainTextArea
import components.TitleBarActions
import icons.AppIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import theme.TTSReaderTheme
import tts.Server
import tts.TTSModel
import tts.TTSState
import java.awt.Dimension
import java.io.File

@Composable
fun BoxScope.App(baseViewModel: BaseViewModel) {
//        Box(Modifier.background(Color(0xff151515)).padding(8.dp).fillMaxSize()) {
//                AnimatedWordHighlightTTS(modifier = Modifier.align(Alignment.TopCenter))
    MainTextArea(modifier = Modifier, baseViewModel = baseViewModel)
    Controller(
        modifier = Modifier.align(Alignment.BottomCenter),
        baseViewModel = baseViewModel
    )
//                PositionedBlueBoxExample()

//    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    Window(
        title = "Text to speech", undecorated = true, transparent = true, onCloseRequest = {
            TTSModel.freeResources()
            exitApplication()
        }) {
        window.minimumSize = Dimension(620, 400)
        val baseViewModel = remember { BaseViewModel() }
        val isDarkTheme by baseViewModel.isDarkMode.collectAsState()
        TTSReaderTheme(isDarkTheme) {
            Surface(
                shape = RoundedCornerShape(12.dp), border = BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            Color.Transparent,
                            Color.Gray,
                            MaterialTheme.colors.primary
                        )
                    )
                )
            ) {
                val showDownloadModelDialog = remember { mutableStateOf(false) }
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(baseViewModel) {
                    coroutineScope.launch(Dispatchers.IO) {
                        if (TTSModel.isModelExist()) {
                            baseViewModel.setTTSState(TTSState.STOP)
                            TTSModel.getInstance().generate(" ")
                        } else {
                            showDownloadModelDialog.value = true
                        }
                        Server(baseViewModel).startSimpleTtsServer()
                    }
                }
                Column(
                    modifier = Modifier.background(MaterialTheme.colors.background).fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    WindowDraggableArea {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.align(Alignment.CenterStart),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = AppIcon,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(start = 8.dp, end = 8.dp)
                                        .size(28.dp),
                                    tint = MaterialTheme.colors.primary
                                )
                                Text(
                                    text = "Text-to-Speech Reader",
                                    color = MaterialTheme.colors.primary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            AudioVisualize(
                                modifier = Modifier
                                    .width(90.dp)
                                    .offset(x = 5.dp),
                                barData = baseViewModel.barData.collectAsState().value.toList(),
                                barColor = MaterialTheme.colors.primary,
                            )
                            TitleBarActions(
                                modifier = Modifier.align(Alignment.CenterEnd),
                                baseViewModel = baseViewModel,
                                onClose = { exitApplication() }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        App(baseViewModel = baseViewModel)
//                        if (showDownloadModelDialog.value) {
//                            DownloadModelDialog(
//                                url = TTSModel.url,
//                                onDismissRequest = {
//                                    showDownloadModelDialog.value = false
//                                    coroutineScope.launch(Dispatchers.IO) {
//                                        TTSModel.getInstance().generate(" ")
//                                    }
//                                }
//                            )
//                        }
                    }
                }
            }
        }
    }
}