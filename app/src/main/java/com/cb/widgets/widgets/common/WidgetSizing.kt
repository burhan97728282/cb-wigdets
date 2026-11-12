package com.cb.widgets.widgets.common

import android.appwidget.AppWidgetManager
import android.os.Bundle

object WidgetSizing {
    /**
     * Current widget size in dp. Portrait uses (MIN width, MAX height), landscape uses (MAX width, MIN height),
     * as documented for AppWidgetManager. Falls back to the provider's default when the launcher reports nothing.
     */
    fun sizeDp(options: Bundle?, defaultWidthDp: Int, defaultHeightDp: Int, landscape: Boolean = false): Pair<Int, Int> {
        val widthKey = if (landscape) AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH else AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH
        val heightKey = if (landscape) AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT else AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT
        val width = options?.getInt(widthKey, 0) ?: 0
        val height = options?.getInt(heightKey, 0) ?: 0
        return (if (width > 0) width else defaultWidthDp) to (if (height > 0) height else defaultHeightDp)
    }
}
