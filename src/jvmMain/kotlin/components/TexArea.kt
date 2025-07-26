package components

import BaseViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier as Modi

@Composable
fun BoxScope.MainTextArea(modifier: Modi, baseViewModel: BaseViewModel) {
    val textFieldValue by baseViewModel.rawText.collectAsState()
    val sentences by baseViewModel.sentencesOfTextSegments.collectAsState()
    val showHideText = remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val customSelectionColors = TextSelectionColors(
        handleColor = Color.Magenta,   // The drag handle
        backgroundColor = Color.White.copy(alpha = 0.4f) // The highlight color
    )

    val currentHighlightWordIndex = baseViewModel.currentHighlightWordIdx.collectAsState(-1)

    val focusRequester = remember { FocusRequester() }
    val scrollPosition = remember { mutableStateOf(0) }
    LaunchedEffect(currentHighlightWordIndex.value) {
        val index = currentHighlightWordIndex.value
        if (index > 0 && index < sentences.size) {
            val move = (sentences[index-1].text.length*scrollState.maxValue / textFieldValue.length)
            scrollPosition.value+= move
            val scroll = scrollPosition.value
            if (scroll < 20) return@LaunchedEffect
            scrollState.animateScrollTo(scroll)
//            val offsetPx = with(density) { (16.dp * index).toPx() }
//            scrollState.animateScrollTo(offsetPx.toInt())
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 150.dp)
            .background(Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
            .clickable(indication = null, interactionSource = remember {
                MutableInteractionSource()
            }) {
                focusRequester.requestFocus()
            }) {
        Box(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            CompositionLocalProvider(LocalTextSelectionColors provides customSelectionColors) {
                BasicTextField(
                    value = textFieldValue,
                    modifier = Modifier.fillMaxSize().requiredHeightIn(min = 150.dp).focusRequester(focusRequester),
                    onValueChange = { newValue -> baseViewModel.updateTextValue(newValue) },
                    cursorBrush = SolidColor(Color.White),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        color = if (!showHideText.value) Color.Green else Color.Transparent,  // Make the actual input transparent
                        letterSpacing = TextUnit(1f, TextUnitType.Sp)
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (textFieldValue.isEmpty()) {
                                Text(
                                    text = "Enter your text here...",
                                    color = Color.White.copy(.5f),
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            val animatedText = buildAnnotatedString {
                                sentences.forEachIndexed { index, sentence ->
                                    withStyle(
                                        SpanStyle(
                                            background = if (currentHighlightWordIndex.value == index) Color.Yellow.copy(
                                                alpha = 0.2f
                                            ) else Color.Transparent,
                                            color = Color.White.copy(.8f),
                                            letterSpacing = TextUnit(1f, TextUnitType.Sp),
                                        )
                                    ) {
                                        append("${sentence.text}${sentence.delimiter}")
                                    }
                                }
                            }
                            Text(
                                animatedText, fontSize = 18.sp, modifier = Modifier.fillMaxSize()
                            )
                            innerTextField()
                        }
                    })
            }
        }
    }
//    Row {
//        Button(
//            onClick = {
//                showHideText.value = !showHideText.value
////                if (NonStreamingTtsKokoroEn.TTS.isPlaying) {
////                    NonStreamingTtsKokoroEn.TTS.stopAudio()
////                    NonStreamingTtsKokoroEn.TTS.isPlaying = false
////                    return@Button
////                } else {
////                    if (NonStreamingTtsKokoroEn.TTS.finished) {
////                        NonStreamingTtsKokoroEn.TTS.currentWordIdx = 0
////                    }
////                }
////                NonStreamingTtsKokoroEn.TTS.finished = false
////                NonStreamingTtsKokoroEn.TTS.isPlaying = true
////                coroutineScope.launch {
////                    NonStreamingTtsKokoroEn.TTS.audioLoadingQueue.add(Pair(0, sentences[0].text))
////                    NonStreamingTtsKokoroEn.generateAudio()
////                    loadAndPlay(
////                        sentences, NonStreamingTtsKokoroEn.TTS.currentWordIdx
////                    ) { newIndex ->
////                    }
////                }
//            },
//            modifier = Modifier.width(50.dp).height(20.dp),
//            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
//        ) {
//            Text("Generate & Play", color = Color.White)
//        }
//    }
}