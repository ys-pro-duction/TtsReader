package components

import BaseViewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import icons.ArrowDropDown
import icons.Close
import icons.DayLight
import icons.Github
import icons.MoreVert
import icons.NightMoon

@Composable
fun TitleBarActions(
    modifier: Modifier = Modifier, baseViewModel: BaseViewModel, onClose: () -> Unit
) {
    Row(modifier = modifier) {
        MinimalDropdownMenu(baseViewModel)
        CloseButton(onClose)
    }
}

@Composable
private fun MinimalDropdownMenu(baseViewModel: BaseViewModel) {
    var expanded by remember { mutableStateOf(false) }


    Icon(
        MoreVert,
        contentDescription = "More options",
        tint = androidx.compose.material.MaterialTheme.colors.primary,
        modifier = Modifier.size(48.dp).clip(shape = RoundedCornerShape(4.dp)).clickable { expanded = !expanded }
            .padding(14.dp))

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier.width(180.dp).background(androidx.compose.material.MaterialTheme.colors.background).border(
                BorderStroke(
                    1.dp, Brush.linearGradient(
                        colors = listOf(
                            androidx.compose.material.MaterialTheme.colors.primary, Color.Transparent
                        )
                    )
                ), RoundedCornerShape(2.dp)
            ).animateContentSize(),
        offset = DpOffset(x = -10.dp, y = 0.dp)
    ) {
        var isColorPickerExpanded by remember { mutableStateOf(false) }
        val animateDropdownRotation = animateFloatAsState(if (isColorPickerExpanded) 180f else 0f)
        DropdownMenuItem(
            text = { Text("Highlight color", color = androidx.compose.material.MaterialTheme.colors.primary) },
            trailingIcon = {
                Icon(
                    ArrowDropDown,
                    null,
                    tint = androidx.compose.material.MaterialTheme.colors.primary,
                    modifier = Modifier.rotate(animateDropdownRotation.value)
                )
            },
            onClick = { isColorPickerExpanded = !isColorPickerExpanded },
        )
        if (isColorPickerExpanded) HuePickerSlider(baseViewModel)
        HorizontalDivider(
            Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally),
            color = androidx.compose.material.MaterialTheme.colors.secondary
        )
        DropdownMenuItem(
            text = {
                Text(
                    if (baseViewModel.isDarkMode.value) "Light theme" else "Dark theme",
                    color = androidx.compose.material.MaterialTheme.colors.primary
                )
            },
            leadingIcon = {
                Icon(
                    if (baseViewModel.isDarkMode.value) DayLight else NightMoon,
                    contentDescription = null,
                    tint = androidx.compose.material.MaterialTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            },
            onClick = {
                baseViewModel.setDarkMode(!baseViewModel.isDarkMode.value)
                expanded = false
            },
        )
        HorizontalDivider(
            Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally),
            color = androidx.compose.material.MaterialTheme.colors.secondary
        )
        val uriHandler = LocalUriHandler.current
        DropdownMenuItem(
            text = { Text("Github project", color = androidx.compose.material.MaterialTheme.colors.primary) },
            leadingIcon = {
                Icon(
                    Github,
                    contentDescription = "GitHub",
                    tint = androidx.compose.material.MaterialTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            },
            onClick = {
                uriHandler.openUri("https://github.com/ys-pro-duction/TtsReader.git")
            },
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.HuePickerSlider(baseViewModel: BaseViewModel) {
    val hue = baseViewModel.highlightColorHue.collectAsState()
    val colorAlpha = baseViewModel.highlightColorAlpha.collectAsState()
    val selectedColor = remember { mutableStateOf(Color.hsl(hue.value, 1f, 0.5f)) }
    LaunchedEffect(hue.value) {
        selectedColor.value = Color.hsl(hue.value, 1f, 0.5f)
    }
    Slider(
        value = hue.value, onValueChange = { newValue ->
        baseViewModel.updateHighlightColorHue(newValue)
    }, modifier = Modifier.fillMaxWidth(0.9f).align(Alignment.CenterHorizontally), valueRange = 0f..360f,
        // Custom track for the hue gradient
        track = { sliderState ->
            val gradientColors = remember {
                listOf(
                    Color.hsv(0f, 1f, 1f),    // Red
                    Color.hsv(60f, 1f, 1f),   // Yellow
                    Color.hsv(120f, 1f, 1f),  // Green
                    Color.hsv(180f, 1f, 1f),  // Cyan
                    Color.hsv(240f, 1f, 1f),  // Blue
                    Color.hsv(300f, 1f, 1f),  // Magenta
                    Color.hsv(360f, 1f, 1f)   // Back to Red
                )
            }
            Canvas(
                modifier = Modifier.fillMaxWidth().height(12.dp) // Adjust height as needed
                    .background(
                        brush = Brush.horizontalGradient(gradientColors),
                        shape = RoundedCornerShape(4.dp) // Use your desired shape
                    )
            ) {

            }
        }, thumb = {
            Spacer(
                modifier = Modifier.size(24.dp).background(selectedColor.value, shape = MaterialTheme.shapes.small)
                    .border(
                        3.dp,
                        androidx.compose.material.MaterialTheme.colors.secondary,
                        shape = MaterialTheme.shapes.small
                    )
            )
        }

    )
    Slider(colorAlpha.value, onValueChange = { newValue ->
        baseViewModel.updateHilightColorAlpha(newValue)
    }, valueRange = 0.0f..1f, modifier = Modifier.fillMaxWidth(0.9f).align(Alignment.CenterHorizontally), track = {
        Spacer(
            Modifier.height(12.dp).fillMaxWidth().background(
                    Brush.horizontalGradient(listOf(Color.Transparent, selectedColor.value)),
                    shape = RoundedCornerShape(3.dp)
                )
        )
    }, thumb = {
        Spacer(
            modifier = Modifier.size(24.dp).background(
                    androidx.compose.material.MaterialTheme.colors.secondary,
                    shape = MaterialTheme.shapes.small
                ).background(
                    selectedColor.value.copy(colorAlpha.value), shape = MaterialTheme.shapes.small
                ).border(
                    3.dp,
                    androidx.compose.material.MaterialTheme.colors.secondary,
                    shape = MaterialTheme.shapes.small
                )
        )
    })
}

@Composable
private fun CloseButton(onClose: () -> Unit) {
    val interationSource = remember { MutableInteractionSource() }
    val isHover = interationSource.collectIsHoveredAsState()
    Icon(
        Close,
        null,
        tint = if (isHover.value) Color.White else androidx.compose.material.MaterialTheme.colors.primary,
        modifier = Modifier.background(if (isHover.value) Color.Red else Color.Transparent).hoverable(interationSource)
            .size(48.dp).clickable { onClose() }.padding(14.dp))
}