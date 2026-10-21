package com.cb.widgets.core.diagnostics

import android.content.Context
import android.os.Build
import com.cb.widgets.BuildConfig
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Debug-build helper: saves the stack trace of an unexpected crash to a private file so it can be
 * shown on the next launch. Nothing is sent anywhere. Release builds do not install it
 * (crash reporting for release is added later, opt-in).
 */
object CrashLog {
    private const val FILE_NAME = "last_crash.txt"
    private const val MAX_CHARS = 20_000

    fun install(context: Context) {
        if (!BuildConfig.DEBUG) return
        val app = context.applicationContext
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val trace = StringWriter()
                throwable.printStackTrace(PrintWriter(trace))
                val text = buildString {
                    append("CB Widgets ").append(BuildConfig.VERSION_NAME).append('\n')
                    append("Android ").append(Build.VERSION.RELEASE)
                    append(" (API ").append(Build.VERSION.SDK_INT).append(")\n")
                    append(Build.MANUFACTURER).append(' ').append(Build.MODEL).append('\n')
                    append("Thread: ").append(thread.name).append("\n\n")
                    append(trace.toString())
                }
                File(app.filesDir, FILE_NAME).writeText(text.take(MAX_CHARS))
            } catch (ignored: Throwable) {
                // Never let the reporter itself hide the original crash.
            }
            previous?.uncaughtException(thread, throwable)
        }
    }

    fun read(context: Context): String? {
        if (!BuildConfig.DEBUG) return null
        val file = File(context.filesDir, FILE_NAME)
        return if (file.exists()) runCatching { file.readText() }.getOrNull() else null
    }

    fun clear(context: Context) {
        runCatching { File(context.filesDir, FILE_NAME).delete() }
    }
}
