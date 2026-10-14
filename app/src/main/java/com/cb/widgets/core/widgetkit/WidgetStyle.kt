package com.cb.widgets.core.widgetkit

enum class WidgetMaterial(val label: String) {
    MATTE("Matte"),
    METAL("Metal"),
    GLASS("Glass"),
    OBSIDIAN("Obsidian")
}

/** User-adjustable look of a widget. Android-free so widgets and the app share it. */
data class WidgetStyle(
    val material: WidgetMaterial = WidgetMaterial.MATTE,
    val accentArgb: Int = 0xFFE08A1E.toInt(),
    val opacity: Float = 1f,
    val cornerDp: Int = 24
)
