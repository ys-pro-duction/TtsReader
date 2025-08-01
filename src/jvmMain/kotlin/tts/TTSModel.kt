package tts

import com.k2fsa.sherpa.onnx.OfflineTts
import com.k2fsa.sherpa.onnx.OfflineTtsConfig
import com.k2fsa.sherpa.onnx.OfflineTtsKokoroModelConfig
import com.k2fsa.sherpa.onnx.OfflineTtsModelConfig
import java.io.File

object TTSModel {
    private var offlineTts: OfflineTts? = null
    var isGeneratingAudio = false
    //    const val url = "https://hostbet.moreproductive.in/models/tts/kokoro/kokoro-en-v0_19.zip"
    const val url = "http://localhost:8080/kokoro-en-v0_19.zip"

    fun getInstance(): OfflineTts {
        if (offlineTts != null) return offlineTts!!
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
        offlineTts = tts.OfflineTts(config)
        return offlineTts as OfflineTts
    }
    fun freeResources() {
        offlineTts?.release()
    }

    suspend fun isModelExist(): Boolean {
        val dir = "./kokoro-en-v0_19/"
        val model = "${dir}model.onnx"
        val voices = "${dir}voices.bin"
        val tokens = "${dir}tokens.txt"
        val dataDir = "${dir}espeak-ng-data"
        return File(model).exists() && File(voices).exists() && File(tokens).exists() && File(
            dataDir
        ).exists() && File(dir,"DownloadedProof").exists()
    }
}