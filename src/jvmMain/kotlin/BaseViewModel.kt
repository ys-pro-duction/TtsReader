import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import components.Speaker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import tts.TTSState
import tts.TextToSpeech
import utils.TextSegment
import utils.splitSmartWithDelimiters
import java.util.prefs.Preferences
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

    // Highlight color
    private val _highlightColorHue = MutableStateFlow(1f)
    val highlightColorHue: StateFlow<Float> get() = _highlightColorHue

    // Highlight color
    private val _highlightColorAlpha = MutableStateFlow(0.5f)
    val highlightColorAlpha: StateFlow<Float> get() = _highlightColorAlpha

    // Dark mode state
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> get() = _isDarkMode

    var textFormating = false

    private val textToSpeech = TextToSpeech()

    init {
        with(PreferencesSettings(Preferences.userRoot()) as Settings) {
            _rawText.value = getString("rawText", "")
            _currentHighlightWordIdx.value = getInt("currentHighlightWordIdx", -1)
            _volume.value = getFloat("volume", 100f)
            _speechSpeed.value = getFloat("speechSpeed", 1f)
            _selectedSpeaker.value = Speaker.getById(getInt("selectedSpeaker", Speaker.Raquel.id))
            _highlightColorHue.value = getFloat("highlightColorHue", 1f)
            _highlightColorAlpha.value = getFloat("highlightColorAlpha", 0.5f)
            _isDarkMode.value = getBoolean("isDarkMode", true)
            CoroutineScope(Dispatchers.Default).launch {
                launch { _rawText.collect { putString("rawText", it) } }
                launch { _currentHighlightWordIdx.collect { putInt("currentHighlightWordIdx", it) } }
                launch { _volume.collect { putFloat("volume", it) } }
                launch { _speechSpeed.collect { putFloat("speechSpeed", it) } }
                launch { _selectedSpeaker.collect { putInt("selectedSpeaker", it.id) } }
                launch { _highlightColorHue.collect { putFloat("highlightColorHue", it) } }
                launch { _highlightColorAlpha.collect { putFloat("highlightColorAlpha", it) } }
                launch { _isDarkMode.collect { putBoolean("isDarkMode", it) } }
            }
        }


        CoroutineScope(Dispatchers.Default).launch {
            launch {
                speechSpeed.debounce(200).collectLatest { speed ->
                    val oldState = ttsState.value
                    stopTTS()
                    if (oldState == TTSState.PLAY) startTTS()
                }
            }
            launch {
                _rawText.collect { text ->
                    updateSentences(text)
                }
            }
        }
    }

    fun updateTextValue(string: String) {
        if (string == _rawText.value) return
        textFormating = true
        _rawText.update {
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
        job?.cancel(null)
        job = CoroutineScope(Dispatchers.IO).launch {
            waitWhileTextFormating()
            if (_ttsState.value == TTSState.LOADING) return@launch
            setTTSState(TTSState.PLAY)
            val idx = max(0, currentHighlightWordIdx.value)
            _currentHighlightWordIdx.value = idx
            if (idx < 0 || idx >= sentencesOfTextSegments.value.size ||!textToSpeech.isValidText(sentencesOfTextSegments.value[idx])) {
                delay(200)
                if (isActive) nextSpeech(false)
                return@launch
            }
            textToSpeech.generateAudioForIdx(
                idx, selectedSpeaker.value.id, speechSpeed.value, sentencesOfTextSegments.value[idx]
            )
            textToSpeech.loadNextInAdvance(this@BaseViewModel)
            val isSuccessfullyCompleted = textToSpeech.playAudio(idx, baseViewModel = this@BaseViewModel)
            if (isSuccessfullyCompleted && idx == max(
                    0, currentHighlightWordIdx.value
                ) && ttsState.value == TTSState.PLAY
            ) {
                delay(if (speechSpeed.value <= 1f) 500 else (500 * (1 / speechSpeed.value)).toLong())
                if (this.isActive) nextSpeech(false)
            }
        }
    }

    fun previousSpeech() {
        if (_currentHighlightWordIdx.value < 0) return
        if (_currentHighlightWordIdx.value != 0) {
            _currentHighlightWordIdx.value = _currentHighlightWordIdx.value - 1
        }
        startTTS()
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

    suspend fun waitWhileTextFormating() {
        while (textFormating) {
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

    fun updateHighlightColorHue(hue: Float) {
        _highlightColorHue.value = hue
    }

    fun updateHilightColorAlpha(colorAlpha: Float) {
        _highlightColorAlpha.value = colorAlpha
    }

    fun setDarkMode(isDarkMode: Boolean) {
        _isDarkMode.value = isDarkMode
    }
}