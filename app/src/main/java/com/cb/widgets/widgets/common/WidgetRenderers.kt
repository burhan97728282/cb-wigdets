package com.cb.widgets.widgets.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import androidx.core.graphics.ColorUtils
import com.cb.widgets.core.render.SkeuPainter
import com.cb.widgets.core.render.WellColors
import com.cb.widgets.core.render.WidgetPalette
import com.cb.widgets.core.widgetkit.WidgetStyle
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

/** Draws the skeuomorphic widget body with the same renderer the app UI uses. */
object WidgetSurface {
    // RemoteViews bitmaps count against a per-update memory budget, so keep them modest.
    private const val MAX_PIXELS = 1_500_000L

    fun render(context: Context, widthDp: Int, heightDp: Int, widgetStyle: WidgetStyle): Bitmap {
        val density = context.resources.displayMetrics.density
        var w = (widthDp.coerceAtLeast(1) * density).roundToInt().coerceAtLeast(1)
        var h = (heightDp.coerceAtLeast(1) * density).roundToInt().coerceAtLeast(1)
        var scale = 1f
        val pixels = w.toLong() * h
        if (pixels > MAX_PIXELS) {
            scale = sqrt(MAX_PIXELS.toDouble() / pixels).toFloat()
            w = (w * scale).roundToInt().coerceAtLeast(1)
            h = (h * scale).roundToInt().coerceAtLeast(1)
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val colors = WidgetPalette.of(widgetStyle.material, widgetStyle.opacity)
        val radius = (widgetStyle.cornerDp * density * scale).coerceAtMost(min(w, h) / 2f)
        SkeuPainter.drawRaised(
            Canvas(bitmap),
            RectF(0f, 0f, w.toFloat(), h.toFloat()),
            radius, 0f, colors.raised, colors.gloss, false, false, colors.brushed
        )
        return bitmap
    }
}

/** Value-driven gauges (ring and bar) drawn into small bitmaps. The numbers themselves stay real text. */
object GaugeRenderer {

    fun ring(context: Context, widgetStyle: WidgetStyle, fraction: Float, fillArgb: Int): Bitmap {
        val density = context.resources.displayMetrics.density
        val size = (150 * density).roundToInt().coerceAtLeast(48)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val colors = WidgetPalette.of(widgetStyle.material, widgetStyle.opacity)

        val stroke = size * 0.13f
        val inset = stroke / 2f + size * 0.03f
        val rect = RectF(inset, inset, size - inset, size - inset)

        val track = Paint(Paint.ANTI_ALIAS_FLAG)
        track.style = Paint.Style.STROKE
        track.strokeWidth = stroke
        track.strokeCap = Paint.Cap.ROUND
        track.color = SkeuPainter.withAlpha(colors.ink, 0.16f)
        canvas.drawArc(rect, -90f, 360f, false, track)

        val sweep = 360f * fraction.coerceIn(0f, 1f)
        if (sweep > 0f) {
            val fill = Paint(Paint.ANTI_ALIAS_FLAG)
            fill.style = Paint.Style.STROKE
            fill.strokeWidth = stroke
            fill.strokeCap = Paint.Cap.ROUND
            fill.color = fillArgb
            canvas.drawArc(rect, -90f, sweep, false, fill)
        }
        return bitmap
    }

    fun bar(context: Context, widgetStyle: WidgetStyle, fraction: Float, widthDp: Int, fillArgb: Int): Bitmap {
        val density = context.resources.displayMetrics.density
        val w = (widthDp * density).roundToInt().coerceAtLeast(48)
        val h = (20 * density).roundToInt().coerceAtLeast(14)
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val colors = WidgetPalette.of(widgetStyle.material, widgetStyle.opacity)

        val well = WellColors(
            top = SkeuPainter.withAlpha(colors.ink, 0.26f),
            bottom = SkeuPainter.withAlpha(colors.ink, 0.10f),
            innerShadow = 0xFF000000.toInt(),
            rimLight = SkeuPainter.withAlpha(0xFFFFFFFF.toInt(), 0.35f),
            border = SkeuPainter.withAlpha(colors.ink, 0.35f)
        )
        val trackRect = RectF(1.5f, 1.5f, w - 1.5f, h - 3f)
        SkeuPainter.drawRecessed(canvas, trackRect, trackRect.height() / 2f, h * 0.25f, well, false)

        val f = fraction.coerceIn(0f, 1f)
        if (f > 0f) {
            val pad = h * 0.16f
            val inner = RectF(trackRect.left + pad, trackRect.top + pad, trackRect.right - pad, trackRect.bottom - pad)
            val fillRight = (inner.left + inner.width() * f).coerceAtLeast(inner.left + inner.height())
            val fillRect = RectF(inner.left, inner.top, min(fillRight, inner.right), inner.bottom)
            val r = fillRect.height() / 2f

            val body = Paint(Paint.ANTI_ALIAS_FLAG)
            body.style = Paint.Style.FILL
            body.shader = LinearGradient(
                0f, fillRect.top, 0f, fillRect.bottom,
                ColorUtils.blendARGB(fillArgb, 0xFFFFFFFF.toInt(), 0.3f), fillArgb,
                Shader.TileMode.CLAMP
            )
            canvas.drawRoundRect(fillRect, r, r, body)

            val sheen = Paint(Paint.ANTI_ALIAS_FLAG)
            sheen.style = Paint.Style.FILL
            sheen.shader = LinearGradient(
                0f, fillRect.top, 0f, fillRect.centerY(),
                SkeuPainter.withAlpha(0xFFFFFFFF.toInt(), 0.45f), 0x00FFFFFF,
                Shader.TileMode.CLAMP
            )
            canvas.drawRoundRect(fillRect, r, r, sheen)
        }
        return bitmap
    }
}
