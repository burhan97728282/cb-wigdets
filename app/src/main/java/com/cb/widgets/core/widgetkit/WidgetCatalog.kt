package com.cb.widgets.core.widgetkit

import com.cb.widgets.core.widgetkit.PreviewKind.*
import com.cb.widgets.core.widgetkit.WidgetCategory.*
import com.cb.widgets.core.widgetkit.WidgetSize.*

object WidgetCatalog {

    val all: List<WidgetSpec> = listOf(
        // ---- Clock
        WidgetSpec(
            "clock_digital", "Digital Clock", CLOCK,
            "A bold digital clock with the weekday and date underneath.",
            listOf(WIDE, MEDIUM), CLOCK_DIGITAL,
            PreviewSample("10:42", "Saturday", "10 October"),
            featured = true, popular = true
        ),
        WidgetSpec(
            "clock_minimal", "Minimal Clock", CLOCK,
            "Just the time, large and quiet. Ideal for a clean home screen.",
            listOf(COMPACT, WIDE), CLOCK_MINIMAL,
            PreviewSample("10:42"),
            popular = true
        ),
        WidgetSpec(
            "clock_analog", "Analog Clock", CLOCK,
            "A classic round clock face with hour, minute and second hands.",
            listOf(SMALL, LARGE), CLOCK_ANALOG,
            PreviewSample(),
            featured = true
        ),
        WidgetSpec(
            "clock_glass", "Glass Clock", CLOCK,
            "A translucent glass panel with a polished edge and crisp time.",
            listOf(WIDE, MEDIUM), CLOCK_DIGITAL,
            PreviewSample("10:42", "Saturday", "10 October"),
            defaultMaterial = WidgetMaterial.GLASS
        ),
        WidgetSpec(
            "clock_date", "Clock & Date", CLOCK,
            "Time, day and full date together in one tidy card.",
            listOf(MEDIUM, LARGE), CLOCK_DIGITAL,
            PreviewSample("10:42", "Saturday", "10 October 2026")
        ),
        WidgetSpec(
            "clock_world", "World Clock", CLOCK,
            "Local times for the cities you choose.",
            listOf(MEDIUM, LARGE), ROWS,
            PreviewSample(rows = listOf("Karachi" to "10:42", "London" to "06:42", "New York" to "01:42", "Tokyo" to "14:42"))
        ),
        WidgetSpec(
            "date_card", "Date Card", CLOCK,
            "A large calendar-style date with weekday, month and year.",
            listOf(SMALL, MEDIUM), DATE_CARD,
            PreviewSample("10", "Saturday", "October 2026"),
            popular = true
        ),

        // ---- Weather
        WidgetSpec(
            "weather_compact", "Compact Weather", WEATHER,
            "Current temperature and conditions for your location or a chosen city.",
            listOf(COMPACT, SMALL), READOUT,
            PreviewSample("28°", "Clear sky", "Current location")
        ),
        WidgetSpec(
            "weather_card", "Weather Card", WEATHER,
            "Temperature with feels-like, humidity, wind and sunset.",
            listOf(MEDIUM, LARGE), ROWS,
            PreviewSample("28°", "Clear sky", rows = listOf("Feels like" to "30°", "Humidity" to "42%", "Wind" to "9 km/h", "Sunset" to "18:03"))
        ),
        WidgetSpec(
            "weather_forecast", "Weather Forecast", WEATHER,
            "Today at a glance plus the next five days.",
            listOf(WIDE, MEDIUM), FORECAST,
            PreviewSample("28°", "Clear sky", rows = listOf("Sun" to "29°", "Mon" to "27°", "Tue" to "26°", "Wed" to "28°", "Thu" to "30°")),
            featured = true
        ),
        WidgetSpec(
            "weather_clock", "Weather Clock", WEATHER,
            "A clock face that also shows the current weather.",
            listOf(WIDE, MEDIUM), CLOCK_DIGITAL,
            PreviewSample("10:42", "Saturday", "28° · Clear sky"),
            defaultMaterial = WidgetMaterial.GLASS
        ),

        // ---- Battery
        WidgetSpec(
            "battery_ring", "Battery Ring", BATTERY,
            "A circular gauge with your real battery level and charging state.",
            listOf(SMALL), RING,
            PreviewSample("76%", caption = "Battery", fraction = 0.76f),
            popular = true, featured = true
        ),
        WidgetSpec(
            "battery_bar", "Battery Bar", BATTERY,
            "A slim horizontal battery bar with percentage and charging status.",
            listOf(COMPACT, WIDE), BAR,
            PreviewSample("76%", "Charging", "Battery", 0.76f)
        ),
        WidgetSpec(
            "battery_card", "Battery Card", BATTERY,
            "Level, charging status, temperature and health in one card.",
            listOf(MEDIUM, LARGE), ROWS,
            PreviewSample("76%", "Charging", rows = listOf("Status" to "Charging", "Temperature" to "31.2 °C", "Health" to "Good"))
        ),

        // ---- Network (including Net Speed)
        WidgetSpec(
            "netspeed_minimal", "Net Speed Minimal", NETWORK,
            "Your current download speed on a recessed digital display, measured from real traffic counters.",
            listOf(COMPACT, WIDE), SPEED_MINIMAL,
            PreviewSample("12.4", "Mb/s", "Download"),
            popular = true, featured = true
        ),
        WidgetSpec(
            "netspeed_dual", "Download & Upload", NETWORK,
            "Live download and upload speed side by side.",
            listOf(WIDE, MEDIUM), SPEED_DUAL,
            PreviewSample(rows = listOf("Download" to "12.4 Mb/s", "Upload" to "1.8 Mb/s")),
            popular = true, featured = true
        ),
        WidgetSpec(
            "netspeed_meter", "Compact Speed Meter", NETWORK,
            "A small arc meter with your maximum-speed scale of choice.",
            listOf(SMALL), SPEED_DIAL,
            PreviewSample("12.4", "Mb/s", "Download", 0.42f)
        ),
        WidgetSpec(
            "netspeed_speedometer", "Digital Speedometer", NETWORK,
            "A skeuomorphic speedometer dial with a digital read-out.",
            listOf(SMALL, MEDIUM), SPEED_DIAL,
            PreviewSample("56.8", "Mb/s", "Download", 0.7f),
            defaultMaterial = WidgetMaterial.OBSIDIAN
        ),
        WidgetSpec(
            "netspeed_glass", "Glass Speed Monitor", NETWORK,
            "Download and upload speed on a translucent glass panel.",
            listOf(WIDE, MEDIUM), SPEED_DUAL,
            PreviewSample(rows = listOf("Download" to "12.4 Mb/s", "Upload" to "1.8 Mb/s")),
            defaultMaterial = WidgetMaterial.GLASS
        ),
        WidgetSpec(
            "netspeed_status", "Network Status + Speed", NETWORK,
            "Speed together with the active connection type.",
            listOf(MEDIUM), ROWS,
            PreviewSample("12.4 Mb/s", "Download", rows = listOf("Upload" to "1.8 Mb/s", "Connection" to "Wi-Fi"))
        ),
        WidgetSpec(
            "netspeed_dashboard", "Internet Dashboard", NETWORK,
            "Download, upload, session peak and connection type in one dashboard.",
            listOf(LARGE), ROWS,
            PreviewSample("12.4 Mb/s", "Download", rows = listOf("Upload" to "1.8 Mb/s", "Session peak" to "48.2 Mb/s", "Connection" to "Wi-Fi"))
        ),
        WidgetSpec(
            "netspeed_wifi", "Net Speed + Wi-Fi", NETWORK,
            "Live speed alongside Wi-Fi status and the link rate of your router.",
            listOf(MEDIUM), ROWS,
            PreviewSample("12.4 Mb/s", "Download", rows = listOf("Wi-Fi" to "Connected", "Link rate" to "433 Mb/s"))
        ),
        WidgetSpec(
            "wifi_status", "Wi-Fi Status", NETWORK,
            "Shows whether you are connected to Wi-Fi and how strong the signal is.",
            listOf(COMPACT, SMALL), READOUT,
            PreviewSample("Connected", "Signal: Excellent", "Wi-Fi")
        ),
        WidgetSpec(
            "signal_strength", "Signal Strength", NETWORK,
            "Mobile signal bars with the network type, such as LTE or 5G.",
            listOf(COMPACT, SMALL), SIGNAL_BARS,
            PreviewSample("LTE", "Strong signal", fraction = 0.8f)
        ),

        // ---- SIM
        WidgetSpec(
            "sim_status", "SIM Status", SIM,
            "SIM 1 and SIM 2 state and which SIM is used for mobile data.",
            listOf(MEDIUM), ROWS,
            PreviewSample(rows = listOf("SIM 1" to "Ready", "SIM 2" to "Ready", "Mobile data" to "SIM 1"))
        ),

        // ---- Storage / Memory
        WidgetSpec(
            "storage_ring", "Storage Ring", STORAGE,
            "How much of your internal storage is used.",
            listOf(SMALL), RING,
            PreviewSample("58%", caption = "Storage used", fraction = 0.58f)
        ),
        WidgetSpec(
            "storage_bar", "Storage Bar", STORAGE,
            "Used and total internal storage with a progress bar.",
            listOf(COMPACT, WIDE), BAR,
            PreviewSample("74 GB", "of 128 GB", "Internal storage", 0.58f)
        ),
        WidgetSpec(
            "memory_ring", "Memory Ring", MEMORY,
            "RAM currently in use, shown as a circular gauge.",
            listOf(SMALL), RING,
            PreviewSample("62%", caption = "Memory used", fraction = 0.62f)
        ),

        // ---- Calendar
        WidgetSpec(
            "calendar_month", "Month Calendar", CALENDAR,
            "The current month with today highlighted.",
            listOf(MEDIUM, LARGE), CALENDAR_MONTH,
            PreviewSample(),
            popular = true
        ),
        WidgetSpec(
            "calendar_next", "Next Event", CALENDAR,
            "Your next calendar event. Needs calendar permission.",
            listOf(WIDE, MEDIUM), READOUT,
            PreviewSample("10:30", "Design review", "Next event")
        ),
        WidgetSpec(
            "calendar_agenda", "Agenda", CALENDAR,
            "Today's upcoming events in a list. Needs calendar permission.",
            listOf(MEDIUM, LARGE), ROWS,
            PreviewSample(rows = listOf("09:00" to "Stand-up", "10:30" to "Design review", "14:00" to "Lunch"))
        ),

        // ---- Device / System
        WidgetSpec(
            "device_info", "Device Info", DEVICE,
            "Android version, manufacturer, model and screen resolution.",
            listOf(MEDIUM), ROWS,
            PreviewSample(rows = listOf("Android" to "15", "Manufacturer" to "Brand", "Model" to "Model", "Screen" to "1080 × 2400"))
        ),
        WidgetSpec(
            "system_dashboard", "System Dashboard", SYSTEM,
            "Battery, Wi-Fi, storage, memory and time at a glance.",
            listOf(MEDIUM, LARGE), ROWS,
            PreviewSample(rows = listOf("Battery" to "76%", "Wi-Fi" to "Connected", "Storage" to "58%", "Memory" to "62%", "Time" to "10:42")),
            featured = true
        ),

        // ---- Productivity
        WidgetSpec(
            "todo_list", "To-do List", PRODUCTIVITY,
            "A short checklist you can tick off from the home screen.",
            listOf(MEDIUM, LARGE), ROWS,
            PreviewSample(rows = listOf("1" to "Send report", "2" to "Buy groceries", "3" to "Water plants"))
        ),
        WidgetSpec(
            "countdown", "Countdown", PRODUCTIVITY,
            "Days remaining until a date you choose.",
            listOf(COMPACT, SMALL), READOUT,
            PreviewSample("12", "days to go", "Countdown")
        )
    )

    private val index: Map<String, WidgetSpec> = all.associateBy { it.id }

    fun byId(id: String): WidgetSpec? = index[id]

    fun countIn(category: WidgetCategory): Int = all.count { it.category == category }

    fun search(query: String, category: WidgetCategory?): List<WidgetSpec> {
        val q = query.trim().lowercase()
        return all.filter { spec ->
            (category == null || spec.category == category) &&
                (q.isEmpty() ||
                    spec.name.lowercase().contains(q) ||
                    spec.description.lowercase().contains(q) ||
                    spec.category.label.lowercase().contains(q))
        }
    }
}
