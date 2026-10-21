package com.cb.widgets.core.diagnostics

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

/**
 * Plain framework views on purpose: this screen must work even when Compose, Hilt or the theme
 * are the thing that crashed.
 */
object CrashScreen {

    fun build(activity: Activity, report: String, onRetry: () -> Unit): View {
        val density = activity.resources.displayMetrics.density
        fun px(dp: Int) = (dp * density).toInt()

        val root = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(px(16), px(48), px(16), px(16))
        }

        val title = TextView(activity).apply {
            text = "CB Widgets closed unexpectedly"
            setTextColor(Color.BLACK)
            textSize = 20f
            setTypeface(typeface, Typeface.BOLD)
        }
        val hint = TextView(activity).apply {
            text = "Tap \"Copy report\" and send it to the developer. Nothing was uploaded."
            setTextColor(Color.DKGRAY)
            textSize = 14f
            setPadding(0, px(8), 0, px(12))
        }
        val body = TextView(activity).apply {
            text = report
            setTextColor(Color.BLACK)
            typeface = Typeface.MONOSPACE
            textSize = 11f
            setTextIsSelectable(true)
        }
        val scroll = ScrollView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
            addView(body)
        }
        val copy = Button(activity).apply {
            text = "Copy report"
            setOnClickListener {
                val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("CB Widgets crash report", report))
                Toast.makeText(activity, "Report copied", Toast.LENGTH_SHORT).show()
            }
        }
        val retry = Button(activity).apply {
            text = "Clear and try again"
            setOnClickListener { onRetry() }
        }

        root.addView(title)
        root.addView(hint)
        root.addView(scroll)
        root.addView(copy)
        root.addView(retry)
        return root
    }
}
