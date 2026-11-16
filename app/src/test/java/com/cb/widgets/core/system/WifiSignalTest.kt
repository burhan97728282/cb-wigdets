package com.cb.widgets.core.system

import org.junit.Assert.assertEquals
import org.junit.Test

class WifiSignalTest {
    @Test
    fun levelsFollowThresholds() {
        assertEquals(4, WifiSignal.level(-40))
        assertEquals(4, WifiSignal.level(-55))
        assertEquals(3, WifiSignal.level(-60))
        assertEquals(2, WifiSignal.level(-70))
        assertEquals(1, WifiSignal.level(-80))
        assertEquals(0, WifiSignal.level(-90))
    }

    @Test
    fun invalidReadingsAreLevelZero() {
        assertEquals(0, WifiSignal.level(null))
        assertEquals(0, WifiSignal.level(-127))
        assertEquals(0, WifiSignal.level(0))
    }

    @Test
    fun labels() {
        assertEquals("Excellent", WifiSignal.label(4))
        assertEquals("Weak", WifiSignal.label(1))
    }
}
