package utils

import com.k2fsa.sherpa.onnx.GeneratedAudio
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun FloatArray.toByteArray(): ByteArray {
    val buffer = ByteBuffer.allocate(size * 2) // 16-bit PCM
    buffer.order(ByteOrder.LITTLE_ENDIAN)

    for (sample in this) {
        val pcmSample = (sample * Short.MAX_VALUE).toInt().toShort()
        buffer.putShort(pcmSample)
    }

    return buffer.array()
}

fun sanitizeString(input: String): String {
    val string = input.filter { char ->
        char.isLetterOrDigit() || char.isWhitespace() || char == '.' || char == '\'' || char == ','
    }.replace("\n", " ")
    return if (string == " ") ""
    else string
}

fun GeneratedAudio.getDuration(): Float {
    return samples.size / sampleRate.toFloat()
}
