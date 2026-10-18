package com.cb.widgets

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cb.widgets.core.design.CbTheme
import com.cb.widgets.core.design.isDarkFor
import com.cb.widgets.navigation.CbApp
import com.cb.widgets.widgets.common.WidgetUpdater
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        // Battery widgets can be up to 15 minutes old; opening the app brings them up to date.
        WidgetUpdater.refreshAllAsync(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        splash.setKeepOnScreenCondition { viewModel.onboardingDone.value == null }

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val reduce by viewModel.reduceEffects.collectAsStateWithLifecycle()
            val onboardingDone by viewModel.onboardingDone.collectAsStateWithLifecycle()
            val dark = isDarkFor(themeMode)

            // Keeps status/navigation bar icon colours in sync with the in-app theme choice.
            DisposableEffect(dark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark }
                )
                onDispose { }
            }

            CbTheme(themeMode = themeMode, reduceEffects = reduce) {
                onboardingDone?.let { done ->
                    CbApp(
                        onboardingDone = done,
                        onFinishOnboarding = viewModel::completeOnboarding
                    )
                }
            }
        }
    }
}
