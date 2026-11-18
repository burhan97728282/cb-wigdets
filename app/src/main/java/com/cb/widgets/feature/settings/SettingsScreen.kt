package com.cb.widgets.feature.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cb.widgets.BuildConfig
import com.cb.widgets.core.data.ThemeMode
import com.cb.widgets.core.metrics.SpeedUnit
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
    val speedUnit by viewModel.speedUnit.collectAsStateWithLifecycle()
    val liveMode by viewModel.liveMode.collectAsStateWithLifecycle()
    val liveInterval by viewModel.liveInterval.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var askNotifications by remember { mutableStateOf(false) }
    // Whether or not the user allows notifications, Live mode is started after the question was answered.
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        viewModel.setLiveMode(true)
    }
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

        Section("Network speed") {
            Text("Show speed in", style = MaterialTheme.typography.titleMedium, color = palette.text)
            Spacer(Modifier.height(10.dp))
            SkeuSegmentedControl(
                options = listOf("Bits (Mb/s)", "Bytes (MB/s)"),
                selectedIndex = if (speedUnit.isBits) 0 else 1,
                onSelect = { index ->
                    viewModel.setSpeedUnit(if (index == 0) SpeedUnit.AUTO_BITS else SpeedUnit.AUTO_BYTES)
                }
            )
            Spacer(Modifier.height(16.dp))
            Text("Unit", style = MaterialTheme.typography.titleMedium, color = palette.text)
            Spacer(Modifier.height(10.dp))
            val choices = if (speedUnit.isBits) SpeedUnit.bitChoices else SpeedUnit.byteChoices
            SkeuSegmentedControl(
                options = choices.map { it.label },
                selectedIndex = choices.indexOf(speedUnit).coerceAtLeast(0),
                onSelect = { index -> viewModel.setSpeedUnit(choices[index]) }
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Bits are what internet providers quote (Mb/s). Bytes are what download managers show (MB/s). " +
                    "1 byte is 8 bits, so 8 Mb/s is 1 MB/s. Units count in thousands (1 Mb = 1,000,000 bits). " +
                    "Speeds are measured from your phone's traffic counters for all apps, not from an internet speed test.",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary
            )
        }

        Section("Live net speed (optional)") {
            SettingRow(
                title = "Live mode",
                subtitle = "Updates the Net Speed widgets every few seconds, only while the screen is on.",
                trailing = {
                    SkeuToggle(
                        checked = liveMode,
                        onCheckedChange = { on ->
                            val needsPermission = on && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                                PackageManager.PERMISSION_GRANTED
                            when {
                                !on -> viewModel.setLiveMode(false)
                                needsPermission -> askNotifications = true
                                else -> viewModel.setLiveMode(true)
                            }
                        }
                    )
                }
            )
            Spacer(Modifier.height(12.dp))
            Text("Update every", style = MaterialTheme.typography.titleMedium, color = palette.text)
            Spacer(Modifier.height(10.dp))
            val intervals = listOf(1, 2, 5)
            SkeuSegmentedControl(
                options = intervals.map { "$it s" },
                selectedIndex = intervals.indexOf(liveInterval).coerceAtLeast(0),
                onSelect = { viewModel.setLiveInterval(intervals[it]) }
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Android requires a small notification while Live mode runs, and you can stop it from there. " +
                    "It uses some extra battery while the screen is on and stops by itself when the screen turns off. " +
                    "Without Live mode, tap a Net Speed widget to measure. If your phone's battery manager stops Live " +
                    "mode, allow CB Widgets to run in the background in your phone's battery settings.",
                style = MaterialTheme.typography.bodySmall,
                color = palette.textSecondary
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

    if (askNotifications) {
        SkeuDialog(
            title = "Allow a notification?",
            text = "Live mode keeps a small notification on screen while it runs. Android requires this for anything " +
                "that works continuously in the background. If you decline, Live mode still works, and you can stop " +
                "it here or in your phone's running-apps list.",
            confirmLabel = "Continue",
            onConfirm = {
                askNotifications = false
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            },
            onDismiss = { askNotifications = false }
        )
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
