package com.cb.widgets.feature.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddToHomeScreen
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cb.widgets.core.design.ColorWell
import com.cb.widgets.core.design.EmptyState
import com.cb.widgets.core.design.LocalSkeu
import com.cb.widgets.core.design.PreviewStage
import com.cb.widgets.core.design.SkeuButton
import com.cb.widgets.core.design.SkeuDialog
import com.cb.widgets.core.design.SkeuDims
import com.cb.widgets.core.design.SkeuIconButton
import com.cb.widgets.core.design.SkeuSegmentedControl
import com.cb.widgets.core.design.SkeuSlider
import com.cb.widgets.core.design.SkeuSurface
import com.cb.widgets.core.design.SkeuTag
import com.cb.widgets.core.design.SkeuTopBar
import com.cb.widgets.core.widgetkit.WidgetMaterial
import com.cb.widgets.widgets.WidgetPinner
import kotlin.math.roundToInt

private val AccentChoices = listOf(
    "Amber" to 0xFFE08A1E,
    "Sky" to 0xFF3FA7D6,
    "Mint" to 0xFF4CC38A,
    "Coral" to 0xFFE0566B,
    "Violet" to 0xFF9B7BE0,
    "Ivory" to 0xFFE8E4DA
)

@Composable
fun DetailScreen(
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val palette = LocalSkeu.current
    val context = LocalContext.current
    val spec = viewModel.spec
    val style by viewModel.style.collectAsStateWithLifecycle()
    val favorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    var showHowTo by remember { mutableStateOf(false) }

    if (spec == null) {
        Column(Modifier.fillMaxSize()) {
            SkeuTopBar(title = "Widget", onBack = onBack)
            EmptyState(
                icon = Icons.Outlined.SearchOff,
                title = "Widget not found",
                body = "This widget is no longer available."
            )
        }
        return
    }

    Column(Modifier.fillMaxSize()) {
        SkeuTopBar(
            title = spec.name,
            subtitle = spec.category.label,
            onBack = onBack,
            actions = {
                SkeuIconButton(
                    icon = if (favorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (favorite) "Remove from favorites" else "Add to favorites",
                    onClick = viewModel::toggleFavorite,
                    tint = if (favorite) palette.accent else null
                )
            }
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = SkeuDims.ScreenPadding)
        ) {
            Spacer(Modifier.height(8.dp))
            PreviewStage(spec = spec, style = style, modifier = Modifier.fillMaxWidth(), stageAspect = 1.25f)
            Spacer(Modifier.height(10.dp))
            Text(
                "Preview uses sample values. The real widget shows live data from your phone.",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary
            )

            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                spec.sizes.forEach { SkeuTag(text = "${it.label} ${it.grid}") }
            }
            Spacer(Modifier.height(16.dp))
            Text(spec.description, style = MaterialTheme.typography.bodyLarge, color = palette.text)
            if (!spec.implemented) {
                Spacer(Modifier.height(12.dp))
                SkeuTag(text = "Coming soon", led = true)
            }

            Spacer(Modifier.height(28.dp))
            Text("Customize", style = MaterialTheme.typography.titleLarge, color = palette.text)
            Spacer(Modifier.height(12.dp))
            SkeuSurface(Modifier.fillMaxWidth().padding(10.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)) {
                Column {
                    Text("Material", style = MaterialTheme.typography.titleMedium, color = palette.text)
                    Spacer(Modifier.height(10.dp))
                    val materials = WidgetMaterial.entries
                    SkeuSegmentedControl(
                        options = materials.map { it.label },
                        selectedIndex = materials.indexOf(style.material),
                        onSelect = { viewModel.setMaterial(materials[it]) }
                    )

                    Spacer(Modifier.height(20.dp))
                    Text("Accent color", style = MaterialTheme.typography.titleMedium, color = palette.text)
                    Spacer(Modifier.height(4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        AccentChoices.forEach { (name, value) ->
                            val argb = value.toInt()
                            ColorWell(
                                color = Color(argb),
                                selected = style.accentArgb == argb,
                                onClick = { viewModel.setAccent(argb) },
                                description = name
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Background opacity", style = MaterialTheme.typography.titleMedium, color = palette.text)
                        Text("${(style.opacity * 100).roundToInt()}%", style = MaterialTheme.typography.labelLarge, color = palette.textSecondary)
                    }
                    SkeuSlider(
                        value = style.opacity,
                        onValueChange = viewModel::setOpacity,
                        valueRange = 0.3f..1f
                    )

                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Corner radius", style = MaterialTheme.typography.titleMedium, color = palette.text)
                        Text("${style.cornerDp} dp", style = MaterialTheme.typography.labelLarge, color = palette.textSecondary)
                    }
                    SkeuSlider(
                        value = style.cornerDp.toFloat(),
                        onValueChange = { viewModel.setCorner(it.roundToInt()) },
                        valueRange = 8f..40f
                    )

                    Spacer(Modifier.height(12.dp))
                    SkeuButton(text = "Reset", onClick = viewModel::reset, modifier = Modifier.fillMaxWidth())
                }
            }

            Spacer(Modifier.height(20.dp))
            SkeuButton(
                text = "Add to Home Screen",
                onClick = {
                    // Android always shows its own confirmation. If the launcher cannot, fall back to instructions.
                    val result = WidgetPinner.requestPin(context, spec.id)
                    if (result != WidgetPinner.Result.REQUESTED) showHowTo = true
                },
                primary = true,
                enabled = spec.implemented,
                icon = Icons.Outlined.AddToHomeScreen,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(14.dp))
            SkeuButton(
                text = "How to add it manually",
                onClick = { showHowTo = true },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            Text(
                if (spec.implemented) {
                    "Real widgets use the default material from Settings for now. Per-widget customization is coming in an update."
                } else {
                    "This widget is not released yet. You can already explore and style it."
                },
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary
            )
            Spacer(Modifier.height(24.dp))
            Spacer(Modifier.navigationBarsPadding())
        }
    }

    if (showHowTo) {
        SkeuDialog(
            title = "Add a widget",
            text = "1. Touch and hold an empty area of your home screen.\n" +
                "2. Tap Widgets.\n" +
                "3. Find CB Widgets in the list.\n" +
                "4. Drag the widget you want onto your home screen.",
            confirmLabel = "Got it",
            onConfirm = { showHowTo = false },
            onDismiss = { showHowTo = false },
            dismissLabel = null
        )
    }
}
