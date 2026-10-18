package com.cb.widgets.widgets.common

import android.appwidget.AppWidgetManager
import android.os.Bundle

object WidgetSizing {
    /**
     * Current widget size in dp. Uses the portrait dimensions (MIN width, MAX height) and falls back
     * to the provider's default size when the launcher does not report one.
     */
    fun sizeDp(options: Bundle?, defaultWidthDp: Int, defaultHeightDp: Int): Pair<Int, Int> {
        val width = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0) ?: 0
        val height = options?.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 0) ?: 0
        return (if (width > 0) width else defaultWidthDp) to (if (height > 0) height else defaultHeightDp)
    }
}
