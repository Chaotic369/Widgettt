package com.custom.textwidget.widget
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.custom.textwidget.model.TextAlignment
import com.custom.textwidget.model.WidgetConfig
import com.custom.textwidget.model.WidgetShape
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
object WidgetBitmapRenderer {
fun render(context: Context, config: WidgetConfig, widthPx: Int, heightPx: Int): Bitmap {
val safeW = widthPx.coerceAtLeast(100)
val safeH = heightPx.coerceAtLeast(80)
val density = context.resources.displayMetrics.density
val scaledDensity = context.resources.displayMetrics.scaledDensity
val bitmap = Bitmap.createBitmap(safeW, safeH, Bitmap.Config.ARGB_8888)
val canvas = Canvas(bitmap)
val strokePx = config.borderWidthDp * density
val inset = (strokePx / 2f) + (4f * density)
val rect = RectF(inset, inset, safeW - inset, safeH - inset)
val shapePath = buildShapePath(rect, config, density)
if (config.bgAlpha > 0) {
val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
style = Paint.Style.FILL
val primaryColor = applyAlpha(parseColorSafe(config.bgColorHex, Color.BLACK), config.bgAlpha)
if (config.useGradientBg) {
val secondaryColor = applyAlpha(parseColorSafe(config.bgGradientEndHex, Color.DKGRAY), config.bgAlpha)
val angleRad = Math.toRadians(config.gradientAngle.toDouble())
val cx = rect.centerX()
val cy = rect.centerY()
val r = min(rect.width(), rect.height()) / 2f
val x0 = cx - (cos(angleRad) * r).toFloat()
val y0 = cy - (sin(angleRad) * r).toFloat()
val x1 = cx + (cos(angleRad) * r).toFloat()
val y1 = cy + (sin(angleRad) * r).toFloat()
shader = LinearGradient(x0, y0, x1, y1, primaryColor, secondaryColor, Shader.TileMode.CLAMP)
} else {
color = primaryColor
}
}
canvas.drawPath(shapePath, bgPaint)
}
if (config.borderWidthDp > 0f && config.borderAlpha > 0) {
val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
style = Paint.Style.STROKE
strokeWidth = strokePx
color = applyAlpha(parseColorSafe(config.borderColorHex, Color.WHITE), config.borderAlpha)
}
canvas.drawPath(shapePath, borderPaint)
}
val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
color = applyAlpha(parseColorSafe(config.textColorHex, Color.WHITE), config.textAlpha)
textSize = config.textSizeSp * scaledDensity
typeface = resolveTypeface(context, config.fontFamily, config.isBold, config.isItalic)
letterSpacing = config.letterSpacing
if (config.shadowRadiusDp > 0f) {
setShadowLayer(
config.shadowRadiusDp * density,
0f,
2f * density,
parseColorSafe(config.shadowColorHex, Color.BLACK)
)
}
}
val padPx = config.paddingDp * density
val availableTextWidth = (rect.width() - padPx * 2f).toInt().coerceAtLeast(40)
val align = when (config.textAlignment) {
TextAlignment.LEFT -> Layout.Alignment.ALIGN_NORMAL
TextAlignment.CENTER -> Layout.Alignment.ALIGN_CENTER
TextAlignment.RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
}
val displayContent = if (config.text.isEmpty()) " " else config.text
val staticLayout = StaticLayout.Builder
.obtain(displayContent, 0, displayContent.length, textPaint, availableTextWidth)
.setAlignment(align)
.setLineSpacing(config.lineSpacingExtraDp * density, 1f)
.setIncludePad(false)
.build()
canvas.save()
canvas.clipPath(shapePath)
val baseX = rect.left + padPx
val baseY = rect.centerY() - (staticLayout.height / 2f)
val shiftX = (config.offsetXPercent / 100f) * (rect.width() / 2f)
val shiftY = (config.offsetYPercent / 100f) * (rect.height() / 2f)
val centerX = baseX + (availableTextWidth / 2f) + shiftX
val centerY = baseY + (staticLayout.height / 2f) + shiftY
canvas.rotate(config.rotationDegrees, centerX, centerY)
canvas.translate(baseX + shiftX, baseY + shiftY)
staticLayout.draw(canvas)
canvas.restore()
return bitmap
}
private fun buildShapePath(rect: RectF, config: WidgetConfig, density: Float): Path {
val path = Path()
val radiusPx = config.cornerRadiusDp * density
when (config.shape) {
WidgetShape.RECTANGLE -> {
path.addRect(rect, Path.Direction.CW)
}
WidgetShape.ROUNDED_RECT -> {
path.addRoundRect(rect, radiusPx, radiusPx, Path.Direction.CW)
}
WidgetShape.PILL -> {
val pillR = min(rect.width(), rect.height()) / 2f
path.addRoundRect(rect, pillR, pillR, Path.Direction.CW)
}
WidgetShape.CIRCLE -> {
path.addOval(rect, Path.Direction.CW)
}
WidgetShape.SQUIRCLE -> {
val r = min(rect.width(), rect.height()) * 0.38f
path.addRoundRect(rect, r, r, Path.Direction.CW)
}
WidgetShape.CUT_CORNERS -> {
val cut = radiusPx.coerceAtMost(min(rect.width(), rect.height()) / 2.2f)
path.moveTo(rect.left + cut, rect.top)
path.lineTo(rect.right - cut, rect.top)
path.lineTo(rect.right, rect.top + cut)
path.lineTo(rect.right, rect.bottom - cut)
path.lineTo(rect.right - cut, rect.bottom)
path.lineTo(rect.left + cut, rect.bottom)
path.lineTo(rect.left, rect.bottom - cut)
path.lineTo(rect.left, rect.top + cut)
path.close()
}
WidgetShape.HEXAGON -> {
val sides = config.polygonSides.coerceIn(3, 12)
val cx = rect.centerX()
val cy = rect.centerY()
val rx = rect.width() / 2f
val ry = rect.height() / 2f
for (i in 0 until sides) {
val angle = Math.toRadians((360.0 / sides) * i - 90.0)
val px = cx + (rx * cos(angle)).toFloat()
val py = cy + (ry * sin(angle)).toFloat()
if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
}
path.close()
}
WidgetShape.STAR_BADGE -> {
val points = (config.polygonSides.coerceIn(5, 12)) * 2
val cx = rect.centerX()
val cy = rect.centerY()
val outerRx = rect.width() / 2f
val outerRy = rect.height() / 2f
val innerRx = outerRx * 0.78f
val innerRy = outerRy * 0.78f
for (i in 0 until points) {
val angle = Math.toRadians((360.0 / points) * i - 90.0)
val rx = if (i % 2 == 0) outerRx else innerRx
val ry = if (i % 2 == 0) outerRy else innerRy
val px = cx + (rx * cos(angle)).toFloat()
val py = cy + (ry * sin(angle)).toFloat()
if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
}
path.close()
}
}
return path
}
private fun resolveTypeface(
context: Context,
family: String,
isBold: Boolean,
isItalic: Boolean
): Typeface {
val style = when {
isBold && isItalic -> Typeface.BOLD_ITALIC
isBold -> Typeface.BOLD
isItalic -> Typeface.ITALIC
else -> Typeface.NORMAL
}
val assetPath = when (family) {
"Inter" -> "fonts/Inter-Regular.ttf"
"JetBrainsMono" -> "fonts/JetBrainsMono-Regular.ttf"
"PlayfairDisplay" -> "fonts/PlayfairDisplay-Regular.ttf"
else -> null
}
if (assetPath != null) {
try {
val tf = Typeface.createFromAsset(context.assets, assetPath)
return Typeface.create(tf, style)
} catch (_: Exception) {
// Fallback safely if placeholder font is replaced or unavailable
}
}
val base = when (family.lowercase()) {
"monospace", "jetbrainsmono" -> Typeface.MONOSPACE
"serif", "playfairdisplay" -> Typeface.SERIF
"cursive" -> Typeface.create("cursive", style)
else -> Typeface.SANS_SERIF
}
return Typeface.create(base, style)
}
private fun parseColorSafe(hex: String, fallback: Int): Int {
return try {
Color.parseColor(hex)
} catch (_: Exception) {
fallback
}
}
private fun applyAlpha(color: Int, alpha: Int): Int {
val clamped = alpha.coerceIn(0, 255)
return Color.argb(clamped, Color.red(color), Color.green(color), Color.blue(color))
}
}
