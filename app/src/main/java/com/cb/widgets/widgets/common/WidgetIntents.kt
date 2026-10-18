package com.cb.widgets.widgets.common

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock

object WidgetIntents {
    const val ACTION_REFRESH = "com.cb.widgets.action.REFRESH"

    private const val FLAGS = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    /** Opens the phone's clock app (alarms tab). If no app handles it, tapping simply does nothing. */
    fun openClock(context: Context): PendingIntent {
        val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(context, 10, intent, FLAGS)
    }

    /** Opens the phone's default calendar app. */
    fun openCalendar(context: Context): PendingIntent {
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_APP_CALENDAR)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(context, 11, intent, FLAGS)
    }

    /** Asks one provider to redraw itself with fresh data. */
    fun refresh(context: Context, providerClass: Class<*>): PendingIntent {
        val intent = Intent(context, providerClass).setAction(ACTION_REFRESH)
        return PendingIntent.getBroadcast(context, providerClass.name.hashCode(), intent, FLAGS)
    }
}
