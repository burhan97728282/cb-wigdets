package com.cb.widgets.widgets.config

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.cb.widgets.core.design.CbTheme
import com.cb.widgets.core.design.LocalSkeu
import com.cb.widgets.core.widgetkit.WidgetCatalog
import com.cb.widgets.widgets.LiveWidgets
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Opens when a widget is added (and when the user chooses to reconfigure it) so the look can be chosen,
 * including removing the background. Cancelling while adding makes Android discard the new widget.
 */
@AndroidEntryPoint
class WidgetConfigActivity : ComponentActivity() {

    private val viewModel: WidgetConfigViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)

        val appWidgetId = intent?.extras
            ?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val providerClass = AppWidgetManager.getInstance(this).getAppWidgetInfo(appWidgetId)?.provider?.className
        val live = LiveWidgets.all.firstOrNull { it.providerClass.name == providerClass }
        val spec = live?.let { WidgetCatalog.byId(it.id) }
        if (live == null || spec == null) {
            finish()
            return
        }

        viewModel.load(appWidgetId, live.id)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val reduce by viewModel.reduceEffects.collectAsStateWithLifecycle()
            val style by viewModel.style.collectAsStateWithLifecycle()
            CbTheme(themeMode = themeMode, reduceEffects = reduce) {
                val palette = LocalSkeu.current
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(palette.bgTop, palette.bgBottom)))
                ) {
                    WidgetConfigScreen(
                        spec = spec,
                        style = style,
                        onChange = viewModel::update,
                        onApply = { applyAndFinish(appWidgetId) },
                        onCancel = { finish() }
                    )
                }
            }
        }
    }

    private fun applyAndFinish(appWidgetId: Int) {
        lifecycleScope.launch {
            viewModel.apply()
            setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))
            finish()
        }
    }
}
