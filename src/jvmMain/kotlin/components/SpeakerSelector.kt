package components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun SpeakerSelecter(backgroundColor: Color = Color(0xff151515), modifier: Modifier) {
    val speakerInteraction = remember { MutableInteractionSource() }
    val showAllSpeaker = remember { mutableStateOf(false) }
    val animateOffest = animateOffsetAsState(
        targetValue = if (!showAllSpeaker.value) Offset(0f,0f) else Offset(-108f,-120f)
    )
    showAllSpeaker.value = speakerInteraction.collectIsHoveredAsState().value
    val speakers = remember {
        mutableStateListOf(
            Speaker("Raquel", 0, true),
            Speaker("Mónica", 1, false),
            Speaker("Tokyo", 2, false),
            Speaker("Alicia", 3, false),
            Speaker("Julia", 4, false),
            Speaker("Berlin", 5, false),
            Speaker("Sergio", 6, false),
            Speaker("Alison", 7, false),
            Speaker("Tatiana", 8, false),
            Speaker("Denver", 9, false),
            Speaker("Moscow", 10, false),
        )
    }
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
        items(if (showAllSpeaker.value) speakers else speakers.filter { it.selected }) { item ->
            SpeakerItem(item.name, item.id, item.selected) { selectedId ->
                for (i in speakers.indices) {
                    speakers[i] = speakers[i].copy(selected = speakers[i].id == selectedId)
                }
                showAllSpeaker.value = false
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpeakerItem(name: String, id: Int, selected: Boolean, onClick: (Int) -> Unit) {
    Column(Modifier.onClick {
        if (!selected) onClick(id)
        println(name)
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

data class Speaker(val name: String, val id: Int, val selected: Boolean)