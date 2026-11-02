package com.custom.textwidget.ui.components
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.custom.textwidget.model.WidgetConfig
import com.custom.textwidget.model.WidgetShape
class ShapeAndBorderControls @JvmOverloads constructor(
context: Context,
attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
var onShapeChanged: ((WidgetConfig) -> Unit)? = null
private var currentConfig = WidgetConfig()
private var isBinding = false
private val shapeButtons = mutableMapOf<WidgetShape, Button>()
private val cornerSeek: SeekBar
private val polygonSidesSeek: SeekBar
private val borderWidthSeek: SeekBar
private val borderAlphaSeek: SeekBar
private val borderColors = listOf(
"#A855F7", "#38BDF8", "#FFFFFF", "#10B981", "#F59E0B", "#EF4444", "#EC4899", "#000000"
)
init {
orientation = VERTICAL
setPadding(dp(14), dp(14), dp(14), dp(14))
background = GradientDrawable().apply {
setColor(Color.parseColor("#080B20"))
cornerRadius = dp(16).toFloat()
setStroke(dp(1), Color.parseColor("#1E2448"))
}
val header = TextView(context).apply {
text = "Shape Sculpting & Border Geometry"
setTextColor(Color.parseColor("#A855F7"))
setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
setPadding(0, 0, 0, dp(8))
}
addView(header)
val shapeScroll = HorizontalScrollView(context).apply {
isHorizontalScrollBarEnabled = false
}
val shapeRow = LinearLayout(context).apply {
orientation = HORIZONTAL
setPadding(0, 0, 0, dp(8))
}
for (shape in WidgetShape.values()) {
val btn = Button(context).apply {
text = shape.displayName
isAllCaps = false
setTextColor(Color.WHITE)
setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, dp(38)).apply {
rightMargin = dp(8)
}
setOnClickListener {
if (!isBinding) {
currentConfig = currentConfig.copy(shape = shape)
updateShapeHighlight(shape)
onShapeChanged?.invoke(currentConfig)
}
}
}
shapeButtons[shape] = btn
shapeRow.addView(btn)
}
shapeScroll.addView(shapeRow)
addView(shapeScroll)
val (cWrap, cSeek) = createSlider("Corner Radius / Cut Size (0dp - 80dp)", 80) { p ->
if (!isBinding) {
currentConfig = currentConfig.copy(cornerRadiusDp = p.toFloat())
onShapeChanged?.invoke(currentConfig)
}
}
cornerSeek = cSeek
addView(cWrap)
val (polyWrap, pSeek) = createSlider("Polygon / Starburst Points (3 - 12)", 9) { p ->
if (!isBinding) {
currentConfig = currentConfig.copy(polygonSides = p + 3)
onShapeChanged?.invoke(currentConfig)
}
}
polygonSidesSeek = pSeek
addView(polyWrap)
val (bwWrap, bwSeek) = createSlider("Border Stroke Thickness (0dp - 16dp)", 16) { p ->
if (!isBinding) {
currentConfig = currentConfig.copy(borderWidthDp = p.toFloat())
onShapeChanged?.invoke(currentConfig)
}
}
borderWidthSeek = bwSeek
addView(bwWrap)
val (baWrap, baSeek) = createSlider("Border Opacity", 255) { p ->
if (!isBinding) {
currentConfig = currentConfig.copy(borderAlpha = p)
onShapeChanged?.invoke(currentConfig)
}
}
borderAlphaSeek = baSeek
addView(baWrap)
val colorScroll = HorizontalScrollView(context).apply {
isHorizontalScrollBarEnabled = false
}
val colorRow = LinearLayout(context).apply {
orientation = HORIZONTAL
setPadding(0, dp(6), 0, dp(4))
}
for (hex in borderColors) {
val swatch = View(context).apply {
layoutParams = LayoutParams(dp(32), dp(32)).apply {
rightMargin = dp(10)
}
background = GradientDrawable().apply {
shape = GradientDrawable.OVAL
setColor(Color.parseColor(hex))
setStroke(dp(2), Color.parseColor("#64748B"))
}
setOnClickListener {
if (!isBinding) {
currentConfig = currentConfig.copy(borderColorHex = hex)
onShapeChanged?.invoke(currentConfig)
}
}
}
colorRow.addView(swatch)
}
colorScroll.addView(colorRow)
addView(colorScroll)
}
fun bind(config: WidgetConfig) {
isBinding = true
currentConfig = config
updateShapeHighlight(config.shape)
cornerSeek.progress = config.cornerRadiusDp.toInt().coerceIn(0, 80)
polygonSidesSeek.progress = (config.polygonSides - 3).coerceIn(0, 9)
borderWidthSeek.progress = config.borderWidthDp.toInt().coerceIn(0, 16)
borderAlphaSeek.progress = config.borderAlpha.coerceIn(0, 255)
isBinding = false
}
private fun updateShapeHighlight(selected: WidgetShape) {
shapeButtons.forEach { (shape, button) ->
val active = shape == selected
button.background = GradientDrawable().apply {
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
