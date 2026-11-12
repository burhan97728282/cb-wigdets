package com.cb.widgets.widgets.config

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cb.widgets.core.data.SettingsRepository
import com.cb.widgets.core.data.ThemeMode
import com.cb.widgets.core.widgetkit.WidgetStyle
import com.cb.widgets.widgets.LiveWidgets
import com.cb.widgets.widgets.common.WidgetConfigStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WidgetConfigViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val repository: SettingsRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = repository.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)

    val reduceEffects: StateFlow<Boolean> = repository.reduceEffects
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _style = MutableStateFlow(WidgetStyle())
    val style: StateFlow<WidgetStyle> = _style.asStateFlow()

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var liveWidgetId: String? = null
    private var loaded = false

    /** Loads the widget's saved look (or the defaults) once. Safe to call again after a rotation. */
    fun load(appWidgetId: Int, liveWidgetId: String) {
        if (loaded) return
        loaded = true
        this.appWidgetId = appWidgetId
        this.liveWidgetId = liveWidgetId
        viewModelScope.launch {
            val default = repository.currentDefaultMaterial()
            _style.value = WidgetConfigStore.load(appContext, appWidgetId, default)
        }
    }

    fun update(transform: (WidgetStyle) -> WidgetStyle) {
        _style.update(transform)
    }

    /** Saves the look and redraws this widget on the home screen. */
    suspend fun apply() {
        withContext(Dispatchers.Default) {
            WidgetConfigStore.save(appContext, appWidgetId, _style.value)
            val live = liveWidgetId?.let { LiveWidgets.byId(it) } ?: return@withContext
            live.create().renderWidgets(appContext, AppWidgetManager.getInstance(appContext), intArrayOf(appWidgetId))
        }
    }
}
