package com.cb.widgets.widgets.clock

import android.content.Context
import android.widget.RemoteViews
import com.cb.widgets.R
import com.cb.widgets.core.render.WidgetPalette
import com.cb.widgets.core.widgetkit.WidgetStyle
import com.cb.widgets.widgets.common.BaseWidgetProvider
import com.cb.widgets.widgets.common.WidgetIntents
import com.cb.widgets.widgets.common.WidgetSurface

// The time and date text in these widgets are TextClock / AnalogClock views. The launcher ticks them
// by itself, so the widgets need no alarms, services or WorkManager and cost no extra battery.

class DigitalClockWidgetProvider : BaseWidgetProvider() {
    override val defaultWidthDp: Int = 250
    override val defaultHeightDp: Int = 110

    override fun buildViews(context: Context, widthDp: Int, heightDp: Int, style: WidgetStyle): RemoteViews {
        val c = WidgetPalette.of(style.material, style.opacity)
        val views = RemoteViews(context.packageName, R.layout.widget_clock_digital)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))
        views.setTextColor(R.id.time, c.ink)
        views.setTextColor(R.id.day, c.ink)
        views.setTextColor(R.id.date, c.inkSecondary)
        views.setInt(R.id.accent_bar, "setBackgroundColor", style.accentArgb)
        views.setOnClickPendingIntent(R.id.root, WidgetIntents.openClock(context))
        return views
    }
}

class MinimalClockWidgetProvider : BaseWidgetProvider() {
    override val defaultWidthDp: Int = 110
    override val defaultHeightDp: Int = 60

    override fun buildViews(context: Context, widthDp: Int, heightDp: Int, style: WidgetStyle): RemoteViews {
        val c = WidgetPalette.of(style.material, style.opacity)
        val views = RemoteViews(context.packageName, R.layout.widget_clock_minimal)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))
        views.setTextColor(R.id.time, c.ink)
        views.setOnClickPendingIntent(R.id.root, WidgetIntents.openClock(context))
        return views
    }
}

class AnalogClockWidgetProvider : BaseWidgetProvider() {
    override val defaultWidthDp: Int = 110
    override val defaultHeightDp: Int = 110

    override fun buildViews(context: Context, widthDp: Int, heightDp: Int, style: WidgetStyle): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_clock_analog)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))
        views.setOnClickPendingIntent(R.id.root, WidgetIntents.openClock(context))
        return views
    }
}

class DateCardWidgetProvider : BaseWidgetProvider() {
    override val defaultWidthDp: Int = 110
    override val defaultHeightDp: Int = 110

    override fun buildViews(context: Context, widthDp: Int, heightDp: Int, style: WidgetStyle): RemoteViews {
        val c = WidgetPalette.of(style.material, style.opacity)
        val views = RemoteViews(context.packageName, R.layout.widget_date_card)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))
        views.setTextColor(R.id.weekday, c.inkSecondary)
        views.setTextColor(R.id.daynum, c.ink)
        views.setTextColor(R.id.month, c.inkSecondary)
        views.setInt(R.id.accent_bar, "setBackgroundColor", style.accentArgb)
        views.setOnClickPendingIntent(R.id.root, WidgetIntents.openCalendar(context))
        return views
    }
}
