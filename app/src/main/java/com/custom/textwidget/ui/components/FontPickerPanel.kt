package com.custom.textwidget.ui.components
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.Button
import android.widget.CheckBox
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.custom.textwidget.model.WidgetConfig
class FontPickerPanel @JvmOverloads constructor(
context: Context,
attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
var onTypographyChanged: ((WidgetConfig) -> Unit)? = null
private var currentConfig = WidgetConfig()
private var isBinding = false
private val fonts = listOf(
"Inter",
"JetBrainsMono",
"PlayfairDisplay",
"Sans-Serif",
"Serif",
"Monospace",
"Cursive"
)
private val fontButtons = mutableMapOf<String, Button>()
private val sizeSeek: SeekBar
private val letterSpacingSeek: SeekBar
private val lineSpacingSeek: SeekBar
private val boldCheck: CheckBox
private val italicCheck: CheckBox
init {
orientation = VERTICAL
setPadding(dp(14), dp(14), dp(14), dp(14))
background = GradientDrawable().apply {
setColor(Color.parseColor("#080B20"))
cornerRadius = dp(16).toFloat()
setStroke(dp(1), Color.parseColor("#1E2448"))
}
val header = TextView(context).apply {
text = "Typography & Font Controls"
setTextColor(Color.parseColor("#A855F7"))
setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
setPadding(0, 0, 0, dp(8))
}
addView(header)
val fontScroll = HorizontalScrollView(context).apply {
isHorizontalScrollBarEnabled = false
}
val fontRow = LinearLayout(context).apply {
orientation = HORIZONTAL
}
for (fontName in fonts) {
val btn = Button(context).apply {
text = fontName
isAllCaps = false
setTextColor(Color.WHITE)
setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, dp(38)).apply {
rightMargin = dp(8)
}
setOnClickListener {
if (!isBinding) {
currentConfig = currentConfig.copy(fontFamily = fontName)
updateSelectedFontButton(fontName)
onTypographyChanged?.invoke(currentConfig)
}
}
}
fontButtons[fontName] = btn
fontRow.addView(btn)
}
fontScroll.addView(fontRow)
addView(fontScroll)
val styleRow = LinearLayout(context).apply {
orientation = HORIZONTAL
setPadding(0, dp(8), 0, dp(4))
}
boldCheck = CheckBox(context).apply {
text = "Bold"
setTextColor(Color.WHITE)
setOnCheckedChangeListener { _, isChecked ->
if (!isBinding) {
currentConfig = currentConfig.copy(isBold = isChecked)
onTypographyChanged?.invoke(currentConfig)
}
}
}
italicCheck = CheckBox(context).apply {
text = "Italic"
setTextColor(Color.WHITE)
setOnCheckedChangeListener { _, isChecked ->
if (!isBinding) {
currentConfig = currentConfig.copy(isItalic = isChecked)
onTypographyChanged?.invoke(currentConfig)
}
}
}
styleRow.addView(boldCheck)
styleRow.addView(italicCheck)
addView(styleRow)
val (sizeContainer, sSeek) = createSlider("Font Size (10sp - 72sp)", 62) { progress ->
if (!isBinding) {
currentConfig = currentConfig.copy(textSizeSp = (10 + progress).toFloat())
onTypographyChanged?.invoke(currentConfig)
}
}
sizeSeek = sSeek
addView(sizeContainer)
val (letterContainer, lSeek) = createSlider("Letter Spacing", 40) { progress ->
if (!isBinding) {
val spacing = (progress - 10) / 100f
currentConfig = currentConfig.copy(letterSpacing = spacing)
onTypographyChanged?.invoke(currentConfig)
}
}
letterSpacingSeek = lSeek
addView(letterContainer)
val (lineContainer, lnSeek) = createSlider("Line Spacing", 30) { progress ->
if (!isBinding) {
currentConfig = currentConfig.copy(lineSpacingExtraDp = progress.toFloat())
onTypographyChanged?.invoke(currentConfig)
}
}
lineSpacingSeek = lnSeek
addView(lineContainer)
}
fun bind(config: WidgetConfig) {
isBinding = true
currentConfig = config
updateSelectedFontButton(config.fontFamily)
boldCheck.isChecked = config.isBold
italicCheck.isChecked = config.isItalic
sizeSeek.progress = (config.textSizeSp - 10f).toInt().coerceIn(0, 62)
letterSpacingSeek.progress = ((config.letterSpacing * 100f) + 10f).toInt().coerceIn(0, 40)
lineSpacingSeek.progress = config.lineSpacingExtraDp.toInt().coerceIn(0, 30)
isBinding = false
}
private fun updateSelectedFontButton(selected: String) {
fontButtons.forEach { (name, button) ->
val active = name.equals(selected, ignoreCase = true)
button.background = GradientDrawable().apply {
setColor(if (active) Color.parseColor("#A855F7") else Color.parseColor("#131836"))
cornerRadius = dp(10).toFloat()
}
}
}
private fun createSlider(
label: String,
maxVal: Int,
onChanged: (Int) -> Unit
): Pair<LinearLayout, SeekBar> {
val wrap = LinearLayout(context).apply {
orientation = VERTICAL
setPadding(0, dp(4), 0, dp(6))
}
val tv = TextView(context).apply {
text = label
setTextColor(Color.parseColor("#CBD5E1"))
setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
}
val sb = SeekBar(context).apply {
max = maxVal
setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
if (fromUser) onChanged(progress)
}
override fun onStartTrackingTouch(seekBar: SeekBar?) {}
override fun onStopTrackingTouch(seekBar: SeekBar?) {}
})
}
wrap.addView(tv)
wrap.addView(sb)
return wrap to sb
}
private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
