import androidx.compose.ui.Modifier as Modi
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utils.TextSegment

suspend fun loadAndPlay(
    words: List<TextSegment>,
    fromIndex: Int,
    updateIndex: (Int) -> Unit
) {
//    if (fromIndex < 0 || fromIndex >= words.size || !NonStreamingTtsKokoroEn.TTS.isPlaying) return
//    if (!NonStreamingTtsKokoroEn.TTS.isLoaded(fromIndex, words[fromIndex].text)) {
//        if (NonStreamingTtsKokoroEn.TTS.isProcessing) {
//            while (NonStreamingTtsKokoroEn.TTS.isProcessing && !NonStreamingTtsKokoroEn.TTS.isLoaded(
//                    fromIndex,
//                    words[fromIndex].text
//                )
//            ) {
//                delay(100)
//            }
//            loadAndPlay(words, fromIndex, updateIndex)
//        } else {
//            NonStreamingTtsKokoroEn.TTS.audioLoadingQueue.add(Pair(fromIndex, words[fromIndex].text))
//            NonStreamingTtsKokoroEn.generateAudio()
//            loadAndPlay(words, fromIndex, updateIndex)
//        }
//    } else {
//        val notLoadedidx = playWordsWithAnimation(words, fromIndex,  updateIndex)
//        loadAndPlay(words, notLoadedidx,  updateIndex)
//    }
}

// ✅ Function to Animate Highlighting for Each Word
suspend fun playWordsWithAnimation(
    words: List<TextSegment>,
    fromIndex: Int,
    updateIndex: (Int) -> Unit
): Int {
////    if (NonStreamingTtsKokoroEn.TTS.isPlaying) return fromIndex
//    var firstPlaying = true
//    for (index in fromIndex until words.size) {
//        val audioData = NonStreamingTtsKokoroEn.TTS.audioLoaded[index] ?: return index
//        if (index + 1 < words.size) NonStreamingTtsKokoroEn.TTS.audioLoadingQueue.add(
//            Pair(
//                index + 1,
//                words[index + 1].text
//            )
//        )
//        if (index + 2 < words.size) NonStreamingTtsKokoroEn.TTS.audioLoadingQueue.add(
//            Pair(
//                index + 2,
//                words[index + 2].text
//            )
//        )
////        if (!firstPlaying) delay(2000)
//        NonStreamingTtsKokoroEn.generateAudio()
//        updateIndex(index) // Highlight current word
//
//
//
//
//        CoroutineScope(Dispatchers.IO).launch {
//            if (!NonStreamingTtsKokoroEn.TTS.isPlaying) return@launch
//            NonStreamingTtsKokoroEn.TTS.currentWordIdx = index
//            NonStreamingTtsKokoroEn.playAudio(index, audioData.text)
//            if (index == words.size-1) {
//                NonStreamingTtsKokoroEn.TTS.finished = true
//                NonStreamingTtsKokoroEn.TTS.currentWordIdx = 0
//            }
//        }.join()
////        animations.getOrNull(index)
////            ?.animateTo(
////                0f,
////                animationSpec = tween(durationMillis = 10)
////            ) // Fade out
//        firstPlaying = false
//    }
//
//    updateIndex(-1) // Remove highlight after playback
    return -1
}