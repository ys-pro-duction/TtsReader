import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.round


@Composable
fun ControlButtons(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    onResetClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val m = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(horizontal = 4.dp)
        Button(
            onClick = onPreviousClick,
            enabled = enabled,
            modifier = m,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
        ) {
            Text("Prev", color = Color.White)
        }
        
        Button(
            onClick = onNextClick,
            enabled = enabled,
            modifier = m,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
        ) {
            Text("Next", color = Color.White)
        }
        Button(
            onClick = onResetClick,
            enabled = enabled,
            modifier = m,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
        ) {
            Text("Restart", color = Color.White)
        }
    }
}

@Composable
fun VoiceSpeedControl(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
    ) {
        var speed by remember { mutableStateOf(1f) }
        Text(
            "Voice speed ${String.format("%.2f", speed)}x",
            Modifier.width(150.dp),
            overflow = TextOverflow.Clip,
            maxLines = 1,
            color = Color.White
        )
        Slider(speed, { value ->
            speed = value
            NonStreamingTtsKokoroEn.TTS.SPEED = speed
        }, steps = 100, modifier = Modifier.mouseScrollable { delta ->
            speed = (speed + (delta * 0.1f)).coerceIn(0.2f, 5f)
            NonStreamingTtsKokoroEn.TTS.SPEED = speed
        }, valueRange = 0.2f..5f, colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White.copy(.8f)))
    }
}


@Composable
fun SpeakerChooser(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
    ) {
        var speakerid by remember { mutableStateOf(0f) }
        Text(
            "Speaker_Id ${round(speakerid * 100).toInt()}",
            Modifier.width(150.dp),
            overflow = TextOverflow.Clip,
            maxLines = 1,
            color = Color.White
        )
        Slider(speakerid, { value ->
            speakerid = value
            NonStreamingTtsKokoroEn.TTS.SID = round(speakerid * 100).toInt()
        }, steps = 9, modifier = Modifier.mouseScrollable { delta ->
            speakerid = (speakerid + (delta * 0.01f)).coerceIn(0.0f, 0.1f)
            NonStreamingTtsKokoroEn.TTS.SID = round(speakerid * 100).toInt()
        }, valueRange = 0.0f..0.1f,  colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White.copy(.8f)))
    }
}


fun Modifier.mouseScrollable(onScroll: (Float) -> Unit): Modifier = composed {
    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                val scrollDelta = event.changes.firstOrNull()?.scrollDelta?.y ?: 0f
                if (scrollDelta != 0f) {
                    onScroll(scrollDelta * -1)
                }
            }
        }
    }
}


