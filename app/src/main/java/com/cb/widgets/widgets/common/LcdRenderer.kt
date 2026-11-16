package com.cb.widgets.widgets.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import com.cb.widgets.core.render.LcdPalette
import com.cb.widgets.core.render.SkeuPainter
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

/** Draws a recessed dark LCD well behind live text, using the same renderer as everything else. */
object LcdRenderer {
    private const val MAX_PIXELS = 900_000L

    fun render(context: Context, widthDp: Int, heightDp: Int): Bitmap {
        val density = context.resources.displayMetrics.density
        var w = (widthDp.coerceAtLeast(8) * density).roundToInt().coerceAtLeast(1)
        var h = (heightDp.coerceAtLeast(8) * density).roundToInt().coerceAtLeast(1)
        var scale = 1f
        val pixels = w.toLong() * h
        if (pixels > MAX_PIXELS) {
            scale = sqrt(MAX_PIXELS.toDouble() / pixels).toFloat()
            w = (w * scale).roundToInt().coerceAtLeast(1)
            h = (h * scale).roundToInt().coerceAtLeast(1)
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val radius = min(14f * density * scale, min(w, h) / 3f)
        SkeuPainter.drawRecessed(
            Canvas(bitmap),
            RectF(0f, 0f, w.toFloat(), h.toFloat()),
            radius, 6f * density * scale, LcdPalette.well, false
        )
        return bitmap
    }
}
