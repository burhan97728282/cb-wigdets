package com.cb.widgets.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context

/** Android never lets an app place a widget silently. This asks the launcher to show its own "Add" dialog. */
object WidgetPinner {
    enum class Result { REQUESTED, UNSUPPORTED, UNAVAILABLE }

    fun requestPin(context: Context, widgetId: String): Result {
        val widget = LiveWidgets.byId(widgetId) ?: return Result.UNAVAILABLE
        val manager = AppWidgetManager.getInstance(context)
        if (!manager.isRequestPinAppWidgetSupported) return Result.UNSUPPORTED
        val accepted = manager.requestPinAppWidget(ComponentName(context, widget.providerClass), null, null)
        return if (accepted) Result.REQUESTED else Result.UNSUPPORTED
    }
}
