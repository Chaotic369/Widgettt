package com.custom.textwidget
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.custom.textwidget.storage.WidgetPrefsRepository
import com.custom.textwidget.ui.EditorViewModel
import com.custom.textwidget.ui.components.ColorAlphaSliders
import com.custom.textwidget.ui.components.FontPickerPanel
import com.custom.textwidget.ui.components.LiveWidgetPreview
import com.custom.textwidget.ui.components.PositionAndPaddingControls
import com.custom.textwidget.ui.components.ShapeAndBorderControls
import com.custom.textwidget.widget.CustomTextWidgetProvider
class WidgetConfigActivity : AppCompatActivity() {
private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
private val viewModel: EditorViewModel by viewModels()
private lateinit var repository: WidgetPrefsRepository
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setResult(Activity.RESULT_CANCELED)
setContentView(R.layout.activity_widget_config)
repository = WidgetPrefsRepository(this)
appWidgetId = intent?.extras?.getInt(
AppWidgetManager.EXTRA_APPWIDGET_ID,
AppWidgetManager.INVALID_APPWIDGET_ID
) ?: AppWidgetManager.INVALID_APPWIDGET_ID
if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
appWidgetId = 1001
}
val initialConfig = repository.loadConfig(appWidgetId)
viewModel.setInitialConfig(initialConfig)
val livePreview = findViewById<LiveWidgetPreview>(R.id.liveWidgetPreview)
val inputWidgetText = findViewById<EditText>(R.id.inputWidgetText)
val fontPanel = findViewById<FontPickerPanel>(R.id.fontPickerPanel)
val shapeControls = findViewById<ShapeAndBorderControls>(R.id.shapeAndBorderControls)
val colorSliders = findViewById<ColorAlphaSliders>(R.id.colorAlphaSliders)
val positionControls = findViewById<PositionAndPaddingControls>(R.id.positionAndPaddingControls)
val btnSaveWidget = findViewById<Button>(R.id.btnSaveWidget)
inputWidgetText.setText(initialConfig.text)
fontPanel.bind(initialConfig)
shapeControls.bind(initialConfig)
colorSliders.bind(initialConfig)
positionControls.bind(initialConfig)
inputWidgetText.addTextChangedListener(object : TextWatcher {
override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
viewModel.updateText(s?.toString() ?: "")
}
override fun afterTextChanged(s: Editable?) {}
})
fontPanel.onTypographyChanged = { updated ->
viewModel.updateConfig(updated)
}
shapeControls.onShapeChanged = { updated ->
viewModel.updateConfig(updated)
}
colorSliders.onStyleChanged = { updated ->
viewModel.updateConfig(updated)
}
positionControls.onPlacementChanged = { updated ->
viewModel.updateConfig(updated)
}
livePreview.onPositionDragged = { normX, normY ->
viewModel.updateDraggedOffsets(normX, normY)
}
viewModel.config.observe(this) { config ->
livePreview.updateConfig(config)
fontPanel.bind(config)
shapeControls.bind(config)
colorSliders.bind(config)
positionControls.bind(config)
}
btnSaveWidget.setOnClickListener {
val finalConfig = viewModel.config.value ?: initialConfig
repository.saveConfig(finalConfig)
val appWidgetManager = AppWidgetManager.getInstance(this)
CustomTextWidgetProvider.updateSingleWidget(this, appWidgetManager, appWidgetId)
val resultValue = Intent().apply {
putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
}
setResult(Activity.RESULT_OK, resultValue)
finish()
}
}
}
