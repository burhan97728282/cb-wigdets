package com.cb.widgets.core.design

import android.graphics.RectF
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.cb.widgets.core.render.RaisedColors
import com.cb.widgets.core.render.SkeuPainter
import com.cb.widgets.core.widgetkit.WidgetMaterial
import com.cb.widgets.core.widgetkit.WidgetStyle

/** Surface + ink colours of one widget material. */
class MaterialSpec(
    val raised: RaisedColors,
    val ink: Color,
    val inkSecondary: Color,
    val brushed: Boolean,
    val gloss: Boolean
)

val LocalMaterialSpec = compositionLocalOf<MaterialSpec> {
    materialSpec(WidgetMaterial.MATTE, 1f)
}

private fun scaleAlpha(argb: Int, factor: Float): Int = SkeuPainter.withAlpha(argb, factor)

fun materialSpec(material: WidgetMaterial, opacity: Float): MaterialSpec {
    val o = opacity.coerceIn(0.3f, 1f)
    return when (material) {
        WidgetMaterial.MATTE -> MaterialSpec(
            raised = RaisedColors(
                faceTop = scaleAlpha(0xFFF4F1EA.toInt(), o),
                faceBottom = scaleAlpha(0xFFDCD7CC.toInt(), o),
                highlight = 0xFFFFFFFF.toInt(),
                lowlight = 0xFF9E9889.toInt(),
                shadow = 0xFF000000.toInt()
            ),
            ink = Color(0xFF1F1D19),
            inkSecondary = Color(0xFF55514A),
            brushed = false,
            gloss = true
        )
        WidgetMaterial.METAL -> MaterialSpec(
            raised = RaisedColors(
                faceTop = scaleAlpha(0xFFE3E6EA.toInt(), o),
                faceBottom = scaleAlpha(0xFFB5BAC2.toInt(), o),
                highlight = 0xFFFFFFFF.toInt(),
                lowlight = 0xFF70757E.toInt(),
                shadow = 0xFF000000.toInt()
            ),
            ink = Color(0xFF1A1D22),
            inkSecondary = Color(0xFF3F444C),
            brushed = true,
            gloss = true
        )
        WidgetMaterial.GLASS -> MaterialSpec(
            raised = RaisedColors(
                faceTop = scaleAlpha(0xCC566377.toInt(), o),
                faceBottom = scaleAlpha(0xCC2B3342.toInt(), o),
                highlight = 0xFFFFFFFF.toInt(),
                lowlight = 0xFF0F1218.toInt(),
                shadow = 0xFF000000.toInt()
            ),
            ink = Color(0xFFF4F6F8),
            inkSecondary = Color(0xFFD0D5DD),
            brushed = false,
            gloss = true
        )
        WidgetMaterial.OBSIDIAN -> MaterialSpec(
            raised = RaisedColors(
                faceTop = scaleAlpha(0xFF2C2E33.toInt(), o),
                faceBottom = scaleAlpha(0xFF141517.toInt(), o),
                highlight = 0xFF6A6E77.toInt(),
                lowlight = 0xFF000000.toInt(),
                shadow = 0xFF000000.toInt()
            ),
            ink = Color(0xFFF1EEE8),
            inkSecondary = Color(0xFFBDB9B0),
            brushed = false,
            gloss = true
        )
    }
}

/**
 * Draws the widget body (face, gloss, bevel) with the shared renderer.
 * No outer shadow: a real home-screen widget cannot draw outside its bounds either.
 */
@Composable
fun WidgetFrame(
    style: WidgetStyle,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val spec = remember(style.material, style.opacity) { materialSpec(style.material, style.opacity) }
    val reduced = LocalReduceEffects.current
    val radiusPx = with(LocalDensity.current) { style.cornerDp.dp.toPx() }
    CompositionLocalProvider(
        LocalMaterialSpec provides spec,
        LocalContentColor provides spec.ink
    ) {
        Box(
            modifier = modifier
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        SkeuPainter.drawRaised(
                            canvas.nativeCanvas,
                            RectF(0f, 0f, size.width, size.height),
                            radiusPx, 0f, spec.raised, spec.gloss, false, reduced, spec.brushed
                        )
                    }
                }
                .padding(16.dp),
            content = content
        )
    }
}
