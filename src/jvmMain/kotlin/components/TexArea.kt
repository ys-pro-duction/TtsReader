package components

import BaseViewModel
import NonStreamingTtsKokoroEn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import loadAndPlay
import utils.TextSegment
import utils.splitSmartWithDelimiters

@Composable
fun BoxScope.MainTextArea(modifier: Modifier, baseViewModel: BaseViewModel) {
    var textFieldValue by remember { mutableStateOf("") }
    var sentences by remember { mutableStateOf(listOf<TextSegment>()) }

    val scrollState = rememberScrollState()

    val coroutineScope = rememberCoroutineScope()

    val currentHighlightWordIndex = baseViewModel.currentHighlightWordIdx.collectAsState(-1)

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(textFieldValue) {
        println(textFieldValue)
        sentences = splitSmartWithDelimiters(textFieldValue)
    }
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 150.dp)
        .background(Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)).padding(8.dp)
        .clickable(indication = null, interactionSource = remember {
            MutableInteractionSource()
        }) {
            focusRequester.requestFocus()
        }) {
        Box(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            BasicTextField(
                value = textFieldValue,
                modifier = Modifier.fillMaxSize().focusRequester(focusRequester),
                onValueChange = { newValue -> textFieldValue = newValue },
                textStyle = TextStyle(
                    fontSize = 18.sp, color = Color.Green,  // Make the actual input transparent
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
                                        letterSpacing = TextUnit(1f, TextUnitType.Sp)
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
    Row {
        Button(
            onClick = {
                if (NonStreamingTtsKokoroEn.TTS.isPlaying) {
                    NonStreamingTtsKokoroEn.TTS.stopAudio()
                    NonStreamingTtsKokoroEn.TTS.isPlaying = false
                    return@Button
                } else {
                    if (NonStreamingTtsKokoroEn.TTS.finished) {
                        NonStreamingTtsKokoroEn.TTS.currentWordIdx = 0
                    }
                }
                NonStreamingTtsKokoroEn.TTS.finished = false
                NonStreamingTtsKokoroEn.TTS.isPlaying = true
                coroutineScope.launch {
                    NonStreamingTtsKokoroEn.TTS.audioLoadingQueue.add(Pair(0, sentences[0].text))
                    NonStreamingTtsKokoroEn.generateAudio()
                    loadAndPlay(
                        sentences, NonStreamingTtsKokoroEn.TTS.currentWordIdx
                    ) { newIndex ->
                    }
                }
            },
            modifier = Modifier.size(10.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
        ) {
            Text("Generate & Play", color = Color.White)
        }
    }
}