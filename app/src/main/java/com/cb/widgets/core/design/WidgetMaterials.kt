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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.cb.widgets.core.render.RaisedColors
import com.cb.widgets.core.render.SkeuPainter
import com.cb.widgets.core.render.WidgetPalette
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

fun materialSpec(material: WidgetMaterial, opacity: Float): MaterialSpec {
    val c = WidgetPalette.of(material, opacity)
    return MaterialSpec(
        raised = c.raised,
        ink = Color(c.ink),
        inkSecondary = Color(c.inkSecondary),
        brushed = c.brushed,
        gloss = c.gloss
    )
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
