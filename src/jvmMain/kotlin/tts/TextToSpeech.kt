package tts

import tts.AudioData
import BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utils.TextSegment
import utils.getDuration
import utils.sanitizeString
import utils.toByteArray
import java.util.PriorityQueue
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.FloatControl
import javax.sound.sampled.SourceDataLine
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class TextToSpeech {

    private val loadedAudioData = HashMap<Int, AudioData>()
    var sourceDataLine: SourceDataLine? = null
    private var speakerVolume = 100f
    var currentPlaySessionId = 1L

    suspend fun generateAudioForIdx(
        idx: Int,
        speakerId: Int,
        voiceSpeed: Float,
        textSegment: TextSegment
    ) {
        queue.peek()?.let {
            if (it.first == idx && TTSModel.isGeneratingAudio) {
                while (!isLoaded(idx,speakerId,voiceSpeed,textSegment)){
                    delay(200)
                }
            }
        }
        if (TTSModel.isGeneratingAudio) return
        if (isLoaded(idx, speakerId, voiceSpeed, textSegment)) {
            return
        }
        TTSModel.isGeneratingAudio = true
        val generatedAudio = TTSModel.getInstance().generate(
            sanitizeString(textSegment.normalizedText),
            speakerId,
            voiceSpeed
        )
        val audioDuration = generatedAudio.getDuration()
        loadedAudioData[idx] = AudioData(
            textSegment,
            generatedAudio.samples.toByteArray(),
            generatedAudio.sampleRate,
            audioDuration,
            speakerId,
            voiceSpeed
        )
        TTSModel.isGeneratingAudio = false
    }

    suspend fun playAudio(index: Int, baseViewModel: BaseViewModel): Boolean {
        sourceDataLine?.flush()
        val thisCurrentPlayId = System.currentTimeMillis()
        currentPlaySessionId = thisCurrentPlayId
        if (index < 0) return false
        loadedAudioData[index]?.let { audioData ->
            val line = getAudioSourceLine(audioData)
            val numBars = 5
            val chunkSize = 1024 * 10

            for (i in 0 until audioData.byteArray.size step chunkSize) {
                if (thisCurrentPlayId != currentPlaySessionId) return false
                val byteArray =
                    audioData.byteArray.copyOfRange(i, min(i + chunkSize, audioData.byteArray.size))
                line.write(byteArray, 0, byteArray.size)
                CoroutineScope(Dispatchers.Default).launch {
                    val segmentSize = byteArray.size / numBars
                    val barData = FloatArray(numBars)
                    for (i in 0 until numBars) {
                        val start = i * segmentSize
                        val end = if (i == numBars - 1) byteArray.size else (i + 1) * segmentSize
                        val subArray = byteArray.copyOfRange(start, end)
                        barData[i] = max(subArray.map { abs(it.toInt()) }.average() - 20, 0.0).toFloat()
                    }
                    baseViewModel.updateAudioVisualBar(barData)
                }
            }
        }
        return true
    }

    fun isLoaded(
        idx: Int,
        speakerId: Int,
        voiceSpeed: Float,
        textSegment: TextSegment
    ): Boolean {
        if (!isValidText(textSegment)) return true
        val audioData = loadedAudioData.getOrDefault(idx, null)
        if (audioData == null) return false
        return audioData.speakerId == speakerId && audioData.voiceSpeed == voiceSpeed && audioData.textSegment === textSegment

    }

    fun isValidText(textSegment: TextSegment): Boolean {
        return textSegment.normalizedText.contains(Regex("[a-zA-Z0-9]"))
    }


    fun getAudioSourceLine(audioData: AudioData): SourceDataLine {
        if (sourceDataLine != null) return sourceDataLine!!
        val format = AudioFormat(audioData.sampleRate.toFloat(), 16, 1, true, false)
        val info = DataLine.Info(SourceDataLine::class.java, format)
        sourceDataLine = AudioSystem.getLine(info) as SourceDataLine
        sourceDataLine?.open(format)
        setVolume(speakerVolume)
        sourceDataLine!!.start()
        return sourceDataLine!!
    }

    fun setVolume(volume: Float) {
        val volume = volume / 100
        val gainControl = sourceDataLine?.getControl(FloatControl.Type.MASTER_GAIN) ?: return
        gainControl as FloatControl

        val min = gainControl.minimum // Typically -80.0 dB (mute)
        val max = gainControl.maximum // Typically 6.0 dB (full boost)


        // Convert 0.0 - 1.0 range to dB scale
        val volumeDB = -40 + (max + 40) * volume

        // Set the new volume
        gainControl.value = if (volumeDB == -40f) -80f else volumeDB
    }
    val queue = PriorityQueue<Pair<Int, TextSegment>>(compareBy { it.first })
    fun loadNextInAdvance(baseViewModel: BaseViewModel){
        baseViewModel.currentHighlightWordIdx.value.let {currentIdx->
            baseViewModel.sentencesOfTextSegments.value.size.let { size->
                if (currentIdx+1 < size)queue.add(Pair(currentIdx+1, baseViewModel.sentencesOfTextSegments.value[currentIdx+1]))
                if (currentIdx+2 < size)queue.add(Pair(currentIdx+2, baseViewModel.sentencesOfTextSegments.value[currentIdx+2]))
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            while (queue.isNotEmpty()){
                if (baseViewModel.ttsState.value != TTSState.PLAY) return@launch
                while (TTSModel.isGeneratingAudio){
                    delay(200)
                }
                val pair = queue.peek() ?: break
                if (pair.first !in baseViewModel.currentHighlightWordIdx.value+1..baseViewModel.currentHighlightWordIdx.value+2) {
                    queue.poll()
                    continue
                }
                if (isLoaded(pair.first, baseViewModel.selectedSpeaker.value.id, baseViewModel.speechSpeed.value, pair.second)) {
                    queue.poll()
                    continue
                } else {
                    generateAudioForIdx(pair.first, baseViewModel.selectedSpeaker.value.id, baseViewModel.speechSpeed.value, pair.second)
                    queue.poll()
                }
            }
        }
    }


}