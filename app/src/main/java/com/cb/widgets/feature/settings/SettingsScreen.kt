package com.cb.widgets.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cb.widgets.BuildConfig
import com.cb.widgets.core.data.ThemeMode
import com.cb.widgets.core.design.LocalSkeu
import com.cb.widgets.core.design.SettingRow
import com.cb.widgets.core.design.SkeuButton
import com.cb.widgets.core.design.SkeuDialog
import com.cb.widgets.core.design.SkeuDims
import com.cb.widgets.core.design.SkeuSegmentedControl
import com.cb.widgets.core.design.SkeuSurface
import com.cb.widgets.core.design.SkeuToggle
import com.cb.widgets.core.widgetkit.WidgetMaterial

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val palette = LocalSkeu.current
    val theme by viewModel.themeMode.collectAsStateWithLifecycle()
    val reduce by viewModel.reduceEffects.collectAsStateWithLifecycle()
    val material by viewModel.defaultMaterial.collectAsStateWithLifecycle()
    var confirmReset by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = SkeuDims.ScreenPadding)
            .padding(bottom = 24.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = palette.text,
            modifier = Modifier.padding(top = 20.dp, bottom = 16.dp)
        )

        Section("Appearance") {
            Text("Theme", style = MaterialTheme.typography.titleMedium, color = palette.text)
            Spacer(Modifier.height(10.dp))
            val themes = ThemeMode.entries
            SkeuSegmentedControl(
                options = listOf("System", "Light", "Dark"),
                selectedIndex = themes.indexOf(theme),
                onSelect = { viewModel.setThemeMode(themes[it]) }
            )
            Spacer(Modifier.height(16.dp))
            SettingRow(
                title = "Reduce effects",
                subtitle = "Flatter surfaces with solid borders. Easier to read and lighter to draw.",
                trailing = { SkeuToggle(checked = reduce, onCheckedChange = viewModel::setReduceEffects) }
            )
        }

        Section("Widgets") {
            Text("Default widget material", style = MaterialTheme.typography.titleMedium, color = palette.text)
            Spacer(Modifier.height(4.dp))
            Text(
                "Used as the starting look for new widgets.",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary
            )
            Spacer(Modifier.height(10.dp))
            val materials = WidgetMaterial.entries
            SkeuSegmentedControl(
                options = materials.map { it.label },
                selectedIndex = materials.indexOf(material),
                onSelect = { viewModel.setDefaultMaterial(materials[it]) }
            )
        }

        Section("Privacy") {
            Text(
                "CB Widgets reads battery, network, storage and similar information on your phone only to draw " +
                    "your widgets. That information stays on your device. Analytics and crash reports are off " +
                    "unless you choose to turn them on.",
                style = MaterialTheme.typography.bodyMedium,
                color = palette.textSecondary
            )
        }

        Section("About") {
            SettingRow(title = "Version", subtitle = BuildConfig.VERSION_NAME)
            Spacer(Modifier.height(12.dp))
            SkeuButton(
                text = "Replay introduction",
                onClick = viewModel::replayOnboarding,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(14.dp))
            SkeuButton(
                text = "Reset preferences",
                onClick = { confirmReset = true },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (confirmReset) {
        SkeuDialog(
            title = "Reset preferences?",
            text = "Theme, default material, favorites and recently used widgets will return to their defaults.",
            confirmLabel = "Reset",
            onConfirm = {
                viewModel.resetPreferences()
                confirmReset = false
            },
            onDismiss = { confirmReset = false }
        )
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    val palette = LocalSkeu.current
    Text(
        title,
        style = MaterialTheme.typography.titleLarge,
        color = palette.text,
        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
    )
    SkeuSurface(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp)
    ) {
        Column { content() }
    }
}
