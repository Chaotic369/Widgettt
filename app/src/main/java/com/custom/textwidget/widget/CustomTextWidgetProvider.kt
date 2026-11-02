package com.custom.textwidget.widget
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.custom.textwidget.R
import com.custom.textwidget.WidgetConfigActivity
import com.custom.textwidget.storage.WidgetPrefsRepository
class CustomTextWidgetProvider : AppWidgetProvider() {
override fun onUpdate(
context: Context,
appWidgetManager: AppWidgetManager,
appWidgetIds: IntArray
) {
for (appWidgetId in appWidgetIds) {
updateSingleWidget(context, appWidgetManager, appWidgetId)
}
}
override fun onAppWidgetOptionsChanged(
context: Context,
appWidgetManager: AppWidgetManager,
appWidgetId: Int,
newOptions: Bundle
) {
super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
updateSingleWidget(context, appWidgetManager, appWidgetId)
}
override fun onDeleted(context: Context, appWidgetIds: IntArray) {
super.onDeleted(context, appWidgetIds)
val repo = WidgetPrefsRepository(context)
for (id in appWidgetIds) {
repo.deleteConfig(id)
}
}
companion object {
fun updateSingleWidget(
context: Context,
appWidgetManager: AppWidgetManager,
appWidgetId: Int
) {
val repo = WidgetPrefsRepository(context)
val config = repo.loadConfig(appWidgetId)
val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
val minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 220)
.coerceAtLeast(100)
val minHeightDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 120)
.coerceAtLeast(60)
val density = context.resources.displayMetrics.density
val widthPx = (minWidthDp * density).toInt().coerceIn(240, 1400)
val heightPx = (minHeightDp * density).toInt().coerceIn(140, 1400)
val bitmap = WidgetBitmapRenderer.render(context, config, widthPx, heightPx)
val views = RemoteViews(context.packageName, R.layout.widget_canvas_layout)
views.setImageViewBitmap(R.id.widgetCanvasImageView, bitmap)
val editIntent = Intent(context, WidgetConfigActivity::class.java).apply {
putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
}
val pendingIntent = PendingIntent.getActivity(
context,
appWidgetId,
editIntent,
PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
)
views.setOnClickPendingIntent(R.id.widgetCanvasImageView, pendingIntent)
appWidgetManager.updateAppWidget(appWidgetId, views)
}
}
}
