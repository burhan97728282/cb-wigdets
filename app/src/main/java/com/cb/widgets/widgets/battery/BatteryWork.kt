package com.cb.widgets.widgets.battery

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.cb.widgets.widgets.LiveWidgets
import com.cb.widgets.widgets.common.WidgetUpdater
import java.util.concurrent.TimeUnit

/** Redraws the battery widgets every 15 minutes (WorkManager's minimum period). */
class BatteryRefreshWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        WidgetUpdater.refresh(applicationContext, LiveWidgets.battery)
        return Result.success()
    }
}

object BatteryWork {
    private const val UNIQUE_NAME = "battery_widget_refresh"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<BatteryRefreshWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(context.applicationContext)
            .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context.applicationContext).cancelUniqueWork(UNIQUE_NAME)
    }
}
