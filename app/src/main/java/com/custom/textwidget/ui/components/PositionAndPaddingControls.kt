package com.custom.textwidget.ui.components
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.custom.textwidget.model.TextAlignment
import com.custom.textwidget.model.WidgetConfig
class PositionAndPaddingControls @JvmOverloads constructor(
context: Context,
attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
var onPlacementChanged: ((WidgetConfig) -> Unit)? = null
private var currentConfig = WidgetConfig()
private var isBinding = false
private val alignButtons = mutableMapOf<TextAlignment, Button>()
private val offsetXSeek: SeekBar
private val offsetYSeek: SeekBar
private val rotationSeek: SeekBar
private val paddingSeek: SeekBar
init {
orientation = VERTICAL
setPadding(dp(14), dp(14), dp(14), dp(14))
background = GradientDrawable().apply {
setColor(Color.parseColor("#080B20"))
cornerRadius = dp(16).toFloat()
setStroke(dp(1), Color.parseColor("#1E2448"))
}
val header = TextView(context).apply {
text = "Placement, Offset & Rotation (Or Drag Preview)"
setTextColor(Color.parseColor("#A855F7"))
setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
setPadding(0, 0, 0, dp(8))
}
addView(header)
val alignRow = LinearLayout(context).apply {
orientation = HORIZONTAL
setPadding(0, 0, 0, dp(8))
}
for (alignment in TextAlignment.values()) {
val btn = Button(context).apply {
text = alignment.displayName
isAllCaps = false
setTextColor(Color.WHITE)
setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
layoutParams = LayoutParams(0, dp(38), 1f).apply {
rightMargin = dp(6)
}
setOnClickListener {
if (!isBinding) {
currentConfig = currentConfig.copy(textAlignment = alignment)
updateAlignHighlight(alignment)
onPlacementChanged?.invoke(currentConfig)
}
}
}
alignButtons[alignment] = btn
alignRow.addView(btn)
}
addView(alignRow)
val (xWrap, xSeek) = createSlider("Horizontal Position Offset (-100% to +100%)", 200) { p ->
if (!isBinding) {
currentConfig = currentConfig.copy(offsetXPercent = (p - 100).toFloat())
onPlacementChanged?.invoke(currentConfig)
}
}
offsetXSeek = xSeek
addView(xWrap)
val (yWrap, ySeek) = createSlider("Vertical Position Offset (-100% to +100%)", 200) { p ->
if (!isBinding) {
currentConfig = currentConfig.copy(offsetYPercent = (p - 100).toFloat())
onPlacementChanged?.invoke(currentConfig)
}
}
offsetYSeek = ySeek
addView(yWrap)
val (rotWrap, rSeek) = createSlider("Text Rotation Angle (-180° to +180°)", 360) { p ->
if (!isBinding) {
currentConfig = currentConfig.copy(rotationDegrees = (p - 180).toFloat())
onPlacementChanged?.invoke(currentConfig)
}
}
rotationSeek = rSeek
addView(rotWrap)
val (padWrap, pSeek) = createSlider("Inner Margin / Padding (0dp - 64dp)", 64) { p ->
if (!isBinding) {
currentConfig = currentConfig.copy(paddingDp = p.toFloat())
onPlacementChanged?.invoke(currentConfig)
}
}
paddingSeek = pSeek
addView(padWrap)
val resetBtn = Button(context).apply {
text = "Center & Reset Position"
isAllCaps = false
setTextColor(Color.WHITE)
background = GradientDrawable().apply {
setColor(Color.parseColor("#1E293B"))
cornerRadius = dp(10).toFloat()
}
setOnClickListener {
currentConfig = currentConfig.copy(
offsetXPercent = 0f,
offsetYPercent = 0f,
rotationDegrees = 0f
)
bind(currentConfig)
onPlacementChanged?.invoke(currentConfig)
}
}
addView(resetBtn)
}
fun bind(config: WidgetConfig) {
isBinding = true
currentConfig = config
updateAlignHighlight(config.textAlignment)
offsetXSeek.progress = (config.offsetXPercent + 100f).toInt().coerceIn(0, 200)
offsetYSeek.progress = (config.offsetYPercent + 100f).toInt().coerceIn(0, 200)
rotationSeek.progress = (config.rotationDegrees + 180f).toInt().coerceIn(0, 360)
paddingSeek.progress = config.paddingDp.toInt().coerceIn(0, 64)
isBinding = false
}
private fun updateAlignHighlight(selected: TextAlignment) {
alignButtons.forEach { (align, btn) ->
val active = align == selected
btn.background = GradientDrawable().apply {
setColor(if (active) Color.parseColor("#A855F7") else Color.parseColor("#131836"))
cornerRadius = dp(10).toFloat()
}
}
}
private fun createSlider(
title: String,
maxVal: Int,
onChange: (Int) -> Unit
): Pair<LinearLayout, SeekBar> {
val wrap = LinearLayout(context).apply {
orientation = VERTICAL
setPadding(0, dp(4), 0, dp(6))
}
val tv = TextView(context).apply {
text = title
setTextColor(Color.parseColor("#CBD5E1"))
setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
}
val sb = SeekBar(context).apply {
max = maxVal
setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
if (fromUser) onChange(progress)
}
override fun onStartTrackingTouch(s: SeekBar?) {}
override fun onStopTrackingTouch(s: SeekBar?) {}
})
}
wrap.addView(tv)
wrap.addView(sb)
return wrap to sb
}
private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
