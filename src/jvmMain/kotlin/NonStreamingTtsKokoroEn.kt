import com.k2fsa.sherpa.onnx.OfflineTts
import com.k2fsa.sherpa.onnx.OfflineTtsConfig
import com.k2fsa.sherpa.onnx.OfflineTtsKokoroModelConfig
import com.k2fsa.sherpa.onnx.OfflineTtsModelConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.LinkedList
import java.util.Queue
import java.util.logging.Level
import java.util.logging.Logger
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.FloatControl
import javax.sound.sampled.SourceDataLine
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object NonStreamingTtsKokoroEn {

    fun generateAudio() {
        if (TTS.isProcessing) return
        TTS.isProcessing = true
        CoroutineScope(Dispatchers.IO).launch {
            while (TTS.audioLoadingQueue.isNotEmpty()) {
                val (index, text) = TTS.audioLoadingQueue.poll()
                generateAudio(text, index)
            }
            TTS.isProcessing = false

        }
    }

    fun generateAudio(text: String, index: Int) {
//        if (TTS.isProcessing) return
//        TTS.isProcessing = true
//        CoroutineScope(Dispatchers.IO).launch {
        if (TTS.isLoaded(index, text)) {
            TTS.isProcessing = false
//                return@launch
            return
        }
        val tts = TTS.getInstance()

        val generationText = sanitizeString(text)
        val audio = if (generationText.isNotEmpty()) tts.generate(
            generationText, TTS.SID,
//            Random.nextInt(0,10),
            TTS.SPEED
        ) else null
        val audioDuration = audio?.let { audio.samples.size / audio.sampleRate.toFloat() } ?: 0f
        TTS.audioLoaded[index] = AudioData(
            text,
            audio?.let { floatArrayToByteArray(audio.samples) } ?: ByteArray(0),
            audio?.sampleRate ?: 0,
            audioDuration,
            TTS.SID,
            TTS.SPEED)
        println("$index == $audioDuration == $text")
//            TTS.isProcessing = false
    }

    var baseViewModel: BaseViewModel? = null
    suspend fun playAudio(index: Int = TTS.currentWordIdx, text: String = ""): Boolean {
        Logger.getGlobal().log(Level.INFO, "playAudio start $text")
        TTS.audioLoaded[index]?.let { audioData ->
            val line = TTS.getAudioSourceLine(audioData)
//            val chunkSize =
//                (audioData.sampleRate.toFloat() * audioData.audioDuration * 2).toInt() // Each sample is 2 bytes
//            var i = 0
//            while (i < audioData.audioFloatData.size) {
//                line.write(
//                    audioData.audioFloatData, i,
//                    min(
//                        chunkSize.toDouble(),
//                        (audioData.audioFloatData.size - i).toDouble()
//                    ).toInt()
//                )
////                Thread.sleep((audioData.audioDuration * 1000).toLong())
////                delay((audioData.audioDuration *1000).toLong())
//                i += chunkSize
            println()
            val numBars = 5
            val chunkSize = 1024*10
            for (i in 0 until audioData.byteArray.size step chunkSize) {
                val byteArray =
                    audioData.byteArray.copyOfRange(i, min(i + chunkSize, audioData.byteArray.size))
                val segmentSize = byteArray.size / numBars
                val barData = FloatArray(numBars)
                for (i in 0 until numBars) {
                    val start = i * segmentSize
                    val end = if (i == numBars - 1) byteArray.size else (i + 1) * segmentSize
                    val subArray = byteArray.copyOfRange(start, end)
                    barData[i] = max(subArray.map { abs(it.toInt()) }.average()-20,0.0).toFloat()
                }

                println(barData.joinToString(", ") { String.format("%.2f", it) })

                line.write(byteArray, 0, byteArray.size)
                baseViewModel?.updateAudioVisualBar(barData)
            }


//            line.write(audioData.audioFloatData, 0, audioData.audioFloatData.size)
        }
//            line.drain()
//            line.close()
        Logger.getGlobal().log(Level.INFO, "playAudio end")

//        } ?: return false
        return true

    }

    private fun sanitizeString(input: String): String {
        val string = input.filter { char ->
            char.isLetterOrDigit() || char.isWhitespace() || char == '.' || char == '\'' || char == ','
        }.replace("\n", " ")
        return if (string == " ") ""
        else string
    }

    private fun floatArrayToByteArray(samples: FloatArray): ByteArray {
        val buffer = ByteBuffer.allocate(samples.size * 2) // 16-bit PCM
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        for (sample in samples) {
            val pcmSample = (sample * Short.MAX_VALUE).toInt().toShort()
            buffer.putShort(pcmSample)
        }

        return buffer.array()
    }


    object TTS {
        var finished: Boolean = false
        var currentWordIdx = 0
        var isProcessing = false
        var isPlaying = false
        val audioLoaded = HashMap<Int, AudioData>()
        val audioLoadingQueue: Queue<Pair<Int, String>> = LinkedList()
        var line: SourceDataLine? = null
        var SID = 0
        var SPEED = 0.5f
        var speakerVolume = 1.0f
        private var tts: OfflineTts? = null
        fun getInstance(): OfflineTts {
            if (tts != null) return tts!!
            val dir = "./kokoro-en-v0_19/"
            val model = "${dir}model.onnx"
            val voices = "${dir}voices.bin"
            val tokens = "${dir}tokens.txt"
            val dataDir = "${dir}espeak-ng-data"
            val kokoroModelConfig =
                OfflineTtsKokoroModelConfig.builder().setModel(model).setVoices(voices)
                    .setTokens(tokens).setDataDir(dataDir).build()

            val modelConfig =
                OfflineTtsModelConfig.builder().setKokoro(kokoroModelConfig).setNumThreads(4)
                    .setProvider("cuda").setDebug(true).build()

            val config = OfflineTtsConfig.builder().setModel(modelConfig).build()
            val libPath = File("libs/sherpa-onnx-jni.dll").absolutePath
            println(libPath)
            System.setProperty("java.library.path", libPath)
            System.load(libPath)
            tts = OfflineTts(config)
            return tts as OfflineTts
        }

        fun freeResources() {
            tts?.release()
        }

        fun getAudioSourceLine(audioData: AudioData): SourceDataLine {
            if (line != null) return line!!
            val format = AudioFormat(audioData.sampleRate.toFloat(), 16, 1, true, false)
            val info = DataLine.Info(SourceDataLine::class.java, format)
            line = AudioSystem.getLine(info) as SourceDataLine
            line!!.open(format)
            setVolume(speakerVolume)
            line!!.start()
            return line!!
        }

        fun setVolume(volume: Float) {
            this.speakerVolume = volume
            val gainControl = line?.getControl(FloatControl.Type.MASTER_GAIN) ?: return
            gainControl as FloatControl
            val min = gainControl.minimum // Typically -80.0 dB (mute)
            val max = gainControl.maximum // Typically 6.0 dB (full boost)

            // Convert 0.0 - 1.0 range to dB scale
            val volumeDB = min + (max - min) * volume

            // Set the new volume
            gainControl.value = volumeDB
        }

        fun isLoaded(index: Int, s: String): Boolean {
            return (audioLoaded[index] != null && audioLoaded[index]?.speakerId == SID && audioLoaded[index]?.voiceSpeed == SPEED && audioLoaded[index]?.text == s)
        }

        fun stopAudio() {
            isPlaying = false
            line?.stop()
            line?.flush()
            line = null
//            isProcessing = false
//            audioLoadingQueue.clear()
        }
    }
}