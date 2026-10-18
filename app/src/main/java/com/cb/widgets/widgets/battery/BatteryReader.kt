package com.cb.widgets.widgets.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlin.math.roundToInt

enum class ChargeState { CHARGING, FULL, DISCHARGING, NOT_CHARGING, UNKNOWN }
enum class PlugType { NONE, AC, USB, WIRELESS }

data class BatteryInfo(
    val percent: Int,
    val state: ChargeState,
    val plug: PlugType
) {
    val isCharging: Boolean get() = state == ChargeState.CHARGING
}

/** Reads the real battery state from Android's sticky battery broadcast. No permission needed. */
object BatteryReader {
    fun read(context: Context): BatteryInfo? {
        val intent: Intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ?: return null
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return null

        val state = when (intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)) {
            BatteryManager.BATTERY_STATUS_CHARGING -> ChargeState.CHARGING
            BatteryManager.BATTERY_STATUS_FULL -> ChargeState.FULL
            BatteryManager.BATTERY_STATUS_DISCHARGING -> ChargeState.DISCHARGING
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> ChargeState.NOT_CHARGING
            else -> ChargeState.UNKNOWN
        }
        val plug = when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
            BatteryManager.BATTERY_PLUGGED_AC -> PlugType.AC
            BatteryManager.BATTERY_PLUGGED_USB -> PlugType.USB
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> PlugType.WIRELESS
            else -> PlugType.NONE
        }
        return BatteryInfo(
            percent = (level * 100f / scale).roundToInt().coerceIn(0, 100),
            state = state,
            plug = plug
        )
    }
}

/** Text and colours derived from real battery values. Shows "unavailable" instead of inventing numbers. */
object BatteryUi {
    private const val GREEN = 0xFF4CC38A.toInt()
    private const val RED = 0xFFE0566B.toInt()

    fun percentText(info: BatteryInfo?): String = info?.let { "${it.percent}%" } ?: "--"

    fun statusText(info: BatteryInfo?): String {
        if (info == null) return "Unavailable"
        return when (info.state) {
            ChargeState.CHARGING -> when (info.plug) {
                PlugType.AC -> "Charging · AC"
                PlugType.USB -> "Charging · USB"
                PlugType.WIRELESS -> "Charging · Wireless"
                PlugType.NONE -> "Charging"
            }
            ChargeState.FULL -> "Fully charged"
            ChargeState.DISCHARGING -> "On battery"
            ChargeState.NOT_CHARGING -> "Not charging"
            ChargeState.UNKNOWN -> ""
        }
    }

    fun fillColor(info: BatteryInfo?, accentArgb: Int): Int = when {
        info == null -> 0xFF8A8A8A.toInt()
        info.isCharging || info.state == ChargeState.FULL -> GREEN
        info.percent <= 15 -> RED
        else -> accentArgb
    }

    fun description(info: BatteryInfo?): String {
        if (info == null) return "Battery information unavailable"
        val status = statusText(info)
        return if (status.isEmpty()) "Battery ${info.percent} percent" else "Battery ${info.percent} percent, $status"
    }
}
