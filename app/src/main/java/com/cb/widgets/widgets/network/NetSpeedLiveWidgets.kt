package com.cb.widgets.widgets.network

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.cb.widgets.R
import com.cb.widgets.core.metrics.SpeedFormatter
import com.cb.widgets.core.metrics.SpeedReading
import com.cb.widgets.core.metrics.SpeedUnit

/**
 * Cheap live updates for the Net Speed widgets: only the text is replaced (partiallyUpdateAppWidget), the
 * bitmaps are left alone. An update is sent only when the displayed text actually changes.
 */
object NetSpeedLiveWidgets {

    private val minimal = NetSpeedMinimalWidgetProvider::class.java
    private val dual = NetSpeedDualWidgetProvider::class.java

    fun hasInstances(context: Context): Boolean {
        val app = context.applicationContext
        val manager = AppWidgetManager.getInstance(app)
        return manager.getAppWidgetIds(ComponentName(app, minimal)).isNotEmpty() ||
            manager.getAppWidgetIds(ComponentName(app, dual)).isNotEmpty()
    }

    /** Pushes the reading to every placed Net Speed widget. Returns the new change key (same key = nothing sent). */
    fun push(
        context: Context,
        reading: SpeedReading,
        unit: SpeedUnit,
        intervalSeconds: Int,
        previousKey: String?
    ): String {
        val down = SpeedFormatter.format(reading.downBytesPerSecond, unit)
        val up = SpeedFormatter.format(reading.upBytesPerSecond, unit)
        val key = "${down.value}${down.unit}|${up.value}${up.unit}|$intervalSeconds"
        if (key == previousKey) return key

        val app = context.applicationContext
        val manager = AppWidgetManager.getInstance(app)
        val note = "Live · every ${intervalSeconds}s"

        val minimalIds = manager.getAppWidgetIds(ComponentName(app, minimal))
        if (minimalIds.isNotEmpty()) {
            val views = RemoteViews(app.packageName, R.layout.widget_netspeed_minimal)
            views.setTextViewText(R.id.value, down.value)
            views.setTextViewText(R.id.unit, down.unit)
            views.setTextViewText(R.id.note, note)
            views.setContentDescription(R.id.root, "Download speed ${down.value} ${down.spokenUnit}. Live.")
            manager.partiallyUpdateAppWidget(minimalIds, views)
        }

        val dualIds = manager.getAppWidgetIds(ComponentName(app, dual))
        if (dualIds.isNotEmpty()) {
            val views = RemoteViews(app.packageName, R.layout.widget_netspeed_dual)
            views.setTextViewText(R.id.down_value, down.value)
            views.setTextViewText(R.id.down_unit, down.unit)
            views.setTextViewText(R.id.up_value, up.value)
            views.setTextViewText(R.id.up_unit, up.unit)
            views.setTextViewText(R.id.note, note)
            views.setContentDescription(
                R.id.root,
                "Download ${down.value} ${down.spokenUnit}. Upload ${up.value} ${up.spokenUnit}. Live."
            )
            manager.partiallyUpdateAppWidget(dualIds, views)
        }
        return key
    }
}
