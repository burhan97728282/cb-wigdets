package com.cb.widgets.core.metrics

import java.util.Locale

data class FormattedSpeed(val value: String, val unit: String, val spokenUnit: String)

object SpeedFormatter {

    /**
     * Formats a rate (bytes per second) in [unit]. For AUTO the largest unit whose value is at least 1 is used,
     * and a value that would round up to 1000 moves to the next unit, so the text never shows "1000 Kb/s".
     */
    fun format(bytesPerSecond: Double, unit: SpeedUnit, locale: Locale = Locale.getDefault()): FormattedSpeed {
        val rate = if (bytesPerSecond.isNaN() || bytesPerSecond < 0.0) 0.0 else bytesPerSecond
        val chosen = if (unit.isAuto) pickAuto(rate, unit) else unit
        val value = chosen.fromBytesPerSecond(rate)
        return FormattedSpeed(
            value = String.format(locale, "%.${decimalsFor(value, chosen)}f", value),
            unit = chosen.label,
            spokenUnit = chosen.spoken
        )
    }

    private fun pickAuto(rate: Double, auto: SpeedUnit): SpeedUnit {
        val ladder = if (auto.isBits) SpeedUnit.bitLadder else SpeedUnit.byteLadder
        var index = 0
        for (i in ladder.indices) {
            if (ladder[i].fromBytesPerSecond(rate) >= 1.0) index = i
        }
        // After rounding, 999.95 would read as 1000.0: step up when a bigger unit exists.
        while (index < ladder.lastIndex) {
            val value = ladder[index].fromBytesPerSecond(rate)
            val rounded = String.format(Locale.ROOT, "%.${decimalsFor(value, ladder[index])}f", value).toDouble()
            if (rounded >= 1000.0) index++ else break
        }
        return ladder[index]
    }

    private fun decimalsFor(value: Double, unit: SpeedUnit): Int = when {
        unit == SpeedUnit.B_S -> 0
        value >= 100.0 -> 0
        value >= 10.0 -> 1
        else -> 2
    }
}
