package com.custom.textwidget.ui.components
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.custom.textwidget.model.WidgetConfig
import com.custom.textwidget.widget.WidgetBitmapRenderer
class LiveWidgetPreview @JvmOverloads constructor(
context: Context,
attrs: AttributeSet? = null
) : View(context, attrs) {
private var config: WidgetConfig = WidgetConfig()
var onPositionDragged: ((Float, Float) -> Unit)? = null
private val checkerLight = Paint().apply { color = Color.parseColor("#151934") }
private val checkerDark = Paint().apply { color = Color.parseColor("#0A0D1E") }
private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
style = Paint.Style.STROKE
strokeWidth = 3f
color = Color.parseColor("#2E365F")
}
fun updateConfig(newConfig: WidgetConfig) {
config = newConfig
invalidate()
}
override fun onDraw(canvas: Canvas) {
super.onDraw(canvas)
val w = width
val h = height
if (w <= 0 || h <= 0) return
val tileSize = (14 * resources.displayMetrics.density).toInt().coerceAtLeast(8)
var y = 0
var row = 0
while (y < h) {
var x = 0
var col = 0
while (x < w) {
val paint = if ((row + col) % 2 == 0) checkerLight else checkerDark
canvas.drawRect(
x.toFloat(),
y.toFloat(),
(x + tileSize).coerceAtMost(w).toFloat(),
(y + tileSize).coerceAtMost(h).toFloat(),
paint
)
x += tileSize
col++
}
y += tileSize
row++
}
val rendered = WidgetBitmapRenderer.render(context, config, w, h)
canvas.drawBitmap(rendered, 0f, 0f, null)
canvas.drawRect(1f, 1f, w - 1f, h - 1f, borderPaint)
}
override fun onTouchEvent(event: MotionEvent): Boolean {
if (width <= 0 || height <= 0) return false
when (event.actionMasked) {
MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
parent?.requestDisallowInterceptTouchEvent(true)
val normX = ((event.x - width / 2f) / (width / 2f) * 100f).coerceIn(-100f, 100f)
val normY = ((event.y - height / 2f) / (height / 2f) * 100f).coerceIn(-100f, 100f)
onPositionDragged?.invoke(normX, normY)
return true
}
}
return super.onTouchEvent(event)
}
}
