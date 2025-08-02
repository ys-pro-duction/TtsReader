package tts

import com.k2fsa.sherpa.onnx.GeneratedAudio
import com.k2fsa.sherpa.onnx.OfflineTtsCallback
import com.k2fsa.sherpa.onnx.OfflineTtsConfig
import java.io.File

class OfflineTts(config: OfflineTtsConfig) {
    private var ptr: Long = 0

    init {
        System.load(File("app/resources/sherpa-onnx-jni.dll").absolutePath)
        ptr = newFromFile(config)
    }

    fun getSampleRate(): Int = getSampleRate(ptr)

    fun generate(text: String, sid: Int, speed: Float): GeneratedAudio {
        val arr = generateImpl(ptr, text, sid, speed)
        val samples = arr[0] as FloatArray
        val sampleRate = arr[1] as Int
        return GeneratedAudio(samples, sampleRate)
    }

    fun generateWithCallback(
        text: String, sid: Int, speed: Float, callback: OfflineTtsCallback
    ): GeneratedAudio {
        val arr = generateWithCallbackImpl(ptr, text, sid, speed, callback)
        val samples = arr[0] as FloatArray
        val sampleRate = arr[1] as Int
        return GeneratedAudio(samples, sampleRate)
    }

    fun release() {
        if (this.ptr == 0L) return
        delete(this.ptr)
        this.ptr = 0L
    }

    private external fun delete(ptr: Long)
    private external fun getSampleRate(ptr: Long): Int
    private external fun getNumSpeakers(ptr: Long): Int
    private external fun generateImpl(ptr: Long, text: String, sid: Int, speed: Float): Array<Any>
    private external fun generateWithCallbackImpl(
        ptr: Long,
        text: String,
        sid: Int,
        speed: Float,
        callback: OfflineTtsCallback
    ): Array<Any>

    private external fun newFromFile(config: OfflineTtsConfig): Long
}