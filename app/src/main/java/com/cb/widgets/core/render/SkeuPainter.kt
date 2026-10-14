package com.cb.widgets.core.render

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader

/** Colours for a raised (convex) surface. All values are ARGB ints. */
data class RaisedColors(
    val faceTop: Int,
    val faceBottom: Int,
    val highlight: Int,
    val lowlight: Int,
    val shadow: Int
)

/** Colours for a recessed (concave) well. All values are ARGB ints. */
data class WellColors(
    val top: Int,
    val bottom: Int,
    val innerShadow: Int,
    val rimLight: Int,
    val border: Int
)

/**
 * Plain android.graphics renderer. It has no Compose dependency, so the exact same
 * drawing code is used by the in-app UI and (from Phase 4) by home-screen widget bitmaps.
 *
 * Global light source: top-left. Shadows fall to the bottom-right.
 * Soft shadows are built from stacked translucent layers instead of setShadowLayer(),
 * because setShadowLayer is unreliable on hardware canvases below API 28.
 */
object SkeuPainter {

    fun withAlpha(color: Int, factor: Float): Int {
        val a = (Color.alpha(color) * factor.coerceIn(0f, 1f)).toInt()
        return (a shl 24) or (color and 0x00FFFFFF)
    }

    private fun newPaint(): Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun drawSoftShadow(
        canvas: Canvas,
        rect: RectF,
        radius: Float,
        dx: Float,
        dy: Float,
        spread: Float,
        shadowColor: Int,
        strength: Float
    ) {
        if (spread <= 0f || strength <= 0f) return
        val steps = 8
        val paint = newPaint()
        paint.style = Paint.Style.FILL
        paint.color = withAlpha(shadowColor, strength / steps * 1.5f)
        for (i in 0 until steps) {
            val t = (i + 1f) / steps
            val grow = spread * t * t
            val r = radius + grow
            canvas.drawRoundRect(
                rect.left + dx - grow,
                rect.top + dy - grow,
                rect.right + dx + grow,
                rect.bottom + dy + grow,
                r,
                r,
                paint
            )
        }
    }

    fun drawRaised(
        canvas: Canvas,
        rect: RectF,
        radius: Float,
        depth: Float,
        colors: RaisedColors,
        gloss: Boolean,
        pressed: Boolean,
        reduced: Boolean,
        brushed: Boolean = false
    ) {
        val paint = newPaint()
        if (reduced) {
            paint.style = Paint.Style.FILL
            paint.color = colors.faceTop
            canvas.drawRoundRect(rect, radius, radius, paint)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1.5f
            paint.color = withAlpha(colors.lowlight, 0.8f)
            val edge = RectF(rect)
            edge.inset(0.75f, 0.75f)
            canvas.drawRoundRect(edge, radius, radius, paint)
            return
        }

        if (depth > 0.5f) {
            drawSoftShadow(canvas, rect, radius, depth * 0.5f, depth * 0.85f, depth * 1.8f, colors.shadow, 0.55f)
            drawSoftShadow(canvas, rect, radius, -depth * 0.45f, -depth * 0.55f, depth * 1.1f, colors.highlight, 0.75f)
        }

        val top = if (pressed) colors.faceBottom else colors.faceTop
        val bottom = if (pressed) colors.faceTop else colors.faceBottom
        paint.style = Paint.Style.FILL
        paint.shader = LinearGradient(
            rect.left, rect.top, rect.left + rect.width() * 0.35f, rect.bottom,
            top, bottom, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rect, radius, radius, paint)
        paint.shader = null

        if (brushed) drawBrushed(canvas, rect, radius, colors.lowlight)

        if (gloss) {
            canvas.save()
            val clip = Path()
            clip.addRoundRect(rect, radius, radius, Path.Direction.CW)
            canvas.clipPath(clip)
            val sheen = newPaint()
            sheen.style = Paint.Style.FILL
            val sheenAlpha = if (pressed) 0.12f else 0.32f
            sheen.shader = LinearGradient(
                rect.left, rect.top, rect.left, rect.top + rect.height() * 0.55f,
                withAlpha(Color.WHITE, sheenAlpha), withAlpha(Color.WHITE, 0f), Shader.TileMode.CLAMP
            )
            canvas.drawRect(rect.left, rect.top, rect.right, rect.top + rect.height() * 0.55f, sheen)
            canvas.restore()
        }

        val sw = if (depth <= 0.5f) 1.4f else (depth * 0.25f).coerceIn(1.2f, 2.6f)
        val bevel = newPaint()
        bevel.style = Paint.Style.STROKE
        bevel.strokeWidth = sw
        bevel.shader = LinearGradient(
            rect.left, rect.top, rect.right, rect.bottom,
            withAlpha(colors.highlight, if (pressed) 0.4f else 0.95f),
            withAlpha(colors.lowlight, 0.9f),
            Shader.TileMode.CLAMP
        )
        val edge = RectF(rect)
        edge.inset(sw / 2f, sw / 2f)
        val er = (radius - sw / 2f).coerceAtLeast(0f)
        canvas.drawRoundRect(edge, er, er, bevel)
    }

