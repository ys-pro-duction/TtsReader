package components

import BaseViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BoxScope.MainTextArea(modifier: Modifier, baseViewModel: BaseViewModel) {
    val textFieldValue by baseViewModel.rawText.collectAsState()
    val sentences by baseViewModel.sentencesOfTextSegments.collectAsState()

    val scrollState = rememberScrollState()

    val customSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colors.primary,
        backgroundColor = MaterialTheme.colors.primary.copy(0.5f)
    )

    val currentHighlightWordIndex = baseViewModel.currentHighlightWordIdx.collectAsState()

    val focusRequester = remember { FocusRequester() }

    val highlightColorHue = baseViewModel.highlightColorHue.collectAsState()
    val highlightColorAlpha = baseViewModel.highlightColorAlpha.collectAsState()
    val highlightColor = remember {
        mutableStateOf(
            Color.hsl(highlightColorHue.value, 1f, 0.5f).copy(highlightColorAlpha.value)
        )
    }
    LaunchedEffect(currentHighlightWordIndex.value) {
        if (currentHighlightWordIndex.value == 0) scrollState.animateScrollTo(0)
        else if (currentHighlightWordIndex.value >= sentences.size-1) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
    LaunchedEffect(highlightColorHue.value, highlightColorAlpha.value) {
        highlightColor.value =
            Color.hsl(highlightColorHue.value, 1f, 0.5f).copy(highlightColorAlpha.value)
    }

    LaunchedEffect(currentHighlightWordIndex.value) {
        if (currentHighlightWordIndex.value <= 0 || currentHighlightWordIndex.value >= sentences.size-1) return@LaunchedEffect
        val index = currentHighlightWordIndex.value
        if (index < sentences.size) {
            val move =
                (sentences[index - 1].text.length * scrollState.maxValue / textFieldValue.length)
            val scroll = scrollState.value + move
            if (scroll > 20) scrollState.animateScrollTo(scroll)
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 150.dp)
            .shadow(4.dp, shape = RoundedCornerShape(8.dp), spotColor = Color.Black)
            .background(MaterialTheme.colors.onPrimary, shape = RoundedCornerShape(8.dp))
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
                    modifier = Modifier.fillMaxSize().requiredHeightIn(min = 150.dp)
                        .focusRequester(focusRequester),
                    onValueChange = { newValue -> baseViewModel.updateTextValue(newValue) },
                    cursorBrush = SolidColor(MaterialTheme.colors.primary),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        color = Color.Transparent,
                        letterSpacing = TextUnit(1f, TextUnitType.Sp)
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (textFieldValue.isEmpty()) {
                                Text(
                                    text = "Enter your text here...",
                                    color = MaterialTheme.colors.primary.copy(.5f),
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            val animatedText = buildAnnotatedString {
                                sentences.forEachIndexed { index, sentence ->
                                    withStyle(
                                        SpanStyle(
                                            background = if (currentHighlightWordIndex.value == index) highlightColor.value else Color.Transparent,
                                            color = MaterialTheme.colors.primary.copy(.9f),
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
}