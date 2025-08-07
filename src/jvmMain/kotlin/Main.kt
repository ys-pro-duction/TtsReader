import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import components.*
import icons.AppIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import theme.TTSReaderTheme
import tts.Server
import tts.TTSModel
import tts.TTSState
import java.awt.Dimension
import java.util.prefs.Preferences

@Composable
fun BoxScope.App(baseViewModel: BaseViewModel) {
    MainTextArea(modifier = Modifier, baseViewModel = baseViewModel)
    Controller(
        modifier = Modifier.align(Alignment.BottomCenter), baseViewModel = baseViewModel
    )
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val windowState = rememberWindowState(
        size = with((PreferencesSettings(Preferences.userRoot()) as Settings)) {
            DpSize(
                getInt("Wwidth", 800).dp, getInt("Wheight", 600).dp
            )
        })
    LaunchedEffect(windowState.size) {
        with((PreferencesSettings(Preferences.userRoot()) as Settings)) {
            putInt("Wwidth", windowState.size.width.value.toInt())
            putInt("Wheight", windowState.size.height.value.toInt())
        }
    }
    Window(
        title = "Text to speech", undecorated = true, transparent = false, onCloseRequest = {
        TTSModel.freeResources()
        exitApplication()
    }, state = windowState) {
        window.minimumSize = Dimension(620, 400)
        val baseViewModel = remember { BaseViewModel() }
        val isDarkTheme by baseViewModel.isDarkMode.collectAsState()
        TTSReaderTheme(isDarkTheme) {
            Surface(
                border = BorderStroke(
                    1.dp, Brush.linearGradient(
                        listOf(
                            Color.Transparent, Color.Gray, MaterialTheme.colors.primary
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
                            TTSModel.getInstance().generate(" ", 0, 1f)
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
                            modifier = Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.align(Alignment.CenterStart),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = AppIcon,
                                    contentDescription = null,
                                    modifier = Modifier.padding(start = 8.dp, end = 8.dp).size(28.dp),
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
                                modifier = Modifier.width(90.dp).offset(x = 5.dp),
                                barData = baseViewModel.barData.collectAsState().value.toList(),
                                barColor = MaterialTheme.colors.primary,
                            )
                            TitleBarActions(
                                modifier = Modifier.align(Alignment.CenterEnd),
                                baseViewModel = baseViewModel,
                                onClose = { exitApplication() })
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxSize().padding(8.dp).clip(RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        App(baseViewModel = baseViewModel)
                        if (showDownloadModelDialog.value) {
                            DownloadModelDialog(
                                url = TTSModel.url, onDismissRequest = {
                                    showDownloadModelDialog.value = false
                                    coroutineScope.launch(Dispatchers.IO) {
                                        TTSModel.getInstance().generate(" ", 0, 1f)
                                    }
                                })
                        }
                    }
                }
            }
        }
    }
}