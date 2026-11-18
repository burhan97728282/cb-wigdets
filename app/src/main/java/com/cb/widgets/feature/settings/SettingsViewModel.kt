package com.cb.widgets.feature.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cb.widgets.core.data.SettingsRepository
import com.cb.widgets.core.data.ThemeMode
import com.cb.widgets.core.metrics.SpeedUnit
import com.cb.widgets.core.widgetkit.WidgetMaterial
import com.cb.widgets.widgets.common.WidgetUpdater
import com.cb.widgets.widgets.network.NetSpeedLive
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = repository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.SYSTEM)

    val reduceEffects: StateFlow<Boolean> = repository.reduceEffects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val defaultMaterial: StateFlow<WidgetMaterial> = repository.defaultMaterial
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WidgetMaterial.MATTE)

    val speedUnit: StateFlow<SpeedUnit> = repository.speedUnit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SpeedUnit.AUTO_BITS)

    val liveMode: StateFlow<Boolean> = repository.liveModeEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val liveInterval: StateFlow<Int> = repository.liveIntervalSeconds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 2)

    /** Switches Live mode on or off. Starting it is allowed here because the Settings screen is in the foreground. */
    fun setLiveMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setLiveModeEnabled(enabled)
            if (enabled) NetSpeedLive.start(appContext) else NetSpeedLive.stop(appContext)
        }
    }

    fun setLiveInterval(seconds: Int) {
        viewModelScope.launch { repository.setLiveIntervalSeconds(seconds) }
    }

    fun setSpeedUnit(unit: SpeedUnit) {
        viewModelScope.launch {
            repository.setSpeedUnit(unit)
            // Net Speed widgets re-format their last measurement in the new unit straight away.
            WidgetUpdater.refreshAll(appContext)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun setReduceEffects(value: Boolean) {
        viewModelScope.launch { repository.setReduceEffects(value) }
    }

    fun setDefaultMaterial(material: WidgetMaterial) {
        viewModelScope.launch {
            repository.setDefaultMaterial(material)
            // Placed widgets follow the default material, so redraw them right away.
            WidgetUpdater.refreshAll(appContext)
        }
    }

    fun replayOnboarding() {
        viewModelScope.launch { repository.setOnboardingDone(false) }
    }

    fun resetPreferences() {
        viewModelScope.launch {
            NetSpeedLive.stop(appContext)
            repository.resetPreferences()
        }
    }
}
