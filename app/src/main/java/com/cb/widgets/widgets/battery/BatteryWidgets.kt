package com.cb.widgets.widgets.battery

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.cb.widgets.R
import com.cb.widgets.core.render.WidgetPalette
import com.cb.widgets.core.widgetkit.WidgetStyle
import com.cb.widgets.widgets.LiveWidgets
import com.cb.widgets.widgets.common.BaseWidgetProvider
import com.cb.widgets.widgets.common.GaugeRenderer
import com.cb.widgets.widgets.common.WidgetIntents
import com.cb.widgets.widgets.common.WidgetSurface
import com.cb.widgets.widgets.common.WidgetUpdater
import com.cb.widgets.widgets.common.setSp
import com.cb.widgets.widgets.common.show
import kotlin.math.min

/**
 * Shared behaviour of the battery widgets.
 *
 * Android does not allow ACTION_BATTERY_CHANGED in the manifest, so the widgets refresh on the broadcasts that
 * ARE allowed (power connected/disconnected, battery low/okay), on a 15-minute WorkManager job, when the app is
 * opened, and when the user taps the widget. There is no always-running service.
 */
abstract class BatteryWidgetProvider : BaseWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            WidgetIntents.ACTION_REFRESH,
            Intent.ACTION_POWER_CONNECTED,
            Intent.ACTION_POWER_DISCONNECTED,
            Intent.ACTION_BATTERY_LOW,
            Intent.ACTION_BATTERY_OKAY -> refreshSelf(context)
        }
    }

    override fun onEnabled(context: Context) {
        BatteryWork.schedule(context)
    }

    override fun onDisabled(context: Context) {
        if (!WidgetUpdater.hasPlacedInstances(context, LiveWidgets.battery)) {
            BatteryWork.cancel(context)
        }
    }
}

class BatteryRingWidgetProvider : BatteryWidgetProvider() {
    override val defaultWidthDp: Int = 110
    override val defaultHeightDp: Int = 110

    override fun buildViews(context: Context, widthDp: Int, heightDp: Int, style: WidgetStyle): RemoteViews {
        val info = BatteryReader.read(context)
        val c = WidgetPalette.of(style)
        val views = RemoteViews(context.packageName, R.layout.widget_battery_ring)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))
        views.setImageViewBitmap(
            R.id.ring,
            GaugeRenderer.ring(context, style, (info?.percent ?: 0) / 100f, BatteryUi.fillColor(info, style.accentArgb))
        )

        // Free space inside the ring (the ring bitmap is fitted into the widget minus a 10dp margin).
        val hole = ((min(widthDp, heightDp) - 20f) * 0.68f).coerceAtLeast(10f)
        views.setTextViewText(R.id.percent, BatteryUi.percentText(info))
        views.setTextColor(R.id.percent, c.ink)
        views.setSp(R.id.percent, (hole * 0.34f).coerceIn(8f, 64f))
        views.setTextViewText(R.id.status, BatteryUi.statusText(info))
        views.setTextColor(R.id.status, c.inkSecondary)
        views.setSp(R.id.status, (hole * 0.15f).coerceIn(7f, 20f))
        views.show(R.id.status, hole >= 50f)

        views.setContentDescription(R.id.root, BatteryUi.description(info))
        views.setOnClickPendingIntent(R.id.root, WidgetIntents.refresh(context, BatteryRingWidgetProvider::class.java))
        return views
    }
}

class BatteryBarWidgetProvider : BatteryWidgetProvider() {
    override val defaultWidthDp: Int = 250
    override val defaultHeightDp: Int = 60

    override fun buildViews(context: Context, widthDp: Int, heightDp: Int, style: WidgetStyle): RemoteViews {
        val info = BatteryReader.read(context)
        val c = WidgetPalette.of(style)
        val views = RemoteViews(context.packageName, R.layout.widget_battery_bar)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))

        val statusText = BatteryUi.statusText(info)
        val percentSp = (heightDp * 0.34f).coerceIn(11f, 48f)
        val statusSp = (heightDp * 0.2f).coerceIn(9f, 22f)
        // The status text needs room; narrow or very short widgets show only the percentage and the bar.
        val showStatus = widthDp >= 190 && heightDp >= 36 && statusText.isNotEmpty()

        // The bar fills whatever width is left, so estimate the text widths to size its bitmap.
        val percentWidth = percentSp * 2.6f
        val statusWidth = if (showStatus) statusText.length * statusSp * 0.52f else 0f
        val barWidth = (widthDp - 20f - 16f - percentWidth - statusWidth).coerceAtLeast(24f)
        val barHeight = (heightDp * 0.30f).coerceIn(6f, 64f)

        views.setImageViewBitmap(
            R.id.bar,
            GaugeRenderer.bar(
                context, style, (info?.percent ?: 0) / 100f,
                barWidth.toInt(), barHeight.toInt().coerceAtLeast(6),
                BatteryUi.fillColor(info, style.accentArgb)
            )
        )
        views.setTextViewText(R.id.percent, BatteryUi.percentText(info))
        views.setTextColor(R.id.percent, c.ink)
        views.setSp(R.id.percent, percentSp)
        views.setTextViewText(R.id.status, statusText)
        views.setTextColor(R.id.status, c.inkSecondary)
        views.setSp(R.id.status, statusSp)
        views.show(R.id.status, showStatus)

        views.setContentDescription(R.id.root, BatteryUi.description(info))
        views.setOnClickPendingIntent(R.id.root, WidgetIntents.refresh(context, BatteryBarWidgetProvider::class.java))
        return views
    }
}
