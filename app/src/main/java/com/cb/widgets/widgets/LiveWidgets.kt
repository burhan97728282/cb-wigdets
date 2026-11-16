package com.cb.widgets.widgets

import com.cb.widgets.widgets.battery.BatteryBarWidgetProvider
import com.cb.widgets.widgets.battery.BatteryRingWidgetProvider
import com.cb.widgets.widgets.clock.AnalogClockWidgetProvider
import com.cb.widgets.widgets.clock.DateCardWidgetProvider
import com.cb.widgets.widgets.clock.DigitalClockWidgetProvider
import com.cb.widgets.widgets.clock.MinimalClockWidgetProvider
import com.cb.widgets.widgets.common.BaseWidgetProvider
import com.cb.widgets.widgets.network.NetSpeedDualWidgetProvider
import com.cb.widgets.widgets.network.NetSpeedMinimalWidgetProvider
import com.cb.widgets.widgets.network.WifiStatusWidgetProvider

/** A catalogue entry that exists as a real Android widget. Ids must match LiveWidgetIds in core.widgetkit. */
class LiveWidget(
    val id: String,
    val providerClass: Class<out BaseWidgetProvider>,
    val create: () -> BaseWidgetProvider
)

object LiveWidgets {
    val all: List<LiveWidget> = listOf(
        LiveWidget("clock_digital", DigitalClockWidgetProvider::class.java) { DigitalClockWidgetProvider() },
        LiveWidget("clock_minimal", MinimalClockWidgetProvider::class.java) { MinimalClockWidgetProvider() },
        LiveWidget("clock_analog", AnalogClockWidgetProvider::class.java) { AnalogClockWidgetProvider() },
        LiveWidget("date_card", DateCardWidgetProvider::class.java) { DateCardWidgetProvider() },
        LiveWidget("battery_ring", BatteryRingWidgetProvider::class.java) { BatteryRingWidgetProvider() },
        LiveWidget("battery_bar", BatteryBarWidgetProvider::class.java) { BatteryBarWidgetProvider() },
        LiveWidget("netspeed_minimal", NetSpeedMinimalWidgetProvider::class.java) { NetSpeedMinimalWidgetProvider() },
        LiveWidget("netspeed_dual", NetSpeedDualWidgetProvider::class.java) { NetSpeedDualWidgetProvider() },
        LiveWidget("wifi_status", WifiStatusWidgetProvider::class.java) { WifiStatusWidgetProvider() }
    )

    val battery: List<LiveWidget> = all.filter { it.id.startsWith("battery_") }

    val wifi: List<LiveWidget> = all.filter { it.id == "wifi_status" }

    fun byId(id: String): LiveWidget? = all.firstOrNull { it.id == id }
}
