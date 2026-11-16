package com.cb.widgets.core.render

/** The recessed dark "LCD" look used by the Net Speed widgets, in both light and dark themes. */
object LcdPalette {
    val well = WellColors(
        top = 0xFF0E1A15.toInt(),
        bottom = 0xFF1C2E26.toInt(),
        innerShadow = 0xFF000000.toInt(),
        rimLight = 0xFFFFFFFF.toInt(),
        border = 0xFF000000.toInt()
    )
    const val TEXT: Int = 0xFF8CF5B8.toInt()
    const val TEXT_DIM: Int = 0xB38CF5B8.toInt()
}
