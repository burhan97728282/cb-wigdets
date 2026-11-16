package com.cb.widgets.core.metrics

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class SpeedFormatterTest {

    private fun fmt(bytesPerSecond: Double, unit: SpeedUnit) = SpeedFormatter.format(bytesPerSecond, unit, Locale.US)

    @Test
    fun bitsAreEightTimesBytes() {
        // 1,250,000 bytes/s = 10,000,000 bits/s = 10 Mb/s = 1.25 MB/s
        assertEquals(10.0, SpeedUnit.MBIT_S.fromBytesPerSecond(1_250_000.0), 1e-9)
        assertEquals(1.25, SpeedUnit.MB_S.fromBytesPerSecond(1_250_000.0), 1e-9)
        assertEquals(8.0, SpeedUnit.MBIT_S.fromBytesPerSecond(1_000_000.0), 1e-9)
    }

    @Test
    fun kilobitsAndKilobytes() {
        assertEquals(1.0, SpeedUnit.KBIT_S.fromBytesPerSecond(125.0), 1e-9)
        assertEquals(1.0, SpeedUnit.KB_S.fromBytesPerSecond(1_000.0), 1e-9)
        assertEquals(1_000.0, SpeedUnit.B_S.fromBytesPerSecond(1_000.0), 1e-9)
    }

    @Test
    fun labelsAreCorrect() {
        assertEquals("Mb/s", fmt(125_000.0, SpeedUnit.MBIT_S).unit)
        assertEquals("MB/s", fmt(1_000_000.0, SpeedUnit.MB_S).unit)
        assertEquals("Kb/s", fmt(125.0, SpeedUnit.KBIT_S).unit)
        assertEquals("KB/s", fmt(1_000.0, SpeedUnit.KB_S).unit)
        assertEquals("B/s", fmt(5.0, SpeedUnit.B_S).unit)
    }

    @Test
    fun fixedUnitValues() {
        assertEquals("10.0", fmt(1_250_000.0, SpeedUnit.MBIT_S).value)
        assertEquals("1.25", fmt(1_250_000.0, SpeedUnit.MB_S).value)
        assertEquals("0.00", fmt(0.0, SpeedUnit.MBIT_S).value)
        assertEquals("512", fmt(512.0, SpeedUnit.B_S).value)
        assertEquals("123", fmt(15_375_000.0, SpeedUnit.MBIT_S).value)
    }

    @Test
    fun autoBitsPicksTheRightUnit() {
        assertEquals("Kb/s", fmt(500.0, SpeedUnit.AUTO_BITS).unit)       // 4 Kb/s
        assertEquals("Mb/s", fmt(1_250_000.0, SpeedUnit.AUTO_BITS).unit) // 10 Mb/s
        assertEquals("Gb/s", fmt(250_000_000.0, SpeedUnit.AUTO_BITS).unit)
    }

    @Test
    fun autoBytesPicksTheRightUnit() {
        assertEquals("B/s", fmt(900.0, SpeedUnit.AUTO_BYTES).unit)
        assertEquals("KB/s", fmt(12_000.0, SpeedUnit.AUTO_BYTES).unit)
        assertEquals("MB/s", fmt(3_400_000.0, SpeedUnit.AUTO_BYTES).unit)
    }

    @Test
    fun autoNeverShowsOneThousand() {
        // 124,999 bytes/s = 999.992 Kb/s, which would round to "1000" so it must become Mb/s.
        val r = fmt(124_999.0, SpeedUnit.AUTO_BITS)
        assertEquals("Mb/s", r.unit)
        assertEquals("1.00", r.value)
    }

    @Test
    fun zeroInAutoUsesSmallestUnit() {
        assertEquals("Kb/s", fmt(0.0, SpeedUnit.AUTO_BITS).unit)
        assertEquals("B/s", fmt(0.0, SpeedUnit.AUTO_BYTES).unit)
    }

    @Test
    fun negativeAndNanBecomeZero() {
        assertEquals("0", fmt(-5.0, SpeedUnit.B_S).value)
        assertEquals("0", fmt(Double.NaN, SpeedUnit.B_S).value)
    }

    @Test
    fun spokenUnitsSayBitsOrBytes() {
        assertEquals("megabits per second", fmt(125_000.0, SpeedUnit.MBIT_S).spokenUnit)
        assertEquals("megabytes per second", fmt(1_000_000.0, SpeedUnit.MB_S).spokenUnit)
    }
}
