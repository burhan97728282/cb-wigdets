package com.cb.widgets.core.metrics

/**
 * Speed units. Bits and bytes are never mixed: 1 byte = 8 bits, and every "K/M/G" step is decimal (x1000),
 * which is how internet providers and network tools quote speeds.
 *
 *   Kb/s, Mb/s, Gb/s = kilo/mega/giga BITS per second
 *   KB/s, MB/s, GB/s = kilo/mega/giga BYTES per second
 */
enum class SpeedUnit(
    val label: String,
    val spoken: String,
    val isBits: Boolean,
    /** Bytes per second that equal 1 of this unit. 0 for the AUTO entries. */
    val bytesPerUnit: Double,
    val isAuto: Boolean = false
) {
    AUTO_BITS("Auto", "bits per second", true, 0.0, true),
    KBIT_S("Kb/s", "kilobits per second", true, 125.0),
    MBIT_S("Mb/s", "megabits per second", true, 125_000.0),
    GBIT_S("Gb/s", "gigabits per second", true, 125_000_000.0),

    AUTO_BYTES("Auto", "bytes per second", false, 0.0, true),
    B_S("B/s", "bytes per second", false, 1.0),
    KB_S("KB/s", "kilobytes per second", false, 1_000.0),
    MB_S("MB/s", "megabytes per second", false, 1_000_000.0),
    GB_S("GB/s", "gigabytes per second", false, 1_000_000_000.0);

    /** Converts a raw rate in bytes per second into this unit. Not meaningful for AUTO entries. */
    fun fromBytesPerSecond(bytesPerSecond: Double): Double =
        if (bytesPerUnit > 0.0) bytesPerSecond / bytesPerUnit else bytesPerSecond

    companion object {
        /** Units a user can pick in Settings when measuring in bits. */
        val bitChoices: List<SpeedUnit> get() = listOf(AUTO_BITS, KBIT_S, MBIT_S)

        /** Units a user can pick in Settings when measuring in bytes. */
        val byteChoices: List<SpeedUnit> get() = listOf(AUTO_BYTES, B_S, KB_S, MB_S)

        /** Steps used by the AUTO entries, smallest to largest. */
        val bitLadder: List<SpeedUnit> get() = listOf(KBIT_S, MBIT_S, GBIT_S)
        val byteLadder: List<SpeedUnit> get() = listOf(B_S, KB_S, MB_S, GB_S)

        fun fromName(name: String?): SpeedUnit? = entries.firstOrNull { it.name == name }
    }
}
