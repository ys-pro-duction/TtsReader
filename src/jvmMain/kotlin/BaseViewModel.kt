import components.Speaker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import tts.TTSState
import tts.TextToSpeech
import utils.TextSegment
import utils.splitSmartWithDelimiters
import kotlin.math.max
import kotlin.math.min

@OptIn(FlowPreview::class)
class BaseViewModel {
    // Text segments
    private val _sentencesOfTextSegments = MutableStateFlow(listOf<TextSegment>())
    val sentencesOfTextSegments: StateFlow<List<TextSegment>> get() = _sentencesOfTextSegments

    // Raw text
    private val _rawText = MutableStateFlow("")
    val rawText: StateFlow<String> get() = _rawText

    // Current play index
    private val _currentHighlightWordIdx = MutableStateFlow(-1)
    val currentHighlightWordIdx: StateFlow<Int> get() = _currentHighlightWordIdx

    // Visualized bar
    private val _barData = MutableStateFlow(FloatArray(5))
    val barData: StateFlow<FloatArray> get() = _barData

    // Volume
    private val _volume = MutableStateFlow(100f)
    val volume: StateFlow<Float> get() = _volume

    // Speed
    private val _speechSpeed = MutableStateFlow(1f)
    val speechSpeed: StateFlow<Float> get() = _speechSpeed

    // Speaker
    private val _selectedSpeaker: MutableStateFlow<Speaker> = MutableStateFlow(Speaker.Raquel)
    val selectedSpeaker: StateFlow<Speaker> get() = _selectedSpeaker

    // TTS state
    private val _ttsState = MutableStateFlow(TTSState.STOP)
    val ttsState: StateFlow<TTSState> get() = _ttsState

    var textFormating = false

    private val textToSpeech = TextToSpeech()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            speechSpeed.debounce(200).collectLatest { speed ->
                val oldState = ttsState.value
                stopTTS()
                if (oldState == TTSState.PLAY) startTTS()
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            _rawText.collect { text ->
                updateSentences(text)
            }
        }
    }

    fun updateTextValue(string: String) {
        if (string == _rawText.value) return
        textFormating = true
        _rawText.update{
            string
        }
    }

    fun setTTSState(value: TTSState) {
        _ttsState.value = value
    }

    fun stopTTS() {
        if (_ttsState.value != TTSState.PLAY) return
        job?.cancel()
        CoroutineScope(Dispatchers.IO).launch {
            textToSpeech.playAudio(-1, this@BaseViewModel)
        }
        setTTSState(TTSState.STOP)
    }

    var job: Job? = null
    fun startTTS() {
        job = CoroutineScope(Dispatchers.IO).launch {
            waitWhileTextFormating()
            if (_sentencesOfTextSegments.value.isEmpty() || _ttsState.value == TTSState.LOADING) return@launch
            setTTSState(TTSState.PLAY)
            val idx = max(0, currentHighlightWordIdx.value)
            _currentHighlightWordIdx.value = idx
            if (!textToSpeech.isValidText(sentencesOfTextSegments.value[idx])) {
                nextSpeech(false)
                return@launch
            }
            textToSpeech.generateAudioForIdx(
                idx, selectedSpeaker.value.id, speechSpeed.value, sentencesOfTextSegments.value[idx]
            )
            textToSpeech.loadNextInAdvance(this@BaseViewModel)
            val isSuccessfullyCompleted = textToSpeech.playAudio(idx, baseViewModel = this@BaseViewModel)
            if (isSuccessfullyCompleted && idx == max(
                    0,
                    currentHighlightWordIdx.value
                ) && ttsState.value == TTSState.PLAY
            ) {
                delay(600)
                nextSpeech(false)
            }
        }
    }

    fun previousSpeech() {
        if (_currentHighlightWordIdx.value < 0) return
        if (_currentHighlightWordIdx.value != 0) {
            _currentHighlightWordIdx.value = _currentHighlightWordIdx.value - 1
        }
        if (textToSpeech.isValidText(_sentencesOfTextSegments.value[_currentHighlightWordIdx.value])) {
            startTTS()
        }
    }

    suspend fun nextSpeech(byUser: Boolean = true) {
        if (byUser) {
            textToSpeech.playAudio(-1, this@BaseViewModel)
        }
        if (_currentHighlightWordIdx.value < _sentencesOfTextSegments.value.size - 1) {
            _currentHighlightWordIdx.value++
            val idx = min(_sentencesOfTextSegments.value.size, currentHighlightWordIdx.value)
            if (!byUser && textToSpeech.isLoaded(
                    _currentHighlightWordIdx.value,
                    _selectedSpeaker.value.id,
                    _speechSpeed.value,
                    _sentencesOfTextSegments.value[idx]
                )
            ) delay(600)
        } else if (!byUser) {
            _ttsState.value = TTSState.STOP
            return
        }
        startTTS()
    }

    fun updateSentences(textFieldValue: String) {
        _sentencesOfTextSegments.value = splitSmartWithDelimiters(textFieldValue)
        stopTTS()
        _currentHighlightWordIdx.value = -1
        textFormating = false
    }
    suspend fun waitWhileTextFormating(){
        while (textFormating){
            delay(64)
        }
    }

    fun updateSpeed(speed: Float) {
        _speechSpeed.value = speed
    }

    fun updateVolume(volume: Float) {
        _volume.value = volume
        textToSpeech.setVolume(volume)
    }

    fun updateSpeaker(speaker: Speaker) {
        _selectedSpeaker.value = speaker
        val oldState = ttsState.value
        stopTTS()
        if (oldState == TTSState.PLAY) startTTS()
    }

    fun updateAudioVisualBar(barData: FloatArray) {
        _barData.value = barData
    }

    fun restartWholeSpeech() {
        stopTTS()
        _currentHighlightWordIdx.value = -1
        startTTS()
    }
}