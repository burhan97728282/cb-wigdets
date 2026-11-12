package com.cb.widgets.core.widgetkit

enum class WidgetMaterial(val label: String) {
    MATTE("Matte"),
    METAL("Metal"),
    GLASS("Glass"),
    OBSIDIAN("Obsidian")
}

/** Text colour choice. AUTO follows the material; with no background it falls back to light text. */
enum class WidgetTextMode(val label: String) {
    AUTO("Auto"),
    LIGHT("Light"),
    DARK("Dark")
}

/** User-adjustable look of a widget. Android-free so widgets and the app share it. */
data class WidgetStyle(
    val material: WidgetMaterial = WidgetMaterial.MATTE,
    val accentArgb: Int = 0xFFE08A1E.toInt(),
    val opacity: Float = 1f,
    val cornerDp: Int = 24,
    /** false = no body at all, the widget's content sits directly on the wallpaper. */
    val showBackground: Boolean = true,
    val textMode: WidgetTextMode = WidgetTextMode.AUTO
)
