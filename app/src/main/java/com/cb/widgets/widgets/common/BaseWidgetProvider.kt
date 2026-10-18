package com.cb.widgets.widgets.common

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import com.cb.widgets.core.data.SettingsRepository
import com.cb.widgets.core.widgetkit.WidgetStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Base class of every CB Widgets home-screen widget.
 *
 * Rendering is shared: [renderWidgets] reads the user's default material, sizes the surface to the
 * widget's real dimensions and asks the subclass for the RemoteViews.
 * No background service is involved. Updates come from system callbacks, WorkManager or the widget itself
 * (TextClock / AnalogClock tick on their own inside the launcher).
 */
abstract class BaseWidgetProvider : AppWidgetProvider() {

    protected abstract val defaultWidthDp: Int
    protected abstract val defaultHeightDp: Int

    protected abstract fun buildViews(context: Context, widthDp: Int, heightDp: Int, style: WidgetStyle): RemoteViews

    suspend fun renderWidgets(context: Context, manager: AppWidgetManager, ids: IntArray) {
        if (ids.isEmpty()) return
        val app = context.applicationContext
        val material = SettingsRepository(app).currentDefaultMaterial()
        val style = WidgetStyle(material = material)
        for (id in ids) {
            try {
                val (w, h) = WidgetSizing.sizeDp(manager.getAppWidgetOptions(id), defaultWidthDp, defaultHeightDp)
                manager.updateAppWidget(id, buildViews(app, w, h, style))
            } catch (t: Throwable) {
                // One broken widget must never take the others (or the app) down.
                Log.w(TAG, "Could not render widget $id", t)
            }
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        refreshAsync(context, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        refreshAsync(context, intArrayOf(appWidgetId))
    }

    /** Re-renders every placed instance of this provider. Call from onReceive only. */
    protected fun refreshSelf(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, this::class.java))
        refreshAsync(context, ids)
    }

    private fun refreshAsync(context: Context, ids: IntArray) {
        val pending: BroadcastReceiver.PendingResult? = goAsync()
        val manager = AppWidgetManager.getInstance(context)
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                renderWidgets(context, manager, ids)
            } finally {
                pending?.finish()
            }
        }
    }

    private companion object {
        const val TAG = "CbWidgets"
    }
}
