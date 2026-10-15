package com.cb.widgets.core.design

import android.graphics.RectF
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cb.widgets.core.render.RaisedColors
import com.cb.widgets.core.render.SkeuPainter
import com.cb.widgets.core.render.WellColors

/** Raised (convex) surface drawn by the shared renderer. */
fun Modifier.skeuRaised(
    radius: Dp = SkeuDims.CardRadius,
    depth: Dp = SkeuDims.DepthMedium,
    gloss: Boolean = false,
    pressed: Boolean = false,
    colors: RaisedColors? = null
): Modifier = composed {
    val palette = LocalSkeu.current
    val reduced = LocalReduceEffects.current
    val density = LocalDensity.current
    val radiusPx = with(density) { radius.toPx() }
    val depthPx = with(density) { depth.toPx() }
    val face = colors ?: palette.raised
    drawBehind {
        drawIntoCanvas { canvas ->
            SkeuPainter.drawRaised(
                canvas.nativeCanvas,
                RectF(0f, 0f, size.width, size.height),
                radiusPx, depthPx, face, gloss, pressed, reduced
            )
        }
    }
}

/** Recessed (concave) well. */
fun Modifier.skeuRecessed(
    radius: Dp = SkeuDims.ControlRadius,
    depth: Dp = 5.dp,
    colors: WellColors? = null
): Modifier = composed {
    val palette = LocalSkeu.current
    val reduced = LocalReduceEffects.current
    val density = LocalDensity.current
    val radiusPx = with(density) { radius.toPx() }
    val depthPx = with(density) { depth.toPx() }
    val well = colors ?: palette.well
    drawBehind {
        drawIntoCanvas { canvas ->
            SkeuPainter.drawRecessed(
                canvas.nativeCanvas,
                RectF(0f, 0f, size.width, size.height),
                radiusPx, depthPx, well, reduced
            )
        }
    }
}

/** Inner shadow drawn OVER the content: makes a stage look like a hole in the surface. */
fun Modifier.skeuInset(
    radius: Dp = 18.dp,
    depth: Dp = 7.dp
): Modifier = composed {
    val palette = LocalSkeu.current
    val reduced = LocalReduceEffects.current
    val density = LocalDensity.current
    val radiusPx = with(density) { radius.toPx() }
    val depthPx = with(density) { depth.toPx() }
    drawWithContent {
        drawContent()
        if (!reduced) {
            drawIntoCanvas { canvas ->
                SkeuPainter.drawInnerShadow(
                    canvas.nativeCanvas,
                    RectF(0f, 0f, size.width, size.height),
                    radiusPx, depthPx, palette.well.innerShadow
                )
            }
        }
    }
}
