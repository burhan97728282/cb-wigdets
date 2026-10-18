package com.cb.widgets.core.render

import com.cb.widgets.core.widgetkit.WidgetMaterial

/** Surface and ink colours of one widget material, as plain ARGB ints. */
data class MaterialColors(
    val raised: RaisedColors,
    val ink: Int,
    val inkSecondary: Int,
    val brushed: Boolean,
    val gloss: Boolean
)

/** Single source of truth for widget materials. Used by in-app previews AND real home-screen widgets. */
object WidgetPalette {

    fun of(material: WidgetMaterial, opacity: Float): MaterialColors {
        val o = opacity.coerceIn(0.3f, 1f)
        return when (material) {
            WidgetMaterial.MATTE -> MaterialColors(
                raised = RaisedColors(
                    faceTop = SkeuPainter.withAlpha(0xFFF4F1EA.toInt(), o),
                    faceBottom = SkeuPainter.withAlpha(0xFFDCD7CC.toInt(), o),
                    highlight = 0xFFFFFFFF.toInt(),
                    lowlight = 0xFF9E9889.toInt(),
                    shadow = 0xFF000000.toInt()
                ),
                ink = 0xFF1F1D19.toInt(),
                inkSecondary = 0xFF55514A.toInt(),
                brushed = false,
                gloss = true
            )
            WidgetMaterial.METAL -> MaterialColors(
                raised = RaisedColors(
                    faceTop = SkeuPainter.withAlpha(0xFFE3E6EA.toInt(), o),
                    faceBottom = SkeuPainter.withAlpha(0xFFB5BAC2.toInt(), o),
                    highlight = 0xFFFFFFFF.toInt(),
                    lowlight = 0xFF70757E.toInt(),
                    shadow = 0xFF000000.toInt()
                ),
                ink = 0xFF1A1D22.toInt(),
                inkSecondary = 0xFF3F444C.toInt(),
                brushed = true,
                gloss = true
            )
            WidgetMaterial.GLASS -> MaterialColors(
                raised = RaisedColors(
                    faceTop = SkeuPainter.withAlpha(0xCC566377.toInt(), o),
                    faceBottom = SkeuPainter.withAlpha(0xCC2B3342.toInt(), o),
                    highlight = 0xFFFFFFFF.toInt(),
                    lowlight = 0xFF0F1218.toInt(),
                    shadow = 0xFF000000.toInt()
                ),
                ink = 0xFFF4F6F8.toInt(),
                inkSecondary = 0xFFD0D5DD.toInt(),
                brushed = false,
                gloss = true
            )
            WidgetMaterial.OBSIDIAN -> MaterialColors(
                raised = RaisedColors(
                    faceTop = SkeuPainter.withAlpha(0xFF2C2E33.toInt(), o),
                    faceBottom = SkeuPainter.withAlpha(0xFF141517.toInt(), o),
                    highlight = 0xFF6A6E77.toInt(),
                    lowlight = 0xFF000000.toInt(),
                    shadow = 0xFF000000.toInt()
                ),
                ink = 0xFFF1EEE8.toInt(),
                inkSecondary = 0xFFBDB9B0.toInt(),
                brushed = false,
                gloss = true
            )
        }
    }
}
