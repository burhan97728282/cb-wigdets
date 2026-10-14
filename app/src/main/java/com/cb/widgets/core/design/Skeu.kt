package com.cb.widgets.core.design

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cb.widgets.core.render.RaisedColors
import com.cb.widgets.core.render.WellColors

/** One palette = one coherent set of materials. Light = Ivory/Aluminium, Dark = Graphite/Obsidian. */
@Immutable
class SkeuPalette(
    val isDark: Boolean,
    val bgTop: Color,
    val bgBottom: Color,
    val raised: RaisedColors,
    val primaryRaised: RaisedColors,
    val well: WellColors,
    val lcdWell: WellColors,
    val text: Color,
    val textSecondary: Color,
    val accent: Color,
    val onAccent: Color,
    val lcdText: Color
)

private val PrimaryFace = RaisedColors(
    faceTop = 0xFFF4AD42.toInt(),
    faceBottom = 0xFFD98A12.toInt(),
    highlight = 0xFFFFE6B8.toInt(),
    lowlight = 0xFF8F5200.toInt(),
    shadow = 0xFF5E3A00.toInt()
)

private val LcdWellColors = WellColors(
    top = 0xFF0E1A15.toInt(),
    bottom = 0xFF1C2E26.toInt(),
    innerShadow = 0xFF000000.toInt(),
    rimLight = 0xFFFFFFFF.toInt(),
    border = 0xFF000000.toInt()
)

val LightSkeuPalette = SkeuPalette(
    isDark = false,
    bgTop = Color(0xFFEDE9E1),
    bgBottom = Color(0xFFDAD5CA),
    raised = RaisedColors(
        faceTop = 0xFFFCFBF8.toInt(),
        faceBottom = 0xFFE4E0D6.toInt(),
        highlight = 0xFFFFFFFF.toInt(),
        lowlight = 0xFFB4AE9F.toInt(),
        shadow = 0xFF5E5747.toInt()
    ),
    primaryRaised = PrimaryFace,
    well = WellColors(
        top = 0xFFCFC9BD.toInt(),
        bottom = 0xFFE6E2D8.toInt(),
        innerShadow = 0xFF5E5747.toInt(),
        rimLight = 0xFFFFFFFF.toInt(),
        border = 0xFFB9B3A5.toInt()
    ),
    lcdWell = LcdWellColors,
    text = Color(0xFF1F1D19),
    textSecondary = Color(0xFF5B574E),
    accent = Color(0xFFC27009),
    onAccent = Color(0xFF2A1700),
    lcdText = Color(0xFF8CF5B8)
)

val DarkSkeuPalette = SkeuPalette(
    isDark = true,
    bgTop = Color(0xFF1B1C1F),
    bgBottom = Color(0xFF0F1012),
    raised = RaisedColors(
        faceTop = 0xFF3A3D43.toInt(),
        faceBottom = 0xFF26282C.toInt(),
        highlight = 0xFF6B707A.toInt(),
        lowlight = 0xFF0B0C0D.toInt(),
        shadow = 0xFF000000.toInt()
    ),
    primaryRaised = PrimaryFace,
    well = WellColors(
        top = 0xFF08090A.toInt(),
        bottom = 0xFF1A1C1F.toInt(),
        innerShadow = 0xFF000000.toInt(),
        rimLight = 0xFF4A4E56.toInt(),
        border = 0xFF050505.toInt()
    ),
    lcdWell = LcdWellColors,
    text = Color(0xFFEDEAE3),
    textSecondary = Color(0xFFB4B0A7),
    accent = Color(0xFFF0A940),
    onAccent = Color(0xFF2A1700),
    lcdText = Color(0xFF8CF5B8)
)

val LocalSkeu = staticCompositionLocalOf { LightSkeuPalette }

/** When true, surfaces are drawn flat with solid borders (accessibility + performance). */
val LocalReduceEffects = staticCompositionLocalOf { false }

object SkeuDims {
    val CardRadius: Dp = 24.dp
    val ButtonRadius: Dp = 18.dp
    val ControlRadius: Dp = 16.dp
    val ScreenPadding: Dp = 24.dp
    val DepthLow: Dp = 3.dp
    val DepthMedium: Dp = 6.dp
    val DepthHigh: Dp = 10.dp
}
