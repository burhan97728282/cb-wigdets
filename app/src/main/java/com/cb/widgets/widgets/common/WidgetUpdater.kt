package com.cb.widgets.widgets.common

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import com.cb.widgets.widgets.LiveWidget
import com.cb.widgets.widgets.LiveWidgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Redraws placed widgets on demand (app opened, setting changed, WorkManager tick). */
object WidgetUpdater {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    suspend fun refresh(context: Context, widgets: List<LiveWidget>) {
        val app = context.applicationContext
        val manager = AppWidgetManager.getInstance(app)
        for (widget in widgets) {
            try {
                val ids = manager.getAppWidgetIds(ComponentName(app, widget.providerClass))
                if (ids.isNotEmpty()) widget.create().renderWidgets(app, manager, ids)
            } catch (t: Throwable) {
                Log.w("CbWidgets", "Refresh failed for ${widget.id}", t)
            }
        }
    }

    suspend fun refreshAll(context: Context) = refresh(context, LiveWidgets.all)

    fun refreshAllAsync(context: Context) {
        val app = context.applicationContext
        scope.launch { refreshAll(app) }
    }

    /** True when at least one instance of any of [widgets] is placed on a home screen. */
    fun hasPlacedInstances(context: Context, widgets: List<LiveWidget>): Boolean {
        val app = context.applicationContext
        val manager = AppWidgetManager.getInstance(app)
        return widgets.any { manager.getAppWidgetIds(ComponentName(app, it.providerClass)).isNotEmpty() }
    }
}
