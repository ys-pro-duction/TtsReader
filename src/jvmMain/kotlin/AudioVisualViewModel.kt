import kotlinx.coroutines.flow.MutableStateFlow

data class BarData(val b1: Float,val b2: Float,val b3: Float,val b4: Float,val b5: Float)
class AudioVisualViewModel {
    val _barData = MutableStateFlow(BarData(10.0f,20.0f,30.0f,40.0f,50.0f))
    val barData = MutableStateFlow(FloatArray(5))
}