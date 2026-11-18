package com.cb.widgets.widgets.network

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.cb.widgets.MainActivity
import com.cb.widgets.R
import com.cb.widgets.core.data.SettingsRepository
import com.cb.widgets.core.metrics.ReadingQuality
import com.cb.widgets.core.metrics.SpeedCalculator
import com.cb.widgets.core.metrics.SpeedReading
import com.cb.widgets.core.metrics.SpeedUnit
import com.cb.widgets.widgets.common.WidgetUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Optional "Live mode": samples the traffic counters every 1, 2 or 5 seconds and updates the Net Speed widgets.
 *
 * - It only runs because the user switched it on, and Android requires a visible notification for it.
 * - It samples ONLY while the screen is on. When the screen turns off it stops sampling.
 * - It does nothing while no Net Speed widget is on a home screen.
 * - It is not restarted by the system after being killed (START_NOT_STICKY); the app restarts it when opened.
 */
class NetSpeedLiveService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var loop: Job? = null
    private var screenReceiver: BroadcastReceiver? = null

    @Volatile private var unit: SpeedUnit = SpeedUnit.AUTO_BITS
    @Volatile private var intervalSeconds: Int = 2
    @Volatile private var lastReading: SpeedReading? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
        val repository = SettingsRepository(applicationContext)
        scope.launch { repository.speedUnit.collect { unit = it } }
        scope.launch {
            repository.liveIntervalSeconds.collect { seconds ->
                val changed = seconds != intervalSeconds
                intervalSeconds = seconds
                if (changed && loop?.isActive == true) startLoop()
            }
        }
        scope.launch {
            repository.liveModeEnabled.collect { enabled ->
                if (!enabled) stopSelf()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            // Started from the notification's Stop button, so no foreground promotion is needed.
            scope.launch { SettingsRepository(applicationContext).setLiveModeEnabled(false) }
            stopSelf()
            return START_NOT_STICKY
        }

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            buildNotification(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                0
            }
        )
        registerScreenReceiver()
        val interactive = (getSystemService(Context.POWER_SERVICE) as? PowerManager)?.isInteractive ?: true
        if (interactive && loop?.isActive != true) startLoop()
        return START_NOT_STICKY
    }

    private fun startLoop() {
        loop?.cancel()
        val intervalMs = intervalSeconds * 1000L
        loop = scope.launch {
            val app = applicationContext
            var previous = TrafficReader.read()
            var lastKey: String? = null
            var lastSaved = 0L
            while (isActive) {
                delay(intervalMs)
                if (!NetSpeedLiveWidgets.hasInstances(app)) {
                    // Nothing to update: stay quiet and just keep the baseline fresh.
                    previous = TrafficReader.read()
                    continue
                }
                val current = TrafficReader.read()
                val reading = SpeedCalculator.compute(previous, current)
                previous = current
                when (reading.quality) {
                    ReadingQuality.LIVE, ReadingQuality.AVERAGED -> {
                        lastReading = reading
                        lastKey = NetSpeedLiveWidgets.push(app, reading, unit, intervalSeconds, lastKey)
                        val now = System.currentTimeMillis()
                        if (now - lastSaved > SAVE_EVERY_MILLIS) {
                            saveLastReading()
                            lastSaved = now
                        }
                    }
                    ReadingQuality.UNAVAILABLE -> {
                        NetSpeedStore.save(app, StoredReading(0.0, 0.0, ReadingQuality.UNAVAILABLE, System.currentTimeMillis()))
                        WidgetUpdater.refreshAllAsync(app)
                        break
                    }
                    ReadingQuality.INITIALIZING -> Unit
                }
            }
        }
    }

    private fun saveLastReading() {
        val reading = lastReading ?: return
        NetSpeedStore.save(
            applicationContext,
            StoredReading(reading.downBytesPerSecond, reading.upBytesPerSecond, ReadingQuality.LIVE, System.currentTimeMillis())
        )
    }

    private fun registerScreenReceiver() {
        if (screenReceiver != null) return
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        loop?.cancel()
                        // Leave the widgets showing the last real value, labelled with when it was measured.
                        saveLastReading()
                        WidgetUpdater.refreshAllAsync(applicationContext)
                    }
                    Intent.ACTION_SCREEN_ON -> startLoop()
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        screenReceiver = receiver
    }

    override fun onDestroy() {
        loop?.cancel()
        screenReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (e: IllegalArgumentException) {
                Log.w(TAG, "Screen receiver was not registered", e)
            }
        }
        screenReceiver = null
        saveLastReading()
        // Widgets go back to "Measured hh:mm" instead of claiming to be live.
        WidgetUpdater.refreshAllAsync(applicationContext)
        scope.cancel()
        super.onDestroy()
    }

    private fun createChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Live net speed", NotificationManager.IMPORTANCE_LOW)
        channel.description = "Shown while Live mode updates your Net Speed widgets."
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val open = PendingIntent.getActivity(
            this, 20,
            Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            flags
        )
        val stop = PendingIntent.getService(
            this, 21,
            Intent(this, NetSpeedLiveService::class.java).setAction(ACTION_STOP),
            flags
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_arrow_down)
            .setContentTitle("Live net speed is on")
            .setContentText("Updates your Net Speed widgets while the screen is on.")
            .setContentIntent(open)
            .addAction(0, "Stop", stop)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    companion object {
        const val ACTION_STOP = "com.cb.widgets.action.STOP_LIVE"
        private const val CHANNEL_ID = "live_net_speed"
        private const val NOTIFICATION_ID = 4201
        private const val SAVE_EVERY_MILLIS = 5_000L
        private const val TAG = "CbWidgets"
    }
}

/** Starts and stops Live mode. Only call start from the foreground UI: Android forbids it from the background. */
object NetSpeedLive {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun start(context: Context) {
        val app = context.applicationContext
        try {
            ContextCompat.startForegroundService(app, Intent(app, NetSpeedLiveService::class.java))
        } catch (e: Exception) {
            Log.w("CbWidgets", "Could not start Live mode", e)
        }
    }

    fun stop(context: Context) {
        val app = context.applicationContext
        app.stopService(Intent(app, NetSpeedLiveService::class.java))
    }

    /** If Live mode is switched on, make sure it is running (for example after a reboot, when the app opens). */
    fun syncAsync(context: Context) {
        val app = context.applicationContext
        scope.launch {
            if (SettingsRepository(app).currentLiveModeEnabled()) start(app)
        }
    }
}
