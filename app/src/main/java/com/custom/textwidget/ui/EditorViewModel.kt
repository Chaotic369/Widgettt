package com.custom.textwidget.ui
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.custom.textwidget.model.WidgetConfig
class EditorViewModel : ViewModel() {
private val _config = MutableLiveData(WidgetConfig())
val config: LiveData<WidgetConfig> = _config
fun setInitialConfig(initial: WidgetConfig) {
_config.value = initial
}
fun updateConfig(newConfig: WidgetConfig) {
_config.value = newConfig
}
fun updateText(newText: String) {
val current = _config.value ?: WidgetConfig()
if (current.text != newText) {
_config.value = current.copy(text = newText)
}
}
fun updateDraggedOffsets(offsetX: Float, offsetY: Float) {
val current = _config.value ?: WidgetConfig()
_config.value = current.copy(
offsetXPercent = offsetX,
offsetYPercent = offsetY
)
}
}
