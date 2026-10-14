package com.cb.widgets.core.design

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cb.widgets.core.widgetkit.PreviewKind
import com.cb.widgets.core.widgetkit.PreviewSample
import com.cb.widgets.core.widgetkit.WidgetSpec
import com.cb.widgets.core.widgetkit.WidgetStyle
import java.time.LocalDate
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

private val DesignWidth = 320.dp

/** Lays out [content] at a fixed design size and scales it to fit the available width. */
@Composable
private fun ScaledBox(
    aspect: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier.aspectRatio(aspect)) {
        val scale = maxWidth / DesignWidth
        Box(
            Modifier
                .requiredSize(DesignWidth, DesignWidth / aspect)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            content()
        }
    }
}

/** A widget rendered from sample data. Used for gallery cards, detail and customisation previews. */
@Composable
fun WidgetPreview(spec: WidgetSpec, style: WidgetStyle, modifier: Modifier = Modifier) {
    ScaledBox(aspect = spec.kind.aspect, modifier = modifier) {
        WidgetFrame(style = style, modifier = Modifier.fillMaxSize()) {
            val m = LocalMaterialSpec.current
            val accent = Color(style.accentArgb)
            val s = spec.sample
            when (spec.kind) {
                PreviewKind.CLOCK_DIGITAL -> ClockDigital(s, m, accent)
                PreviewKind.CLOCK_MINIMAL -> ClockMinimal(s, m)
                PreviewKind.CLOCK_ANALOG -> ClockAnalog(m, accent)
                PreviewKind.DATE_CARD -> DateCard(s, m, accent)
                PreviewKind.READOUT -> Readout(s, m)
                PreviewKind.RING -> Ring(s, m, accent)
                PreviewKind.BAR -> Bar(s, m, accent)
                PreviewKind.SPEED_DUAL -> SpeedDual(s)
                PreviewKind.SPEED_MINIMAL -> SpeedMinimal(s)
                PreviewKind.SPEED_DIAL -> SpeedDial(s, m, accent)
                PreviewKind.SIGNAL_BARS -> SignalBars(s, m, accent)
                PreviewKind.ROWS -> Rows(s, m)
                PreviewKind.FORECAST -> Forecast(s, m, accent)
                PreviewKind.CALENDAR_MONTH -> CalendarMonth(m, accent)
            }
        }
    }
}

/** Wallpaper-like backdrop in a recessed stage, so translucent materials read correctly. */
@Composable
fun PreviewStage(
    spec: WidgetSpec,
    style: WidgetStyle,
    modifier: Modifier = Modifier,
    stageAspect: Float = 1.3f,
    inner: Dp = 14.dp
) {
    val radius = 20.dp
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(stageAspect)
            .clip(RoundedCornerShape(radius))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF34436A), Color(0xFF7F5F8C), Color(0xFFD9A06E))
                )
            )
            .skeuInset(radius = radius)
    ) {
        val maxW = maxWidth - inner * 2
        val maxH = maxHeight - inner * 2
        val byHeight = maxH * spec.kind.aspect
        val w = if (byHeight < maxW) byHeight else maxW
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            WidgetPreview(spec = spec, style = style, modifier = Modifier.width(w))
        }
    }
}

// ---------------------------------------------------------------- layouts

@Composable
private fun ClockDigital(s: PreviewSample, m: MaterialSpec, accent: Color) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(s.primary, color = m.ink, fontSize = 52.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Spacer(Modifier.height(6.dp))
        Box(Modifier.width(44.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(accent))
        Spacer(Modifier.height(8.dp))
        Text(s.secondary, color = m.ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Text(s.caption, color = m.inkSecondary, fontSize = 14.sp)
    }
}

@Composable
private fun ClockMinimal(s: PreviewSample, m: MaterialSpec) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(s.primary, color = m.ink, fontSize = 84.sp, fontWeight = FontWeight.Light, fontFamily = FontFamily.SansSerif)
    }
}

private fun polar(center: Offset, radius: Float, degrees: Float): Offset {
    val rad = Math.toRadians(degrees.toDouble() - 90.0)
    return Offset(center.x + radius * cos(rad).toFloat(), center.y + radius * sin(rad).toFloat())
}

