package com.cb.widgets.widgets.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cb.widgets.core.design.ColorWell
import com.cb.widgets.core.design.LocalSkeu
import com.cb.widgets.core.design.PreviewStage
import com.cb.widgets.core.design.SkeuButton
import com.cb.widgets.core.design.SkeuDims
import com.cb.widgets.core.design.SkeuSegmentedControl
import com.cb.widgets.core.design.SkeuSlider
import com.cb.widgets.core.design.SkeuSurface
import com.cb.widgets.core.design.SkeuTopBar
import com.cb.widgets.core.widgetkit.WidgetMaterial
import com.cb.widgets.core.widgetkit.WidgetSpec
import com.cb.widgets.core.widgetkit.WidgetStyle
import com.cb.widgets.core.widgetkit.WidgetTextMode
import kotlin.math.roundToInt

private val Accents = listOf(
    "Amber" to 0xFFE08A1E,
    "Sky" to 0xFF3FA7D6,
    "Mint" to 0xFF4CC38A,
    "Coral" to 0xFFE0566B,
    "Violet" to 0xFF9B7BE0,
    "Ivory" to 0xFFE8E4DA
)

private const val BG_SOLID = 0
private const val BG_TRANSLUCENT = 1
private const val BG_NONE = 2

@Composable
fun WidgetConfigScreen(
    spec: WidgetSpec,
    style: WidgetStyle,
    onChange: ((WidgetStyle) -> WidgetStyle) -> Unit,
    onApply: () -> Unit,
    onCancel: () -> Unit
) {
    val palette = LocalSkeu.current
    val backgroundMode = when {
        !style.showBackground -> BG_NONE
        style.opacity < 0.99f -> BG_TRANSLUCENT
        else -> BG_SOLID
    }

    Column(Modifier.fillMaxSize()) {
        SkeuTopBar(title = "Customize widget", subtitle = spec.name, onBack = onCancel)

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SkeuDims.ScreenPadding)
        ) {
            Spacer(Modifier.height(8.dp))
            PreviewStage(spec = spec, style = style, modifier = Modifier.fillMaxWidth(), stageAspect = 1.4f)
            Spacer(Modifier.height(10.dp))
            Text(
                "The preview shows sample values. The widget on your home screen shows live data. " +
                    "You can resize the widget on the home screen to any size.",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary
            )
            Spacer(Modifier.height(20.dp))

            SkeuSurface(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                contentPadding = PaddingValues(18.dp)
            ) {
                Column {
                    Label("Background")
                    Spacer(Modifier.height(10.dp))
                    SkeuSegmentedControl(
                        options = listOf("Solid", "Translucent", "None"),
                        selectedIndex = backgroundMode,
                        onSelect = { index ->
                            onChange { current ->
                                when (index) {
                                    BG_SOLID -> current.copy(showBackground = true, opacity = 1f)
                                    BG_TRANSLUCENT -> current.copy(
                                        showBackground = true,
                                        opacity = if (current.opacity >= 0.99f) 0.55f else current.opacity
                                    )
                                    else -> current.copy(showBackground = false)
                                }
                            }
                        }
                    )
                    if (backgroundMode == BG_NONE) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "No background: the clock or gauge sits directly on your wallpaper. " +
                                "Choose Light or Dark text so it stays readable.",
                            style = MaterialTheme.typography.bodySmall,
                            color = palette.textSecondary
                        )
                    }

                    if (backgroundMode == BG_TRANSLUCENT) {
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Label("Background opacity")
                            Text(
                                "${(style.opacity * 100).roundToInt()}%",
                                style = MaterialTheme.typography.labelLarge,
                                color = palette.textSecondary
                            )
                        }
                        SkeuSlider(
                            value = style.opacity,
                            onValueChange = { v -> onChange { it.copy(opacity = v.coerceIn(0.3f, 0.95f)) } },
                            valueRange = 0.3f..0.95f
                        )
                    }

                    if (style.showBackground) {
                        Spacer(Modifier.height(20.dp))
                        Label("Material")
                        Spacer(Modifier.height(10.dp))
                        val materials = WidgetMaterial.entries
                        SkeuSegmentedControl(
                            options = materials.map { it.label },
                            selectedIndex = materials.indexOf(style.material),
                            onSelect = { i -> onChange { it.copy(material = materials[i]) } }
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    Label("Text color")
                    Spacer(Modifier.height(10.dp))
                    val modes = WidgetTextMode.entries
                    SkeuSegmentedControl(
                        options = modes.map { it.label },
                        selectedIndex = modes.indexOf(style.textMode),
                        onSelect = { i -> onChange { it.copy(textMode = modes[i]) } }
                    )

                    Spacer(Modifier.height(20.dp))
                    Label("Accent color")
                    Spacer(Modifier.height(4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Accents.forEach { (name, value) ->
                            val argb = value.toInt()
                            ColorWell(
                                color = Color(argb),
                                selected = style.accentArgb == argb,
                                onClick = { onChange { it.copy(accentArgb = argb) } },
                                description = name
                            )
                        }
                    }

                    if (style.showBackground) {
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Label("Corner radius")
                            Text(
                                "${style.cornerDp} dp",
                                style = MaterialTheme.typography.labelLarge,
                                color = palette.textSecondary
                            )
                        }
                        SkeuSlider(
                            value = style.cornerDp.toFloat(),
                            onValueChange = { v -> onChange { it.copy(cornerDp = v.roundToInt().coerceIn(0, 40)) } },
                            valueRange = 0f..40f
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            SkeuButton(text = "Apply", onClick = onApply, primary = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(14.dp))
            SkeuButton(text = "Cancel", onClick = onCancel, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(24.dp))
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = LocalSkeu.current.text)
}
