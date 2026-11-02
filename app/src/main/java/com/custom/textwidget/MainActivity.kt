package com.custom.textwidget
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.custom.textwidget.storage.WidgetPrefsRepository
import com.custom.textwidget.widget.CustomTextWidgetProvider
import com.custom.textwidget.widget.WidgetBitmapRenderer
class MainActivity : AppCompatActivity() {
private lateinit var repository: WidgetPrefsRepository
private lateinit var widgetListContainer: LinearLayout
private lateinit var emptyStateText: TextView
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContentView(R.layout.activity_main)
repository = WidgetPrefsRepository(this)
widgetListContainer = findViewById(R.id.widgetListContainer)
emptyStateText = findViewById(R.id.emptyStateText)
val btnPinWidget = findViewById<Button>(R.id.btnPinNewWidget)
btnPinWidget.setOnClickListener {
requestPinNewWidget()
}
}
override fun onResume() {
super.onResume()
loadActiveWidgets()
}
private fun loadActiveWidgets() {
widgetListContainer.removeAllViews()
val appWidgetManager = AppWidgetManager.getInstance(this)
val provider = ComponentName(this, CustomTextWidgetProvider::class.java)
val systemIds = appWidgetManager.getAppWidgetIds(provider).toSet()
val savedIds = repository.getAllWidgetIds()
val allIds = (systemIds + savedIds).sorted()
if (allIds.isEmpty()) {
emptyStateText.visibility = View.VISIBLE
return
}
emptyStateText.visibility = View.GONE
for (widgetId in allIds) {
val config = repository.loadConfig(widgetId)
val card = LinearLayout(this).apply {
orientation = LinearLayout.VERTICAL
setPadding(dp(16), dp(16), dp(16), dp(16))
background = GradientDrawable().apply {
setColor(Color.parseColor("#080B20"))
cornerRadius = dp(16).toFloat()
setStroke(dp(1), Color.parseColor("#2A2F55"))
}
layoutParams = LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
LinearLayout.LayoutParams.WRAP_CONTENT
).apply {
bottomMargin = dp(14)
}
}
val topRow = LinearLayout(this).apply {
orientation = LinearLayout.HORIZONTAL
gravity = Gravity.CENTER_VERTICAL
}
val titleView = TextView(this).apply {
text = "Widget #$widgetId • ${config.shape.displayName}"
setTextColor(Color.parseColor("#A855F7"))
setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
}
val editBtn = Button(this).apply {
text = "Customize"
setTextColor(Color.WHITE)
setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
background = GradientDrawable().apply {
setColor(Color.parseColor("#A855F7"))
cornerRadius = dp(10).toFloat()
}
setOnClickListener {
val intent = Intent(this@MainActivity, WidgetConfigActivity::class.java).apply {
putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
}
startActivity(intent)
}
}
topRow.addView(titleView)
topRow.addView(editBtn)
card.addView(topRow)
val previewImage = ImageView(this).apply {
layoutParams = LinearLayout.LayoutParams(
LinearLayout.LayoutParams.MATCH_PARENT,
dp(120)
).apply {
topMargin = dp(12)
}
scaleType = ImageView.ScaleType.FIT_CENTER
val bmp = WidgetBitmapRenderer.render(this@MainActivity, config, 600, 260)
setImageBitmap(bmp)
}
card.addView(previewImage)
widgetListContainer.addView(card)
}
}
private fun requestPinNewWidget() {
val appWidgetManager = AppWidgetManager.getInstance(this)
val myProvider = ComponentName(this, CustomTextWidgetProvider::class.java)
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appWidgetManager.isRequestPinAppWidgetSupported) {
appWidgetManager.requestPinAppWidget(myProvider, null, null)
} else {
Toast.makeText(
this,
"Long-press your Home Screen -> Widgets -> Drag Custom Text Widget!",
Toast.LENGTH_LONG
).show()
}
}
private fun dp(value: Int): Int {
return (value * resources.displayMetrics.density).toInt()
}
}