    fun drawBrushed(canvas: Canvas, rect: RectF, radius: Float, lineColor: Int) {
        canvas.save()
        val clip = Path()
        clip.addRoundRect(rect, radius, radius, Path.Direction.CW)
        canvas.clipPath(clip)
        val paint = newPaint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        var y = rect.top + 2f
        var odd = false
        while (y < rect.bottom) {
            paint.color = withAlpha(lineColor, if (odd) 0.10f else 0.04f)
            canvas.drawLine(rect.left, y, rect.right, y, paint)
            odd = !odd
            y += 3f
        }
        canvas.restore()
    }

    fun drawInnerShadow(canvas: Canvas, rect: RectF, radius: Float, depth: Float, shadowColor: Int) {
        canvas.save()
        val clip = Path()
        clip.addRoundRect(rect, radius, radius, Path.Direction.CW)
        canvas.clipPath(clip)
        val steps = depth.toInt().coerceIn(3, 9)
        val paint = newPaint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.8f
        for (i in 0 until steps) {
            val t = i / steps.toFloat()
            paint.color = withAlpha(shadowColor, (1f - t) * (1f - t) * 0.85f)
            canvas.save()
            canvas.translate(i * 0.8f + 1f, i * 1f + 1f)
            canvas.drawRoundRect(rect, radius, radius, paint)
            canvas.restore()
        }
        canvas.restore()
    }

    fun drawRecessed(
        canvas: Canvas,
        rect: RectF,
        radius: Float,
        depth: Float,
        colors: WellColors,
        reduced: Boolean
    ) {
        val paint = newPaint()
        paint.style = Paint.Style.FILL
        if (reduced) {
            paint.color = colors.top
            canvas.drawRoundRect(rect, radius, radius, paint)
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1.5f
            paint.color = colors.border
            val edge = RectF(rect)
            edge.inset(0.75f, 0.75f)
            canvas.drawRoundRect(edge, radius, radius, paint)
            return
        }

        paint.color = colors.rimLight
        canvas.drawRoundRect(rect.left, rect.top + 1.4f, rect.right, rect.bottom + 1.4f, radius, radius, paint)

        paint.shader = LinearGradient(
            rect.left, rect.top, rect.left, rect.bottom,
            colors.top, colors.bottom, Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(rect, radius, radius, paint)
        paint.shader = null

        drawInnerShadow(canvas, rect, radius, depth, colors.innerShadow)

        val border = newPaint()
        border.style = Paint.Style.STROKE
        border.strokeWidth = 1f
        border.color = withAlpha(colors.border, 0.8f)
        val edge = RectF(rect)
        edge.inset(0.5f, 0.5f)
        canvas.drawRoundRect(edge, radius, radius, border)
    }
}
