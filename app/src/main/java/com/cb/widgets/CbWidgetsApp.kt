package com.cb.widgets

import android.app.Application
import com.cb.widgets.core.diagnostics.CrashLog
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CbWidgetsApp : Application() {
    override fun onCreate() {
        // Installed before Hilt starts, so a crash while the app graph is created is captured too.
        CrashLog.install(this)
        super.onCreate()
    }
}
