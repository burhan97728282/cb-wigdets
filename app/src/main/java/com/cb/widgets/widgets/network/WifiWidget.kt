package com.cb.widgets.widgets.network

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.widget.RemoteViews
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.cb.widgets.R
import com.cb.widgets.core.render.SkeuPainter
import com.cb.widgets.core.render.WidgetPalette
import com.cb.widgets.core.system.WifiSignal
import com.cb.widgets.core.widgetkit.WidgetStyle
import com.cb.widgets.widgets.LiveWidgets
import com.cb.widgets.widgets.common.BaseWidgetProvider
import com.cb.widgets.widgets.common.WidgetIntents
import com.cb.widgets.widgets.common.WidgetSurface
import com.cb.widgets.widgets.common.WidgetUpdater
import com.cb.widgets.widgets.common.setSp
import com.cb.widgets.widgets.common.show
import java.util.concurrent.TimeUnit
import kotlin.math.min

/**
 * What Android lets an app see about Wi-Fi without location permission. The network NAME (SSID) needs location
 * permission and location services, so it is not shown. RSSI and link rate are available.
 */
data class WifiState(
    val wifiEnabled: Boolean?,
    val connected: Boolean,
    val rssiDbm: Int?,
    val linkMbps: Int?
) {
    val level: Int get() = WifiSignal.level(rssiDbm)
}

object WifiReader {
    @Suppress("DEPRECATION")
    fun read(context: Context): WifiState {
        val app = context.applicationContext
        val wifi = app.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val enabled = try {
            wifi?.isWifiEnabled
        } catch (e: Exception) {
            null
        }
        val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val caps = try {
            cm?.activeNetwork?.let { cm.getNetworkCapabilities(it) }
        } catch (e: Exception) {
            null
        }
        if (caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) != true) {
            return WifiState(enabled, false, null, null)
        }
        val info: WifiInfo? = try {
            (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) caps.transportInfo as? WifiInfo else null)
                ?: wifi?.connectionInfo
        } catch (e: Exception) {
            null
        }
        val rssi = info?.rssi?.takeIf { it in -126..-1 }
        val link = info?.linkSpeed?.takeIf { it > 0 }
        return WifiState(enabled, true, rssi, link)
    }
}

class WifiRefreshWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        WidgetUpdater.refresh(applicationContext, LiveWidgets.wifi)
        return Result.success()
    }
}

object WifiWork {
    private const val UNIQUE_NAME = "wifi_widget_refresh"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<WifiRefreshWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(context.applicationContext)
            .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context.applicationContext).cancelUniqueWork(UNIQUE_NAME)
    }
}

/**
 * Android only allows Wi-Fi change broadcasts to apps that are running, so this widget refreshes every
 * 15 minutes, when the app opens and when the widget is tapped.
 */
class WifiStatusWidgetProvider : BaseWidgetProvider() {
    override val defaultWidthDp: Int = 110
    override val defaultHeightDp: Int = 60

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == WidgetIntents.ACTION_REFRESH) refreshSelf(context)
    }

    override fun onEnabled(context: Context) {
        WifiWork.schedule(context)
    }

    override fun onDisabled(context: Context) {
        if (!WidgetUpdater.hasPlacedInstances(context, LiveWidgets.wifi)) WifiWork.cancel(context)
    }

    override fun buildViews(context: Context, widthDp: Int, heightDp: Int, style: WidgetStyle): RemoteViews {
        val state = WifiReader.read(context)
        val c = WidgetPalette.of(style)
        val views = RemoteViews(context.packageName, R.layout.widget_wifi_status)
        views.setImageViewBitmap(R.id.surface, WidgetSurface.render(context, widthDp, heightDp, style))

        val status = when {
            state.wifiEnabled == false -> "Wi-Fi off"
            state.connected -> "Connected"
            else -> "Not connected"
        }
        val detail = buildList {
            if (state.connected) {
                if (state.rssiDbm != null) add("Signal: " + WifiSignal.label(state.level))
                if (state.linkMbps != null) add("Link ${state.linkMbps} Mb/s")
            } else if (state.wifiEnabled == false) {
                add("Turn on Wi-Fi to connect")
            }
        }.joinToString(" · ")

        val statusSp = min(heightDp * 0.3f, widthDp * 0.14f).coerceIn(11f, 40f)
        views.setTextViewText(R.id.status, status)
        views.setTextColor(R.id.status, c.ink)
        views.setSp(R.id.status, statusSp)
        views.setTextViewText(R.id.caption, "WI-FI")
        views.setTextColor(R.id.caption, c.inkSecondary)
        views.setSp(R.id.caption, (statusSp * 0.4f).coerceIn(7f, 14f))
        views.setTextViewText(R.id.detail, detail)
        views.setTextColor(R.id.detail, c.inkSecondary)
        views.setSp(R.id.detail, (statusSp * 0.45f).coerceIn(8f, 16f))
        views.show(R.id.caption, heightDp >= 64)
        views.show(R.id.detail, detail.isNotEmpty() && heightDp >= 56 && widthDp >= 150)

        val showBars = widthDp >= 80
        views.show(R.id.bars, showBars)
        if (showBars) {
            views.setImageViewBitmap(
                R.id.bars,
                barsBitmap(context, if (state.connected) state.level else 0, c.ink, style.accentArgb)
            )
        }

        views.setContentDescription(
            R.id.root,
            "Wi-Fi: $status" + (if (detail.isNotEmpty()) ". $detail" else "") + ". Tap to refresh."
        )
        views.setOnClickPendingIntent(R.id.root, WidgetIntents.refresh(context, WifiStatusWidgetProvider::class.java))
        return views
    }

    /** Four rising bars; the active ones use the accent colour. */
    private fun barsBitmap(context: Context, level: Int, inkArgb: Int, accentArgb: Int): Bitmap {
        val size = (96 * context.resources.displayMetrics.density).toInt().coerceIn(48, 192)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val barWidth = size * 0.17f
        val gap = size * 0.07f
        val total = 4 * barWidth + 3 * gap
        var x = (size - total) / 2f
        val bottom = size * 0.9f
        for (i in 0 until WifiSignal.MAX_LEVEL) {
            val height = size * (0.25f + 0.17f * i)
            paint.color = if (i < level) accentArgb else SkeuPainter.withAlpha(inkArgb, 0.22f)
            canvas.drawRoundRect(RectF(x, bottom - height, x + barWidth, bottom), barWidth / 3f, barWidth / 3f, paint)
            x += barWidth + gap
        }
        return bitmap
    }
}
