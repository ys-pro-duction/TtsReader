import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tts.TTSState

class BaseViewModel {
    private val _currentHighlightWordIdx = MutableStateFlow(-1)
    val currentHighlightWordIdx: StateFlow<Int> get() = _currentHighlightWordIdx
    private val _barData = MutableStateFlow(FloatArray(5))
    val barData: StateFlow<FloatArray> get() = _barData
    suspend fun updateAudioVisualBar(barData: FloatArray) {
        _barData.value = barData
    }

    private val _ttsState = MutableStateFlow(TTSState.STOP)
    val ttsState: StateFlow<TTSState> get() = _ttsState

    fun setTTSState(value: TTSState) {
        _ttsState.value = value
    }

    fun stopTTS() {
        setTTSState(TTSState.STOP)

    }

    fun startTTS() {
        setTTSState(TTSState.PLAY)

    }
}