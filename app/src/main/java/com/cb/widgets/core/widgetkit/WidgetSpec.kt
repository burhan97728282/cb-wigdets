package com.cb.widgets.core.widgetkit

enum class WidgetCategory(val label: String) {
    CLOCK("Clock"),
    WEATHER("Weather"),
    BATTERY("Battery"),
    NETWORK("Network"),
    SIM("SIM"),
    STORAGE("Storage"),
    MEMORY("Memory"),
    CALENDAR("Calendar"),
    PRODUCTIVITY("Productivity"),
    DEVICE("Device"),
    SYSTEM("System")
}

enum class WidgetSize(val label: String, val grid: String) {
    COMPACT("Compact", "2×1"),
    SMALL("Small", "2×2"),
    WIDE("Wide", "4×1"),
    MEDIUM("Medium", "4×2"),
    LARGE("Large", "4×4")
}

/** Which preview layout renders this widget in the app. */
enum class PreviewKind(val aspect: Float) {
    CLOCK_DIGITAL(2.0f),
    CLOCK_MINIMAL(2.0f),
    CLOCK_ANALOG(1.0f),
    DATE_CARD(1.0f),
    READOUT(2.0f),
    RING(1.0f),
    BAR(2.2f),
    SPEED_DUAL(2.0f),
    SPEED_MINIMAL(2.2f),
    SPEED_DIAL(1.0f),
    SIGNAL_BARS(1.6f),
    ROWS(1.3f),
    FORECAST(1.8f),
    CALENDAR_MONTH(1.1f)
}

/** Clearly-labelled sample values used ONLY for in-app previews. */
data class PreviewSample(
    val primary: String = "",
    val secondary: String = "",
    val caption: String = "",
    val fraction: Float = 0f,
    val rows: List<Pair<String, String>> = emptyList()
)

data class WidgetSpec(
    val id: String,
    val name: String,
    val category: WidgetCategory,
    val description: String,
    val sizes: List<WidgetSize>,
    val kind: PreviewKind,
    val sample: PreviewSample,
    val defaultMaterial: WidgetMaterial? = null,
    val featured: Boolean = false,
    val popular: Boolean = false,
    /** True when a real home-screen widget ships for this entry. */
    val implemented: Boolean = id in LiveWidgetIds
)

/** Widgets that exist as real Android home-screen widgets. Keep in sync with LiveWidgets in the widgets package. */
val LiveWidgetIds: Set<String> = setOf(
    "clock_digital",
    "clock_minimal",
    "clock_analog",
    "date_card",
    "battery_ring",
    "battery_bar"
)

fun WidgetSpec.defaultStyle(userDefault: WidgetMaterial): WidgetStyle =
    WidgetStyle(material = defaultMaterial ?: userDefault)