@Composable
private fun ClockAnalog(m: MaterialSpec, accent: Color) {
    Canvas(Modifier.fillMaxSize()) {
        val c = center
        val r = size.minDimension / 2f * 0.96f
        drawCircle(color = m.ink.copy(alpha = 0.08f), radius = r, center = c)
        drawCircle(color = m.ink.copy(alpha = 0.30f), radius = r, center = c, style = Stroke(width = 2.dp.toPx()))
        for (i in 0 until 12) {
            val major = i % 3 == 0
            val outer = r - 8.dp.toPx()
            val inner = outer - (if (major) 16.dp.toPx() else 8.dp.toPx())
            drawLine(
                color = m.ink.copy(alpha = if (major) 0.95f else 0.55f),
                start = polar(c, inner, i * 30f),
                end = polar(c, outer, i * 30f),
                strokeWidth = if (major) 4.dp.toPx() else 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        // sample time 10:10:30
        drawLine(m.ink, c, polar(c, r * 0.5f, 10f * 30f + 5f), strokeWidth = 8.dp.toPx(), cap = StrokeCap.Round)
        drawLine(m.ink, c, polar(c, r * 0.75f, 10f * 6f), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
        drawLine(accent, polar(c, -r * 0.15f, 30f * 6f), polar(c, r * 0.82f, 30f * 6f), strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(color = accent, radius = 7.dp.toPx(), center = c)
        drawCircle(color = m.ink, radius = 3.dp.toPx(), center = c)
    }
}

@Composable
private fun DateCard(s: PreviewSample, m: MaterialSpec, accent: Color) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(s.secondary.uppercase(), color = accent, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Text(s.primary, color = m.ink, fontSize = 120.sp, fontWeight = FontWeight.Bold)
        Text(s.caption, color = m.inkSecondary, fontSize = 20.sp)
    }
}

@Composable
private fun Readout(s: PreviewSample, m: MaterialSpec) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text(s.caption.uppercase(), color = m.inkSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.5.sp)
        Text(s.primary, color = m.ink, fontSize = 48.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(s.secondary, color = m.inkSecondary, fontSize = 18.sp, maxLines = 1)
    }
}

@Composable
private fun Ring(s: PreviewSample, m: MaterialSpec, accent: Color) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.size(210.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val stroke = 22.dp.toPx()
                val dia = size.minDimension - stroke
                val topLeft = Offset((size.width - dia) / 2f, (size.height - dia) / 2f)
                val arcSize = Size(dia, dia)
                drawArc(
                    color = m.ink.copy(alpha = 0.16f), startAngle = -90f, sweepAngle = 360f, useCenter = false,
                    topLeft = topLeft, size = arcSize, style = Stroke(width = stroke)
                )
                drawArc(
                    color = accent, startAngle = -90f, sweepAngle = 360f * s.fraction.coerceIn(0f, 1f), useCenter = false,
                    topLeft = topLeft, size = arcSize, style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
            Text(s.primary, color = m.ink, fontSize = 46.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        Text(s.caption, color = m.inkSecondary, fontSize = 16.sp)
    }
}

@Composable
private fun Bar(s: PreviewSample, m: MaterialSpec, accent: Color) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Text(s.primary, color = m.ink, fontSize = 38.sp, fontWeight = FontWeight.Bold)
            Text(s.secondary, color = m.inkSecondary, fontSize = 16.sp)
        }
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(m.ink.copy(alpha = 0.16f))
                .padding(3.dp)
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(s.fraction.coerceIn(0.02f, 1f))
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(s.caption, color = m.inkSecondary, fontSize = 14.sp)
    }
}

