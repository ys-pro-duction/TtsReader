package tts

import com.k2fsa.sherpa.onnx.OfflineTtsConfig
import com.k2fsa.sherpa.onnx.OfflineTtsKokoroModelConfig
import com.k2fsa.sherpa.onnx.OfflineTtsModelConfig
import com.k2fsa.sherpa.onnx.OfflineTts
import java.io.File

object TTSModel {
    private var offlineTts: OfflineTts? = null
    var isGeneratingAudio = false
        const val url = "https://hostbet.moreproductive.in/models/tts/kokoro/kokoro-en-v0_19.zip"
//    const val url = "http://localhost:8080/kokoro-en-v0_19.zip"
    val modelDir get() = File(System.getProperty("user.home"), ".TtsReader").apply { mkdirs() }
    fun getInstance(): OfflineTts {
        if (offlineTts != null) return offlineTts!!
        val model = File(modelDir,"model.onnx").absolutePath
        val voices = File(modelDir,"voices.bin").absolutePath
        val tokens = File(modelDir,"tokens.txt").absolutePath
        val dataDir = File(modelDir,"espeak-ng-data").absolutePath
        val kokoroModelConfig =
            OfflineTtsKokoroModelConfig.builder().setModel(model).setVoices(voices)
                .setTokens(tokens).setDataDir(dataDir).build()

        val modelConfig =
            OfflineTtsModelConfig.builder().setKokoro(kokoroModelConfig).setNumThreads(4)
                .setDebug(false).build()

        val config = OfflineTtsConfig.builder().setModel(modelConfig).build()
        offlineTts = OfflineTts(config)
        return offlineTts!!
    }
    fun freeResources() {
        offlineTts?.release()
    }

    suspend fun isModelExist(): Boolean {
        val model = File(modelDir,"model.onnx").absolutePath
        val voices = File(modelDir,"voices.bin").absolutePath
        val tokens = File(modelDir,"tokens.txt").absolutePath
        val dataDir = File(modelDir,"espeak-ng-data").absolutePath
        return File(model).exists() && File(voices).exists() && File(tokens).exists() && File(
            dataDir
        ).exists() && File(modelDir,"DownloadedProof").exists()
    }
}