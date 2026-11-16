package com.cb.widgets.core.metrics

/** Total bytes received/sent by the device at one moment. Negative values mean "unsupported" (TrafficStats). */
data class CounterSample(
    /** Monotonic clock (SystemClock.elapsedRealtimeNanos) so sleeping or changing the date cannot corrupt a rate. */
    val elapsedRealtimeNanos: Long,
    val rxBytes: Long,
    val txBytes: Long
) {
    val isSupported: Boolean get() = rxBytes >= 0L && txBytes >= 0L
}

enum class ReadingQuality {
    /** Measured over a short interval, so it is effectively the current speed. */
    LIVE,

    /** Measured over a long gap. It is an average, not the current speed. */
    AVERAGED,

    /** No usable pair of samples yet (first sample, counter reset, or interval too short). */
    INITIALIZING,

    /** The device does not report traffic counters. */
    UNAVAILABLE
}

data class SpeedReading(
    val downBytesPerSecond: Double,
    val upBytesPerSecond: Double,
    val quality: ReadingQuality
) {
    companion object {
        val INITIALIZING = SpeedReading(0.0, 0.0, ReadingQuality.INITIALIZING)
        val UNAVAILABLE = SpeedReading(0.0, 0.0, ReadingQuality.UNAVAILABLE)
    }
}

/**
 * Turns two counter samples into a rate: bytes difference / elapsed seconds.
 * The numbers are device-wide traffic of all apps (TrafficStats), NOT the speed of one Wi-Fi network and NOT
 * a link rate or an internet speed test.
 */
object SpeedCalculator {
    const val MIN_INTERVAL_NANOS = 250_000_000L
    const val MAX_LIVE_INTERVAL_NANOS = 10_000_000_000L

    fun compute(previous: CounterSample?, current: CounterSample): SpeedReading {
        if (!current.isSupported) return SpeedReading.UNAVAILABLE
        if (previous == null || !previous.isSupported) return SpeedReading.INITIALIZING

        val elapsedNanos = current.elapsedRealtimeNanos - previous.elapsedRealtimeNanos
        if (elapsedNanos < MIN_INTERVAL_NANOS) return SpeedReading.INITIALIZING

        val rxDelta = current.rxBytes - previous.rxBytes
        val txDelta = current.txBytes - previous.txBytes
        // A counter that went backwards was reset (reboot, interface change). Discard the sample.
        if (rxDelta < 0L || txDelta < 0L) return SpeedReading.INITIALIZING

        val seconds = elapsedNanos / 1_000_000_000.0
        val quality = if (elapsedNanos <= MAX_LIVE_INTERVAL_NANOS) ReadingQuality.LIVE else ReadingQuality.AVERAGED
        return SpeedReading(rxDelta / seconds, txDelta / seconds, quality)
    }
}
