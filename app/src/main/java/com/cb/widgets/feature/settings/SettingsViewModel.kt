package com.cb.widgets.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cb.widgets.core.data.SettingsRepository
import com.cb.widgets.core.data.ThemeMode
import com.cb.widgets.core.widgetkit.WidgetMaterial
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = repository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.SYSTEM)

    val reduceEffects: StateFlow<Boolean> = repository.reduceEffects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val defaultMaterial: StateFlow<WidgetMaterial> = repository.defaultMaterial
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WidgetMaterial.MATTE)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun setReduceEffects(value: Boolean) {
        viewModelScope.launch { repository.setReduceEffects(value) }
    }

    fun setDefaultMaterial(material: WidgetMaterial) {
        viewModelScope.launch { repository.setDefaultMaterial(material) }
    }

    fun replayOnboarding() {
        viewModelScope.launch { repository.setOnboardingDone(false) }
    }

    fun resetPreferences() {
        viewModelScope.launch { repository.resetPreferences() }
    }
}
