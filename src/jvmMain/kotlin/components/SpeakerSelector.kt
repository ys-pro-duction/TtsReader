package components

import BaseViewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier as Modi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SpeakerSelecter(backgroundColor: Color = Color(0xff151515), modifier: Modi,baseViewModel: BaseViewModel) {
    val speakerInteraction = remember { MutableInteractionSource() }
    val showAllSpeaker = remember { mutableStateOf(false) }
    val isHoverEnabled = remember { mutableStateOf(true) }
    val animateOffest = animateOffsetAsState(
        targetValue = if (!showAllSpeaker.value) Offset(0f,0f) else Offset(-108f,-120f)
    )
    val selectedSpeaker by baseViewModel.selectedSpeaker.collectAsState()
    showAllSpeaker.value = isHoverEnabled.value && speakerInteraction.collectIsHoveredAsState().value
    val animateGridWith = animateDpAsState(if (showAllSpeaker.value) 360.dp else 120.dp)
    LazyVerticalGrid(
        GridCells.Fixed(if (showAllSpeaker.value) 3 else 1),
        modifier = modifier.offset(animateOffest.value.x.dp, animateOffest.value.y.dp).requiredSize(
            width = animateGridWith.value * 0.9f,
            height = animateGridWith.value
        ).background(backgroundColor, RoundedCornerShape(12.dp)).hoverable(speakerInteraction)
            .animateContentSize().then(
                if (showAllSpeaker.value) Modifier.border(
                    1.dp,
                    Brush.verticalGradient(listOf(Color.Gray, Color.Transparent)),
                    RoundedCornerShape(12.dp)
                ) else Modifier
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(if (showAllSpeaker.value) Speaker.all else Speaker.all.filter {
            it.id == selectedSpeaker.id }) { item ->
            SpeakerItem(item.name, item.id, item.id == selectedSpeaker.id) { selectedId ->
                baseViewModel.updateSpeaker(Speaker.all[selectedId])
                CoroutineScope(Dispatchers.Default).launch {
                    isHoverEnabled.value = false
                    delay(500)
                    isHoverEnabled.value = true
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpeakerItem(name: String, id: Int, selected: Boolean, onClick: (Int) -> Unit) {
    Column(Modifier.onClick {
        if (!selected) onClick(id)
    }, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painterResource("s_i_$id.jpg"), null, modifier = Modifier.requiredSize(80.dp).then(
                if (selected) Modifier
                    .border(1.dp, Color.White, CircleShape)
                    .padding(4.dp) else Modifier.padding(4.dp)
            ).then(Modifier.clip(CircleShape)), contentScale = ContentScale.FillBounds
        )
        Text(
            name, modifier = Modifier.padding(top = 2.dp), color = Color.White
        )
    }
}

