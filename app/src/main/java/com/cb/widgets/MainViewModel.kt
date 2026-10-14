package com.cb.widgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cb.widgets.core.data.SettingsRepository
import com.cb.widgets.core.data.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = repository.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    val reduceEffects: StateFlow<Boolean> = repository.reduceEffects
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /** null while the first value is still loading (keeps the splash screen up). */
    val onboardingDone: StateFlow<Boolean?> = repository.onboardingDone
        .map<Boolean, Boolean?> { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun completeOnboarding() {
        viewModelScope.launch { repository.setOnboardingDone(true) }
    }
}
