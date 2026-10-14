package com.cb.widgets.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cb.widgets.core.data.SettingsRepository
import com.cb.widgets.core.widgetkit.WidgetMaterial
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    val favorites: StateFlow<Set<String>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val recents: StateFlow<List<String>> = repository.recents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val defaultMaterial: StateFlow<WidgetMaterial> = repository.defaultMaterial
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WidgetMaterial.MATTE)

    fun toggleFavorite(id: String) {
        viewModelScope.launch { repository.toggleFavorite(id) }
    }
}
