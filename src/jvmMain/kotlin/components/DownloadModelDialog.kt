package components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tts.TTSModel
import utils.ModelDownloader
import java.io.File


@ExperimentalComposeUiApi
@Composable
fun WindowScope.DownloadModelDialog(url: String, onDismissRequest: () -> Unit) {
    WindowDraggableArea(modifier = Modifier.fillMaxSize().background(Brush.radialGradient(listOf(
        MaterialTheme.colors.primary.copy(0.3f),MaterialTheme.colors.primary.copy(0.05f))))) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                Modifier.width(width = 350.dp)
                    .background(MaterialTheme.colors.background, shape = RoundedCornerShape(8.dp))
                    .border(
                        BorderStroke(
                            .5.dp,
                            Brush.linearGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Gray,
                                    MaterialTheme.colors.primary
                                )
                            )
                        ),RoundedCornerShape(8.dp)
                    ).animateContentSize()
            ) {

                Text(
                    text = "Download text-to-speech model",
                    modifier = Modifier.fillMaxWidth().background(
                        MaterialTheme.colors.background,
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    ).padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    DialogContent(url, onDismissRequest)
                }
            }
        }
    }
}

@Composable
private fun DialogContent(
    url: String, onDismissRequest: () -> Unit
) {
    var showInstruction by remember { mutableStateOf(true) }
    var currentDownloadingFile by remember { mutableStateOf("") }
    val downloadedSize = remember { mutableStateOf(0L) }
    val modelSize = remember { mutableStateOf(0L) }
    val error = remember { mutableStateOf("") }
    val destination = remember { File(TTSModel.modelDir.absolutePath) }
    val progressBarProgress = remember { mutableStateOf(0f) }
    val downloader = remember {
        ModelDownloader(
            object : ModelDownloader.ModelDownloaderListener {
                override fun onDownloadStart() {
                    showInstruction = false
                }

                override fun onDownloadComplete() {
                    onDismissRequest()
                }

                override fun onDownloadError(errorMsg: String) {
                    error.value = errorMsg
                    showInstruction = true
                }

                override fun onDownloadProgress(_downloadedSize: Long) {
                    downloadedSize.value = _downloadedSize
                }

                override fun onProgress(progress: Float) {
                    progressBarProgress.value = progress
                }

                override fun onModelSize(size: Long) {
                    modelSize.value = size
                }

                override fun onUnzippedFileName(name: String) {
                    currentDownloadingFile = name
                }
            })
    }
    if (showInstruction) {
        Text(
            "This program requires a text-to-speech model to function. Please click the download button to begin. (Download size: ~350 MB)",
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(16.dp)
        )
        if (error.value.isNotEmpty()) Text(
            "Error while downloading: ${error.value}",
            color = Color.Red.copy(0.9f),
            modifier = Modifier.padding(16.dp),
            fontSize = 14.sp
        )
        Button(
            {
                CoroutineScope(Dispatchers.IO).launch {
                    error.value = ""
                    downloader.run()
                }
            },
            modifier = Modifier.padding(bottom = 12.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.onSecondary,
                contentColor = MaterialTheme.colors.primary
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (error.value.isEmpty()) "Download" else "Retry")
        }
    } else {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Downloading: $currentDownloadingFile", color = MaterialTheme.colors.primary)
            LinearProgressIndicator(
                progress = progressBarProgress.value,
                modifier = Modifier.height(8.dp).fillMaxWidth(),
                color = MaterialTheme.colors.primary,
                strokeCap = StrokeCap.Round
            )
            Text(
                "${downloadedSize.value} MB / ${modelSize.value} MB",
                color = MaterialTheme.colors.primary,
                modifier = Modifier.align(Alignment.End)
            )
            DestinationText(destination)

        }
    }
}

@Composable
private fun DestinationText(destination: File) {
    Text(
        "location: ${destination.absolutePath}",
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(top = 16.dp)
    )
}