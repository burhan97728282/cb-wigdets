package com.cb.widgets.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cb.widgets.core.widgetkit.WidgetMaterial
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val themeKey = stringPreferencesKey("theme_mode")
    private val reduceKey = booleanPreferencesKey("reduce_effects")
    private val materialKey = stringPreferencesKey("default_material")
    private val onboardingKey = booleanPreferencesKey("onboarding_done")
    private val favoritesKey = stringSetPreferencesKey("favorites")
    private val recentsKey = stringPreferencesKey("recents")

    private val data: Flow<Preferences> = context.settingsDataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }

    val themeMode: Flow<ThemeMode> = data.map { prefs ->
        prefs[themeKey]
            ?.let { stored -> ThemeMode.entries.firstOrNull { it.name == stored } }
            ?: ThemeMode.SYSTEM
    }

    val reduceEffects: Flow<Boolean> = data.map { it[reduceKey] ?: false }

    val defaultMaterial: Flow<WidgetMaterial> = data.map { prefs ->
        prefs[materialKey]
            ?.let { stored -> WidgetMaterial.entries.firstOrNull { it.name == stored } }
            ?: WidgetMaterial.MATTE
    }

    val onboardingDone: Flow<Boolean> = data.map { it[onboardingKey] ?: false }

    val favorites: Flow<Set<String>> = data.map { it[favoritesKey] ?: emptySet() }

    val recents: Flow<List<String>> = data.map { prefs ->
        prefs[recentsKey]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { it[themeKey] = mode.name }
    }

    suspend fun setReduceEffects(value: Boolean) {
        context.settingsDataStore.edit { it[reduceKey] = value }
    }

    suspend fun setDefaultMaterial(material: WidgetMaterial) {
        context.settingsDataStore.edit { it[materialKey] = material.name }
    }

    suspend fun setOnboardingDone(value: Boolean) {
        context.settingsDataStore.edit { it[onboardingKey] = value }
    }

    suspend fun toggleFavorite(id: String) {
        context.settingsDataStore.edit { prefs ->
            val current = prefs[favoritesKey] ?: emptySet()
            prefs[favoritesKey] = if (id in current) current - id else current + id
        }
    }

    suspend fun addRecent(id: String) {
        context.settingsDataStore.edit { prefs ->
            val current = prefs[recentsKey]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            val updated = (listOf(id) + current.filter { it != id }).take(8)
            prefs[recentsKey] = updated.joinToString(",")
        }
    }

    /** Clears appearance, favourites and recents. Keeps the onboarding flag. */
    suspend fun resetPreferences() {
        context.settingsDataStore.edit { prefs ->
            prefs.remove(themeKey)
            prefs.remove(reduceKey)
            prefs.remove(materialKey)
            prefs.remove(favoritesKey)
            prefs.remove(recentsKey)
        }
    }

    suspend fun currentDefaultMaterial(): WidgetMaterial = defaultMaterial.first()
}
