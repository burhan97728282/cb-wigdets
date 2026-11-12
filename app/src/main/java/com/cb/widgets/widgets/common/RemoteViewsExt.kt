package com.cb.widgets.widgets.common

import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews

/** Sets a text size in sp. Used so text grows and shrinks with the widget. */
fun RemoteViews.setSp(viewId: Int, sp: Float) =
    setTextViewTextSize(viewId, TypedValue.COMPLEX_UNIT_SP, sp)

fun RemoteViews.show(viewId: Int, visible: Boolean) =
    setViewVisibility(viewId, if (visible) View.VISIBLE else View.GONE)
