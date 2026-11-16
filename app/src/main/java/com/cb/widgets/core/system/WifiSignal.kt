package com.cb.widgets.core.system

/** Turns a Wi-Fi RSSI (dBm) into a 0..4 level and a word. Thresholds follow common Android conventions. */
object WifiSignal {
    const val MAX_LEVEL = 4

    fun level(rssiDbm: Int?): Int = when {
        rssiDbm == null || rssiDbm <= -127 || rssiDbm >= 0 -> 0
        rssiDbm >= -55 -> 4
        rssiDbm >= -65 -> 3
        rssiDbm >= -75 -> 2
        rssiDbm >= -85 -> 1
        else -> 0
    }

    fun label(level: Int): String = when (level) {
        4 -> "Excellent"
        3 -> "Good"
        2 -> "Fair"
        1 -> "Weak"
        else -> "Very weak"
    }
}
