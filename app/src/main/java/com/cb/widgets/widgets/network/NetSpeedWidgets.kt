package com.cb.widgets.widgets.network

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.cb.widgets.R
import com.cb.widgets.core.data.SettingsRepository
import com.cb.widgets.core.metrics.ReadingQuality
import com.cb.widgets.core.metrics.SpeedFormatter
import com.cb.widgets.core.metrics.SpeedUnit
import com.cb.widgets.core.render.LcdPalette
import com.cb.widgets.core.render.WidgetPalette
import com.cb.widgets.core.widgetkit.WidgetStyle
import com.cb.widgets.widgets.common.BaseWidgetProvider
import com.cb.widgets.widgets.common.LcdRenderer
import com.cb.widgets.widgets.common.WidgetIntents
import com.cb.widgets.widgets.common.WidgetSurface
import com.cb.widgets.widgets.common.setSp
import com.cb.widgets.widgets.common.show
import java.text.DateFormat
import java.util.Date
import kotlin.math.min

/**
 * Net Speed widgets. They show the last speed measured from the device's traffic counters (all apps).
 * Tapping the widget measures again for 1.5 seconds. Without the optional Live mode Android does not allow a
 * widget to refresh every second, so this widget never pretends to be live: it states when it was measured.
 */
abstract class NetSpeedWidgetProvider : BaseWidgetProvider() {

    protected var unit: SpeedUnit = SpeedUnit.AUTO_BITS
    protected var snapshot: StoredReading? = null
    private var measureNow = false

    override suspend fun beforeRender(context: Context) {
        val app = context.applicationContext
        unit = SettingsRepository(app).currentSpeedUnit()
        val saved = NetSpeedStore.load(app)
        snapshot = if (measureNow || saved == null) NetSpeedMeasurer.measure(app) else saved
        measureNow = false
    }

    override fun onReceive(context: Context, intent: Intent) {
        val tapped = intent.action == WidgetIntents.ACTION_REFRESH
        if (tapped) measureNow = true
        super.onReceive(context, intent)
        if (tapped) refreshSelf(context)
    }

    protected class Shown(val value: String, val unit: String, val spoken: String)

    protected fun shown(bytesPerSecond: Double?): Shown {
        val s = snapshot
        return when {
            s == null || bytesPerSecond == null -> Shown("--", "", "")
            s.quality == ReadingQuality.UNAVAILABLE -> Shown("N/A", "", "")
            s.quality == ReadingQuality.INITIALIZING -> Shown("--", "", "")
            else -> SpeedFormatter.format(bytesPerSecond, unit).let { Shown(it.value, it.unit, it.spokenUnit) }
        }
    }

    protected fun noteText(): String {
        val s = snapshot
        return when {
            s == null || s.quality == ReadingQuality.INITIALIZING -> "Tap to measure"
            s.quality == ReadingQuality.UNAVAILABLE -> "Not available on this device"
            else -> "Measured " + DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(s.measuredAtMillis))
        }
    }

    protected fun describe(label: String, shown: Shown): String =
        if (shown.spoken.isEmpty()) "$label unavailable. ${noteText()}"
        else "$label ${shown.value} ${shown.spoken}. ${noteText()}. Tap to measure again."
}

class NetSpeedMinimalWidgetProvider : NetSpeedWidgetProvider() {
    override val defaultWidthDp: Int = 110
    override val defaultHeightDp: Int = 60

    override fun buildViews(context: Context, widthDp: Int, heightDp: Int, style: WidgetStyle): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_netspeed_minimal)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))
        views.setImageViewBitmap(R.id.lcd, LcdRenderer.render(context, widthDp - 16, heightDp - 16))

        val down = shown(snapshot?.downBytesPerSecond)
        val valueSp = min(heightDp * 0.34f, widthDp * 0.2f).coerceIn(12f, 80f)
        val small = (heightDp * 0.11f).coerceIn(7f, 14f)

        views.setTextViewText(R.id.value, down.value)
        views.setTextColor(R.id.value, LcdPalette.TEXT)
        views.setSp(R.id.value, valueSp)
        views.setTextViewText(R.id.unit, down.unit)
        views.setTextColor(R.id.unit, LcdPalette.TEXT)
        views.setSp(R.id.unit, (valueSp * 0.42f).coerceIn(8f, 30f))
        views.setTextColor(R.id.caption, LcdPalette.TEXT_DIM)
        views.setSp(R.id.caption, small)
        views.setTextViewText(R.id.note, noteText())
        views.setTextColor(R.id.note, LcdPalette.TEXT_DIM)
        views.setSp(R.id.note, small)
        views.show(R.id.caption, heightDp >= 64)
        views.show(R.id.note, heightDp >= 84 && widthDp >= 100)

        views.setContentDescription(R.id.root, describe("Download speed", down))
        views.setOnClickPendingIntent(R.id.root, WidgetIntents.refresh(context, NetSpeedMinimalWidgetProvider::class.java))
        return views
    }
}

class NetSpeedDualWidgetProvider : NetSpeedWidgetProvider() {
    override val defaultWidthDp: Int = 250
    override val defaultHeightDp: Int = 110

    override fun buildViews(context: Context, widthDp: Int, heightDp: Int, style: WidgetStyle): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_netspeed_dual)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))
        views.setImageViewBitmap(R.id.lcd, LcdRenderer.render(context, widthDp - 16, heightDp - 16))

        val down = shown(snapshot?.downBytesPerSecond)
        val up = shown(snapshot?.upBytesPerSecond)
        val valueSp = min(heightDp * 0.2f, widthDp * 0.13f).coerceIn(11f, 56f)
        val unitSp = (valueSp * 0.45f).coerceIn(8f, 26f)

        views.setTextViewText(R.id.down_value, down.value)
        views.setTextViewText(R.id.down_unit, down.unit)
        views.setTextViewText(R.id.up_value, up.value)
        views.setTextViewText(R.id.up_unit, up.unit)
        for (id in intArrayOf(R.id.down_value, R.id.up_value)) {
            views.setTextColor(id, LcdPalette.TEXT)
            views.setSp(id, valueSp)
        }
        for (id in intArrayOf(R.id.down_unit, R.id.up_unit)) {
            views.setTextColor(id, LcdPalette.TEXT)
            views.setSp(id, unitSp)
        }
        views.setInt(R.id.arrow_down, "setColorFilter", LcdPalette.TEXT)
        views.setInt(R.id.arrow_up, "setColorFilter", LcdPalette.TEXT)
        views.setTextViewText(R.id.note, noteText())
        views.setTextColor(R.id.note, LcdPalette.TEXT_DIM)
        views.setSp(R.id.note, (heightDp * 0.09f).coerceIn(7f, 13f))
        views.show(R.id.note, heightDp >= 90)

        views.setContentDescription(
            R.id.root,
            describe("Download", down) + " " + describe("Upload", up)
        )
        views.setOnClickPendingIntent(R.id.root, WidgetIntents.refresh(context, NetSpeedDualWidgetProvider::class.java))
        return views
    }
}
