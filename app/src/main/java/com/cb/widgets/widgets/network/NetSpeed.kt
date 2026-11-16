package com.cb.widgets.widgets.network

import android.content.Context
import android.net.TrafficStats
import android.os.SystemClock
import com.cb.widgets.core.metrics.CounterSample
import com.cb.widgets.core.metrics.ReadingQuality
import com.cb.widgets.core.metrics.SpeedCalculator
import kotlinx.coroutines.delay

/** Device-wide traffic counters (all apps). TrafficStats reports -1 where a device does not support them. */
object TrafficReader {
    fun read(): CounterSample = CounterSample(
        elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos(),
        rxBytes = TrafficStats.getTotalRxBytes(),
        txBytes = TrafficStats.getTotalTxBytes()
    )
}

/** The last measurement a Net Speed widget shows. */
data class StoredReading(
    val downBytesPerSecond: Double,
    val upBytesPerSecond: Double,
    val quality: ReadingQuality,
    val measuredAtMillis: Long
)

object NetSpeedStore {
    private const val PREFS = "cb_net_speed"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(context: Context): StoredReading? {
        val p = prefs(context)
        if (!p.contains("at")) return null
        val quality = ReadingQuality.entries.firstOrNull { it.name == p.getString("quality", null) } ?: return null
        return StoredReading(
            downBytesPerSecond = p.getFloat("down", 0f).toDouble(),
            upBytesPerSecond = p.getFloat("up", 0f).toDouble(),
            quality = quality,
            measuredAtMillis = p.getLong("at", 0L)
        )
    }

    fun save(context: Context, reading: StoredReading) {
        prefs(context).edit()
            .putFloat("down", reading.downBytesPerSecond.toFloat())
            .putFloat("up", reading.upBytesPerSecond.toFloat())
            .putString("quality", reading.quality.name)
            .putLong("at", reading.measuredAtMillis)
            .apply()
    }
}

/**
 * Measures the current speed by reading the traffic counters twice, a short moment apart. This reads local
 * counters only. It does not download anything and is not an internet speed test.
 */
object NetSpeedMeasurer {
    private const val SAMPLE_MILLIS = 1_500L

    suspend fun measure(context: Context): StoredReading {
        val first = TrafficReader.read()
        delay(SAMPLE_MILLIS)
        val second = TrafficReader.read()
        val reading = SpeedCalculator.compute(first, second)
        val stored = StoredReading(
            reading.downBytesPerSecond,
            reading.upBytesPerSecond,
            reading.quality,
            System.currentTimeMillis()
        )
        NetSpeedStore.save(context, stored)
        return stored
    }
}
