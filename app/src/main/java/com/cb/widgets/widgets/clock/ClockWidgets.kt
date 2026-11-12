package com.cb.widgets.widgets.clock

import android.content.Context
import android.widget.RemoteViews
import com.cb.widgets.R
import com.cb.widgets.core.render.WidgetPalette
import com.cb.widgets.core.widgetkit.WidgetStyle
import com.cb.widgets.widgets.common.BaseWidgetProvider
import com.cb.widgets.widgets.common.WidgetIntents
import com.cb.widgets.widgets.common.WidgetSurface
import com.cb.widgets.widgets.common.setSp
import com.cb.widgets.widgets.common.show
import kotlin.math.min

// The time and date text in these widgets are TextClock / AnalogClock views. The launcher ticks them
// by itself, so the widgets need no alarms, services or WorkManager and cost no extra battery.
// Text sizes are recomputed from the widget's real size, so every widget works from 1x1 up to full screen.

class DigitalClockWidgetProvider : BaseWidgetProvider() {
    override val defaultWidthDp: Int = 250
    override val defaultHeightDp: Int = 110

    override fun buildViews(context: Context, widthDp: Int, heightDp: Int, style: WidgetStyle): RemoteViews {
        val c = WidgetPalette.of(style)
        val views = RemoteViews(context.packageName, R.layout.widget_clock_digital)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))

        // Small widgets show only the time; more height adds the weekday, then the date.
        val showDay = heightDp >= 80
        val showDate = heightDp >= 100
        val time = min(heightDp * (if (showDay) 0.36f else 0.6f), widthDp * 0.27f).coerceIn(12f, 130f)

        views.setTextColor(R.id.time, c.ink)
        views.setSp(R.id.time, time)
        views.setTextColor(R.id.day, c.ink)
        views.setSp(R.id.day, (time * 0.36f).coerceIn(9f, 30f))
        views.setTextColor(R.id.date, c.inkSecondary)
        views.setSp(R.id.date, (time * 0.3f).coerceIn(8f, 24f))
        views.setInt(R.id.accent_bar, "setBackgroundColor", style.accentArgb)
        views.show(R.id.accent_bar, showDay)
        views.show(R.id.day, showDay)
        views.show(R.id.date, showDate)

        views.setOnClickPendingIntent(R.id.root, WidgetIntents.openClock(context))
        return views
    }
}

class MinimalClockWidgetProvider : BaseWidgetProvider() {
    override val defaultWidthDp: Int = 110
    override val defaultHeightDp: Int = 60

    override fun buildViews(context: Context, widthDp: Int, heightDp: Int, style: WidgetStyle): RemoteViews {
        val c = WidgetPalette.of(style)
        val views = RemoteViews(context.packageName, R.layout.widget_clock_minimal)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))
        views.setTextColor(R.id.time, c.ink)
        views.setSp(R.id.time, min(heightDp * 0.62f, widthDp * 0.3f).coerceIn(12f, 160f))
        views.setOnClickPendingIntent(R.id.root, WidgetIntents.openClock(context))
        return views
    }
}

class AnalogClockWidgetProvider : BaseWidgetProvider() {
    override val defaultWidthDp: Int = 110
    override val defaultHeightDp: Int = 110

    // The analog face scales with the widget by itself.
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
        val c = WidgetPalette.of(style)
        val views = RemoteViews(context.packageName, R.layout.widget_date_card)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))

        val showWeekday = heightDp >= 70
        val showMonth = heightDp >= 95
        val dayFraction = when {
            showMonth -> 0.40f
            showWeekday -> 0.50f
            else -> 0.70f
        }
        val small = (min(widthDp, heightDp) * 0.10f).coerceIn(8f, 22f)

        views.setTextColor(R.id.weekday, c.inkSecondary)
        views.setSp(R.id.weekday, small)
        views.setTextColor(R.id.daynum, c.ink)
        views.setSp(R.id.daynum, min(heightDp * dayFraction, widthDp * 0.5f).coerceIn(14f, 170f))
        views.setTextColor(R.id.month, c.inkSecondary)
        views.setSp(R.id.month, small)
        views.setInt(R.id.accent_bar, "setBackgroundColor", style.accentArgb)
        views.show(R.id.weekday, showWeekday)
        views.show(R.id.accent_bar, showWeekday)
        views.show(R.id.month, showMonth)

        views.setOnClickPendingIntent(R.id.root, WidgetIntents.openCalendar(context))
        return views
    }
}
