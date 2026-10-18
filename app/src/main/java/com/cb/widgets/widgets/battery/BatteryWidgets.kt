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
        val c = WidgetPalette.of(style.material, style.opacity)
        val views = RemoteViews(context.packageName, R.layout.widget_battery_ring)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))
        views.setImageViewBitmap(
            R.id.ring,
            GaugeRenderer.ring(context, style, (info?.percent ?: 0) / 100f, BatteryUi.fillColor(info, style.accentArgb))
        )
        views.setTextViewText(R.id.percent, BatteryUi.percentText(info))
        views.setTextColor(R.id.percent, c.ink)
        views.setTextViewText(R.id.status, BatteryUi.statusText(info))
        views.setTextColor(R.id.status, c.inkSecondary)
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
        val c = WidgetPalette.of(style.material, style.opacity)
        val views = RemoteViews(context.packageName, R.layout.widget_battery_bar)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))
        // Width left for the bar once the percentage and status text and the padding are taken out.
        val barWidthDp = (widthDp - 24 - 56 - 16 - 96).coerceAtLeast(48)
        views.setImageViewBitmap(
            R.id.bar,
            GaugeRenderer.bar(context, style, (info?.percent ?: 0) / 100f, barWidthDp, BatteryUi.fillColor(info, style.accentArgb))
        )
        views.setTextViewText(R.id.percent, BatteryUi.percentText(info))
        views.setTextColor(R.id.percent, c.ink)
        views.setTextViewText(R.id.status, BatteryUi.statusText(info))
        views.setTextColor(R.id.status, c.inkSecondary)
        views.setContentDescription(R.id.root, BatteryUi.description(info))
        views.setOnClickPendingIntent(R.id.root, WidgetIntents.refresh(context, BatteryBarWidgetProvider::class.java))
        return views
    }
}
