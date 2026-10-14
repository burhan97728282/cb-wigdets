package com.cb.widgets.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cb.widgets.core.data.SettingsRepository
import com.cb.widgets.core.widgetkit.WidgetCatalog
import com.cb.widgets.core.widgetkit.WidgetMaterial
import com.cb.widgets.core.widgetkit.WidgetSpec
import com.cb.widgets.core.widgetkit.WidgetStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SettingsRepository
) : ViewModel() {

    private val widgetId: String = savedStateHandle.get<String>("id").orEmpty()
    val spec: WidgetSpec? = WidgetCatalog.byId(widgetId)

    private val _style = MutableStateFlow(WidgetStyle())
    val style: StateFlow<WidgetStyle> = _style.asStateFlow()

    val isFavorite: StateFlow<Boolean> = repository.favorites
        .map { widgetId in it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private var baseMaterial: WidgetMaterial = WidgetMaterial.MATTE

    init {
        val current = spec
        if (current != null) {
            viewModelScope.launch {
                repository.addRecent(current.id)
                baseMaterial = current.defaultMaterial ?: repository.currentDefaultMaterial()
                _style.update { it.copy(material = baseMaterial) }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch { repository.toggleFavorite(widgetId) }
    }

    fun setMaterial(material: WidgetMaterial) = _style.update { it.copy(material = material) }
    fun setAccent(argb: Int) = _style.update { it.copy(accentArgb = argb) }
    fun setOpacity(value: Float) = _style.update { it.copy(opacity = value.coerceIn(0.3f, 1f)) }
    fun setCorner(dp: Int) = _style.update { it.copy(cornerDp = dp.coerceIn(8, 40)) }
    fun reset() = _style.update { WidgetStyle(material = baseMaterial) }
}