@Composable
private fun SpeedDual(s: PreviewSample) {
    val palette = LocalSkeu.current
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LcdDisplay(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                s.rows.forEach { (label, value) ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(label.uppercase(), color = palette.lcdText.copy(alpha = 0.7f), fontSize = 12.sp, letterSpacing = 1.sp)
                        Text(value, color = palette.lcdText, fontSize = 28.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeedMinimal(s: PreviewSample) {
    val palette = LocalSkeu.current
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LcdDisplay(Modifier.fillMaxWidth()) {
            Column {
                Text(s.caption.uppercase(), color = palette.lcdText.copy(alpha = 0.7f), fontSize = 12.sp, letterSpacing = 1.sp)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(s.primary, color = palette.lcdText, fontSize = 52.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Text(s.secondary, color = palette.lcdText, fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun SpeedDial(s: PreviewSample, m: MaterialSpec, accent: Color) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 20.dp.toPx()
            val dia = size.minDimension - stroke - 8.dp.toPx()
            val topLeft = Offset((size.width - dia) / 2f, (size.height - dia) / 2f)
            val arcSize = Size(dia, dia)
            drawArc(
                color = m.ink.copy(alpha = 0.16f), startAngle = 135f, sweepAngle = 270f, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = accent, startAngle = 135f, sweepAngle = 270f * s.fraction.coerceIn(0f, 1f), useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            val c = center
            val tickOuter = dia / 2f - stroke / 2f - 6.dp.toPx()
            for (i in 0..10) {
                val a = 135f + 27f * i
                drawLine(
                    color = m.ink.copy(alpha = if (i % 5 == 0) 0.8f else 0.4f),
                    start = polar(c, tickOuter - (if (i % 5 == 0) 12.dp.toPx() else 7.dp.toPx()), a + 90f),
                    end = polar(c, tickOuter, a + 90f),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(s.primary, color = m.ink, fontSize = 50.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            Text(s.secondary, color = m.inkSecondary, fontSize = 18.sp)
            Text(s.caption.uppercase(), color = m.inkSecondary, fontSize = 11.sp, letterSpacing = 1.5.sp)
        }
    }
}

@Composable
private fun SignalBars(s: PreviewSample, m: MaterialSpec, accent: Color) {
    val active = ceil(s.fraction.coerceIn(0f, 1f) * 5f).toInt()
    Row(
        Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            for (i in 0 until 5) {
                Box(
                    Modifier
                        .width(24.dp)
                        .height((26 + i * 18).dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (i < active) accent else m.ink.copy(alpha = 0.18f))
                )
                Spacer(Modifier.width(7.dp))
            }
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(s.primary, color = m.ink, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Text(s.secondary, color = m.inkSecondary, fontSize = 15.sp)
        }
    }
}

@Composable
private fun Rows(s: PreviewSample, m: MaterialSpec) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        if (s.primary.isNotEmpty()) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(s.primary, color = m.ink, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(12.dp))
                Text(s.secondary, color = m.inkSecondary, fontSize = 16.sp, modifier = Modifier.padding(bottom = 6.dp))
            }
            Spacer(Modifier.height(8.dp))
        }
        s.rows.forEachIndexed { index, (label, value) ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, color = m.inkSecondary, fontSize = 16.sp)
                Text(value, color = m.ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            if (index < s.rows.lastIndex) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(m.ink.copy(alpha = 0.10f)))
            }
        }
    }
}

@Composable
private fun Forecast(s: PreviewSample, m: MaterialSpec, accent: Color) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.WbSunny, contentDescription = null, tint = accent, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(12.dp))
            Text(s.primary, color = m.ink, fontSize = 44.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(12.dp))
            Text(s.secondary, color = m.inkSecondary, fontSize = 16.sp)
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            s.rows.forEach { (day, temp) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(day, color = m.inkSecondary, fontSize = 14.sp)
                    Text(temp, color = m.ink, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun CalendarMonth(m: MaterialSpec, accent: Color) {
    val today = remember { LocalDate.now() }
    val lead = today.withDayOfMonth(1).dayOfWeek.value - 1
    val days = today.lengthOfMonth()
    val rowCount = (lead + days + 6) / 7
    val monthName = today.month.getDisplayName(JavaTextStyle.FULL, Locale.getDefault())
    val letters = listOf("M", "T", "W", "T", "F", "S", "S")
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("$monthName ${today.year}", color = m.ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth()) {
            letters.forEach { l ->
                Text(l, color = m.inkSecondary, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
            }
        }
        repeat(rowCount) { r ->
            Row(Modifier.fillMaxWidth()) {
                repeat(7) { c ->
                    val d = r * 7 + c - lead + 1
                    Box(Modifier.weight(1f).height(32.dp), contentAlignment = Alignment.Center) {
                        if (d in 1..days) {
                            val isToday = d == today.dayOfMonth
                            if (isToday) {
                                Box(Modifier.size(28.dp).clip(CircleShape).background(accent))
                            }
                            Text(
                                d.toString(),
                                color = if (isToday) LocalSkeu.current.onAccent else m.ink,
                                fontSize = 15.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
