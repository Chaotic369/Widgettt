package com.custom.textwidget.storage
import android.content.Context
import com.custom.textwidget.model.TextAlignment
import com.custom.textwidget.model.WidgetConfig
import com.custom.textwidget.model.WidgetShape
import org.json.JSONObject
class WidgetPrefsRepository(context: Context) {
private val prefs = context.getSharedPreferences("custom_text_widgets_prefs", Context.MODE_PRIVATE)
fun saveConfig(config: WidgetConfig) {
val json = JSONObject().apply {
put("widgetId", config.widgetId)
put("text", config.text)
put("fontFamily", config.fontFamily)
put("textSizeSp", config.textSizeSp.toDouble())
put("textColorHex", config.textColorHex)
put("textAlpha", config.textAlpha)
put("isBold", config.isBold)
put("isItalic", config.isItalic)
put("letterSpacing", config.letterSpacing.toDouble())
put("lineSpacingExtraDp", config.lineSpacingExtraDp.toDouble())
put("bgColorHex", config.bgColorHex)
put("bgAlpha", config.bgAlpha)
put("useGradientBg", config.useGradientBg)
put("bgGradientEndHex", config.bgGradientEndHex)
put("gradientAngle", config.gradientAngle.toDouble())
put("shape", config.shape.name)
put("cornerRadiusDp", config.cornerRadiusDp.toDouble())
put("polygonSides", config.polygonSides)
put("borderWidthDp", config.borderWidthDp.toDouble())
put("borderColorHex", config.borderColorHex)
put("borderAlpha", config.borderAlpha)
put("textAlignment", config.textAlignment.name)
put("offsetXPercent", config.offsetXPercent.toDouble())
put("offsetYPercent", config.offsetYPercent.toDouble())
put("rotationDegrees", config.rotationDegrees.toDouble())
put("paddingDp", config.paddingDp.toDouble())
put("shadowRadiusDp", config.shadowRadiusDp.toDouble())
put("shadowColorHex", config.shadowColorHex)
}
val ids = getAllWidgetIds().toMutableSet()
ids.add(config.widgetId)
prefs.edit()
.putString("widget_${config.widgetId}", json.toString())
.putStringSet("all_widget_ids", ids.map { it.toString() }.toSet())
.apply()
}
fun loadConfig(widgetId: Int): WidgetConfig {
val raw = prefs.getString("widget_$widgetId", null) ?: return WidgetConfig(widgetId = widgetId)
return try {
val json = JSONObject(raw)
WidgetConfig(
widgetId = json.optInt("widgetId", widgetId),
text = json.optString("text", "Stay Focused.\nBuild Daily."),
fontFamily = json.optString("fontFamily", "Inter"),
textSizeSp = json.optDouble("textSizeSp", 24.0).toFloat(),
textColorHex = json.optString("textColorHex", "#FFFFFF"),
textAlpha = json.optInt("textAlpha", 255),
isBold = json.optBoolean("isBold", true),
isItalic = json.optBoolean("isItalic", false),
letterSpacing = json.optDouble("letterSpacing", 0.02).toFloat(),
lineSpacingExtraDp = json.optDouble("lineSpacingExtraDp", 4.0).toFloat(),
bgColorHex = json.optString("bgColorHex", "#000432"),
bgAlpha = json.optInt("bgAlpha", 190),
useGradientBg = json.optBoolean("useGradientBg", true),
bgGradientEndHex = json.optString("bgGradientEndHex", "#3B0764"),
gradientAngle = json.optDouble("gradientAngle", 45.0).toFloat(),
shape = runCatching {
WidgetShape.valueOf(json.optString("shape", WidgetShape.ROUNDED_RECT.name))
}.getOrDefault(WidgetShape.ROUNDED_RECT),
cornerRadiusDp = json.optDouble("cornerRadiusDp", 28.0).toFloat(),
polygonSides = json.optInt("polygonSides", 6),
borderWidthDp = json.optDouble("borderWidthDp", 2.0).toFloat(),
borderColorHex = json.optString("borderColorHex", "#A855F7"),
borderAlpha = json.optInt("borderAlpha", 220),
textAlignment = runCatching {
TextAlignment.valueOf(json.optString("textAlignment", TextAlignment.CENTER.name))
}.getOrDefault(TextAlignment.CENTER),
offsetXPercent = json.optDouble("offsetXPercent", 0.0).toFloat(),
offsetYPercent = json.optDouble("offsetYPercent", 0.0).toFloat(),
rotationDegrees = json.optDouble("rotationDegrees", 0.0).toFloat(),
paddingDp = json.optDouble("paddingDp", 16.0).toFloat(),
shadowRadiusDp = json.optDouble("shadowRadiusDp", 8.0).toFloat(),
shadowColorHex = json.optString("shadowColorHex", "#000000")
)
} catch (e: Exception) {
WidgetConfig(widgetId = widgetId)
}
}
fun deleteConfig(widgetId: Int) {
val ids = getAllWidgetIds().toMutableSet()
ids.remove(widgetId)
prefs.edit()
.remove("widget_$widgetId")
.putStringSet("all_widget_ids", ids.map { it.toString() }.toSet())
.apply()
}
fun getAllWidgetIds(): Set<Int> {
val rawSet = prefs.getStringSet("all_widget_ids", emptySet()) ?: emptySet()
return rawSet.mapNotNull { it.toIntOrNull() }.toSet()
}
}
