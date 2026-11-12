package com.cb.widgets.widgets.common

import android.content.Context
import com.cb.widgets.core.widgetkit.WidgetMaterial
import com.cb.widgets.core.widgetkit.WidgetStyle
import com.cb.widgets.core.widgetkit.WidgetTextMode

/**
 * Per-widget appearance, keyed by the Android appWidgetId. Plain SharedPreferences on purpose: widget
 * providers read it synchronously and it never needs a database. A widget that was never customised
 * has no saved entry and follows the default material from Settings.
 */
object WidgetConfigStore {
    private const val PREFS = "cb_widget_config"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(context: Context, appWidgetId: Int, defaultMaterial: WidgetMaterial): WidgetStyle {
        val base = WidgetStyle(material = defaultMaterial)
        val p = prefs(context)
        val k = "$appWidgetId."
        if (!p.getBoolean(k + "saved", false)) return base
        return WidgetStyle(
            material = p.getString(k + "material", null)
                ?.let { name -> WidgetMaterial.entries.firstOrNull { it.name == name } }
                ?: defaultMaterial,
            accentArgb = p.getInt(k + "accent", base.accentArgb),
            opacity = p.getFloat(k + "opacity", base.opacity).coerceIn(0.3f, 1f),
            cornerDp = p.getInt(k + "corner", base.cornerDp).coerceIn(0, 48),
            showBackground = p.getBoolean(k + "background", true),
            textMode = p.getString(k + "text", null)
                ?.let { name -> WidgetTextMode.entries.firstOrNull { it.name == name } }
                ?: WidgetTextMode.AUTO
        )
    }

    fun save(context: Context, appWidgetId: Int, style: WidgetStyle) {
        val k = "$appWidgetId."
        prefs(context).edit()
            .putBoolean(k + "saved", true)
            .putString(k + "material", style.material.name)
            .putInt(k + "accent", style.accentArgb)
            .putFloat(k + "opacity", style.opacity)
            .putInt(k + "corner", style.cornerDp)
            .putBoolean(k + "background", style.showBackground)
            .putString(k + "text", style.textMode.name)
            .apply()
    }

    fun delete(context: Context, appWidgetIds: IntArray) {
        val editor = prefs(context).edit()
        val suffixes = listOf("saved", "material", "accent", "opacity", "corner", "background", "text")
        for (id in appWidgetIds) for (s in suffixes) editor.remove("$id.$s")
        editor.apply()
    }
}