@OptIn(ExperimentalUnitApi::class, ExperimentalFoundationApi::class)
@Composable
fun AnimatedWordHighlightTTS() {
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var sentences by remember { mutableStateOf(listOf<String>()) }
    var currentWordIndex by remember { mutableStateOf(-1) }
    val scrollState = rememberScrollState()

    val coroutineScope = rememberCoroutineScope()

    val wordAnimations = remember(sentences.size) {
        sentences.map { Animatable(0f) }
    }
    val focusRequester = remember { FocusRequester() }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .background(Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                .padding(8.dp)
                .clickable(indication = null, interactionSource = remember {
                    MutableInteractionSource()
                }) {
                    focusRequester.requestFocus()
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                BasicTextField(
                    value = textFieldValue,
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester),
                    onValueChange = { newValue -> textFieldValue = newValue },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        color = Color.Transparent,  // Make the actual input transparent
                        letterSpacing = TextUnit(1f, TextUnitType.Sp)
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (textFieldValue.text.isEmpty()) {
                                Text(
                                    text = "Enter your text here...",
                                    color = Color.White.copy(.5f),
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            sentences = textFieldValue.text.split(".").filter { it.isNotEmpty() }
                            val animatedText = buildAnnotatedString {
                                sentences.forEachIndexed { index, sentence ->
                                    val animatedColor = lerp(
                                        Color.Transparent,
                                        Color.Yellow.copy(alpha = 0.1f),
                                        wordAnimations.getOrNull(index)?.value ?: 0f
                                    )
                                    withStyle(
                                        SpanStyle(
                                            background = animatedColor,
                                            color = Color.White.copy(.8f),
                                            letterSpacing = TextUnit(1f, TextUnitType.Sp)
                                        )
                                    ) {
                                        append("$sentence.")
                                    }
                                }
                            }
                            Text(
                                animatedText,
                                fontSize = 18.sp,
                                modifier = Modifier.fillMaxSize()
                            )
                            innerTextField()
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Button(
                onClick = {
                    if (NonStreamingTtsKokoroEn.TTS.isPlaying){
                        NonStreamingTtsKokoroEn.TTS.stopAudio()
                        NonStreamingTtsKokoroEn.TTS.isPlaying = false
                        return@Button
                    }else{
                        if (NonStreamingTtsKokoroEn.TTS.finished){
                            NonStreamingTtsKokoroEn.TTS.currentWordIdx = 0
                        }
                    }
//                    if (NonStreamingTtsKokoroEn.TTS.isPlaying && !NonStreamingTtsKokoroEn.TTS.finished) {
//                        NonStreamingTtsKokoroEn.TTS.stopAudio()
//                        NonStreamingTtsKokoroEn.TTS.finished = false
//                        return@Button
//                    }
                    NonStreamingTtsKokoroEn.TTS.finished = false
                    NonStreamingTtsKokoroEn.TTS.isPlaying = true
                    currentWordIndex = -1
                    coroutineScope.launch {
                        NonStreamingTtsKokoroEn.TTS.audioLoadingQueue.add(Pair(0, sentences[0]))
                        NonStreamingTtsKokoroEn.generateAudio()
                        loadAndPlay(sentences, NonStreamingTtsKokoroEn.TTS.currentWordIdx, wordAnimations) { newIndex ->
                            currentWordIndex = newIndex
                        }
                    }
                },
                modifier = Modifier.fillMaxHeight().fillMaxWidth(0.5f).padding(horizontal = 4.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
            ) {
                Text("Generate & Play", color = Color.White)
            }
            ControlButtons(
                isPlaying = false, enabled = true,
                modifier = Modifier,
                onResetClick = {NonStreamingTtsKokoroEn.TTS.stopAudio()},
                onPreviousClick = {},
                onNextClick = {}
            )
        }
    }
}

//fun loadFirstLine(words: List<String>, fromIndex: Int) {
//    NonStreamingTtsKokoroEn.generateAudio(words[fromIndex], fromIndex)
//    while (NonStreamingTtsKokoroEn.TTS.isProcessing) {
//        Thread.sleep(100)
//    }
//}

suspend fun loadAndPlay(
    words: List<String>,
    fromIndex: Int,
    wordAnimations: List<Animatable<Float, AnimationVector1D>>,
    updateIndex: (Int) -> Unit
) {
    if (fromIndex < 0 || fromIndex >= words.size || !NonStreamingTtsKokoroEn.TTS.isPlaying) return
    if (!NonStreamingTtsKokoroEn.TTS.isLoaded(fromIndex, words[fromIndex])) {
        if (NonStreamingTtsKokoroEn.TTS.isProcessing) {
            while (NonStreamingTtsKokoroEn.TTS.isProcessing && !NonStreamingTtsKokoroEn.TTS.isLoaded(
                    fromIndex,
                    words[fromIndex]
                )
            ) {
                delay(100)
            }
            loadAndPlay(words, fromIndex, wordAnimations, updateIndex)
        } else {
            NonStreamingTtsKokoroEn.TTS.audioLoadingQueue.add(Pair(fromIndex, words[fromIndex]))
            NonStreamingTtsKokoroEn.generateAudio()
            loadAndPlay(words, fromIndex, wordAnimations, updateIndex)
        }
    } else {
        val notLoadedidx = playWordsWithAnimation(words, fromIndex, wordAnimations, updateIndex)
        loadAndPlay(words, notLoadedidx, wordAnimations, updateIndex)
    }
}

// ✅ Function to Animate Highlighting for Each Word
suspend fun playWordsWithAnimation(
    words: List<String>,
    fromIndex: Int,
    animations: List<Animatable<Float, AnimationVector1D>>,
    updateIndex: (Int) -> Unit
): Int {
//    if (NonStreamingTtsKokoroEn.TTS.isPlaying) return fromIndex
    var firstPlaying = true
    for (index in fromIndex until words.size) {
        val audioData = NonStreamingTtsKokoroEn.TTS.audioLoaded[index] ?: return index
        if (index + 1 < words.size) NonStreamingTtsKokoroEn.TTS.audioLoadingQueue.add(
            Pair(
                index + 1,
                words[index + 1]
            )
        )
        if (index + 2 < words.size) NonStreamingTtsKokoroEn.TTS.audioLoadingQueue.add(
            Pair(
                index + 2,
                words[index + 2]
            )
        )
//        if (!firstPlaying) delay(2000)
        NonStreamingTtsKokoroEn.generateAudio()
        updateIndex(index) // Highlight current word


        animations.getOrNull(index)?.snapTo(0f) // Reset animation
        animations.getOrNull(index-1)
            ?.animateTo(
                0f,
                animationSpec = tween(durationMillis = 100)
            ) // Fade out
        animations.getOrNull(index)?.animateTo(
            1f,
            animationSpec = tween(durationMillis = 100)
        )

        CoroutineScope(Dispatchers.IO).launch {
            if (!NonStreamingTtsKokoroEn.TTS.isPlaying) return@launch
            NonStreamingTtsKokoroEn.TTS.currentWordIdx = index
            NonStreamingTtsKokoroEn.playAudio(index, audioData.text)
            if (index == words.size-1) {
                NonStreamingTtsKokoroEn.TTS.finished = true
                animations.getOrNull(index)?.snapTo(0f)
                NonStreamingTtsKokoroEn.TTS.currentWordIdx = 0
            }
        }.join()
//        animations.getOrNull(index)
//            ?.animateTo(
//                0f,
//                animationSpec = tween(durationMillis = 10)
//            ) // Fade out
        firstPlaying = false
    }

    updateIndex(-1) // Remove highlight after playback
    return -1
}