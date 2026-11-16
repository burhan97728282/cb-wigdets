package com.cb.widgets.core.metrics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpeedCalculatorTest {

    private val second = 1_000_000_000L

    private fun sample(seconds: Double, rx: Long, tx: Long) =
        CounterSample((seconds * second).toLong(), rx, tx)

    @Test
    fun rateIsBytesDifferenceOverElapsedSeconds() {
        val r = SpeedCalculator.compute(sample(0.0, 0, 0), sample(2.0, 2_000_000, 500_000))
        assertEquals(ReadingQuality.LIVE, r.quality)
        assertEquals(1_000_000.0, r.downBytesPerSecond, 1e-6)
        assertEquals(250_000.0, r.upBytesPerSecond, 1e-6)
    }

    @Test
    fun firstSampleIsInitializing() {
        assertEquals(ReadingQuality.INITIALIZING, SpeedCalculator.compute(null, sample(1.0, 100, 100)).quality)
    }

    @Test
    fun unsupportedCountersAreUnavailableNotZero() {
        val r = SpeedCalculator.compute(sample(0.0, 0, 0), sample(1.0, -1, -1))
        assertEquals(ReadingQuality.UNAVAILABLE, r.quality)
    }

    @Test
    fun unsupportedPreviousSampleIsInitializing() {
        val r = SpeedCalculator.compute(sample(0.0, -1, -1), sample(1.0, 100, 100))
        assertEquals(ReadingQuality.INITIALIZING, r.quality)
    }

    @Test
    fun counterResetIsDiscarded() {
        val r = SpeedCalculator.compute(sample(0.0, 5_000_000, 5_000_000), sample(1.0, 1_000, 1_000))
        assertEquals(ReadingQuality.INITIALIZING, r.quality)
        assertEquals(0.0, r.downBytesPerSecond, 0.0)
    }

    @Test
    fun onlyOneCounterResettingIsStillDiscarded() {
        val r = SpeedCalculator.compute(sample(0.0, 100, 5_000), sample(1.0, 200, 10))
        assertEquals(ReadingQuality.INITIALIZING, r.quality)
    }

    @Test
    fun tooShortIntervalIsInitializing() {
        val r = SpeedCalculator.compute(sample(0.0, 0, 0), sample(0.1, 1_000, 1_000))
        assertEquals(ReadingQuality.INITIALIZING, r.quality)
    }

    @Test
    fun clockGoingBackwardsIsInitializing() {
        val r = SpeedCalculator.compute(sample(5.0, 0, 0), sample(4.0, 1_000, 1_000))
        assertEquals(ReadingQuality.INITIALIZING, r.quality)
    }

    @Test
    fun longGapIsAveragedNotLive() {
        val r = SpeedCalculator.compute(sample(0.0, 0, 0), sample(900.0, 900_000_000, 0))
        assertEquals(ReadingQuality.AVERAGED, r.quality)
        assertEquals(1_000_000.0, r.downBytesPerSecond, 1e-6)
    }

    @Test
    fun idleNetworkIsZeroLive() {
        val r = SpeedCalculator.compute(sample(0.0, 500, 500), sample(1.5, 500, 500))
        assertEquals(ReadingQuality.LIVE, r.quality)
        assertEquals(0.0, r.downBytesPerSecond, 0.0)
        assertEquals(0.0, r.upBytesPerSecond, 0.0)
    }

    @Test
    fun veryLargeCountersDoNotOverflow() {
        val big = 9_000_000_000_000_000L
        val r = SpeedCalculator.compute(sample(0.0, big, big), sample(1.0, big + 125_000_000L, big))
        assertEquals(125_000_000.0, r.downBytesPerSecond, 1e-3)
        assertTrue(r.upBytesPerSecond == 0.0)
    }
}
