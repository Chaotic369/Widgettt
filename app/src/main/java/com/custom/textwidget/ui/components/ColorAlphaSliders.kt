package com.custom.textwidget.ui.components
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.custom.textwidget.model.WidgetConfig
class ColorAlphaSliders @JvmOverloads constructor(
context: Context,
attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
var onStyleChanged: ((WidgetConfig) -> Unit)? = null
private var currentConfig = WidgetConfig()
private var isBinding = false
private val palette = listOf(
"#FFFFFF", "#E2E8F0", "#A855F7", "#38BDF8", "#10B981",
"#F59E0B", "#EF4444", "#EC4899", "#000432", "#090D16", "#000000", "#3B0764"
)
private val textAlphaSeek: SeekBar
private val bgAlphaSeek: SeekBar
private val gradientCheck: CheckBox
private val gradientAngleSeek: SeekBar
private val shadowRadiusSeek: SeekBar
init {
orientation = VERTICAL
setPadding(dp(14), dp(14), dp(14), dp(14))
background = GradientDrawable().apply {
setColor(Color.parseColor("#080B20"))
cornerRadius = dp(16).toFloat()
setStroke(dp(1), Color.parseColor("#1E2448"))
}
addView(sectionHeader("Colors, Transparency & Effects"))
addView(subLabel("Text Color Swatches"))
addView(createColorRow { hex ->
if (!isBinding) {
currentConfig = currentConfig.copy(textColorHex = hex)
onStyleChanged?.invoke(currentConfig)
}
})
val (tLayout, tSeek) = createSlider("Text Opacity (Alpha)", 255) { value ->
if (!isBinding) {
currentConfig = currentConfig.copy(textAlpha = value)
onStyleChanged?.invoke(currentConfig)
}
}
textAlphaSeek = tSeek
addView(tLayout)
addView(subLabel("Background Primary Color"))
addView(createColorRow { hex ->
if (!isBinding) {
currentConfig = currentConfig.copy(bgColorHex = hex)
onStyleChanged?.invoke(currentConfig)
}
})
val (bgLayout, bSeek) = createSlider("Background Opacity (0 = Fully Transparent)", 255) { value ->
if (!isBinding) {
currentConfig = currentConfig.copy(bgAlpha = value)
onStyleChanged?.invoke(currentConfig)
}
}
bgAlphaSeek = bSeek
addView(bgLayout)
gradientCheck = CheckBox(context).apply {
text = "Enable Dual-Tone Gradient Background"
setTextColor(Color.WHITE)
setOnCheckedChangeListener { _, isChecked ->
if (!isBinding) {
currentConfig = currentConfig.copy(useGradientBg = isChecked)
onStyleChanged?.invoke(currentConfig)
}
}
}
addView(gradientCheck)
addView(subLabel("Gradient Secondary Color"))
addView(createColorRow { hex ->
if (!isBinding) {
currentConfig = currentConfig.copy(bgGradientEndHex = hex)
onStyleChanged?.invoke(currentConfig)
}
})
val (angLayout, aSeek) = createSlider("Gradient Angle (0° - 360°)", 360) { value ->
if (!isBinding) {
currentConfig = currentConfig.copy(gradientAngle = value.toFloat())
onStyleChanged?.invoke(currentConfig)
}
}
gradientAngleSeek = aSeek
addView(angLayout)
val (shLayout, shSeek) = createSlider("Text Drop Shadow Glow", 24) { value ->
if (!isBinding) {
currentConfig = currentConfig.copy(shadowRadiusDp = value.toFloat())
onStyleChanged?.invoke(currentConfig)
}
}
shadowRadiusSeek = shSeek
addView(shLayout)
}
fun bind(config: WidgetConfig) {
isBinding = true
currentConfig = config
textAlphaSeek.progress = config.textAlpha
bgAlphaSeek.progress = config.bgAlpha
gradientCheck.isChecked = config.useGradientBg
gradientAngleSeek.progress = config.gradientAngle.toInt().coerceIn(0, 360)
shadowRadiusSeek.progress = config.shadowRadiusDp.toInt().coerceIn(0, 24)
isBinding = false
}
private fun createColorRow(onSelect: (String) -> Unit): HorizontalScrollView {
val scroll = HorizontalScrollView(context).apply {
isHorizontalScrollBarEnabled = false
}
val row = LinearLayout(context).apply {
orientation = HORIZONTAL
setPadding(0, dp(6), 0, dp(10))
}
for (hex in palette) {
val swatch = View(context).apply {
layoutParams = LayoutParams(dp(34), dp(34)).apply {
rightMargin = dp(10)
}
background = GradientDrawable().apply {
shape = GradientDrawable.OVAL
setColor(Color.parseColor(hex))
setStroke(dp(2), Color.parseColor("#64748B"))
}
setOnClickListener { onSelect(hex) }
}
row.addView(swatch)
}
scroll.addView(row)
return scroll
}
private fun createSlider(
title: String,
maxVal: Int,
onChange: (Int) -> Unit
): Pair<LinearLayout, SeekBar> {
val container = LinearLayout(context).apply {
orientation = VERTICAL
setPadding(0, dp(4), 0, dp(8))
}
val label = TextView(context).apply {
text = title
setTextColor(Color.parseColor("#CBD5E1"))
setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
}
val seek = SeekBar(context).apply {
max = maxVal
setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
if (fromUser) onChange(progress)
}
override fun onStartTrackingTouch(s: SeekBar?) {}
override fun onStopTrackingTouch(s: SeekBar?) {}
})
}
container.addView(label)
container.addView(seek)
return container to seek
}
private fun sectionHeader(title: String): TextView {
return TextView(context).apply {
text = title
setTextColor(Color.parseColor("#A855F7"))
setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
setPadding(0, 0, 0, dp(6))
}
}
private fun subLabel(title: String): TextView {
return TextView(context).apply {
text = title
setTextColor(Color.parseColor("#94A3B8"))
setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
gravity = Gravity.START
}
}
private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
