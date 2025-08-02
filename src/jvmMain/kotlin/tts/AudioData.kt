package tts

import utils.TextSegment

class AudioData(
    val textSegment: TextSegment,
    val byteArray: ByteArray,
    val sampleRate: Int,
    val audioDuration: Float,
    val speakerId: Int,
    val voiceSpeed: Float,
)