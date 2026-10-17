package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.hardwareTrigger

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.backgroundvideovoicerecord.R
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coredeps.DepsProvider.getDepsByApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProximityTriggerService : Service(), SensorEventListener {

    private val hardwareTriggerManager: HardwareTriggerManager by lazy {
        getDepsByApplication()
    }

    private val sensorManager by lazy {
        getSystemService<SensorManager>()
    }

    private var proximitySensor: Sensor? = null
    private val waveTimestamps = mutableListOf<Long>()
    private var isNear = false
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        try {
            ensureNotificationChannel()
            val notification = createNotification()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    startForeground(
                        PROXIMITY_NOTIF_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                    )
                } else {
                    startForeground(PROXIMITY_NOTIF_ID, notification)
                }
            } else {
                startForeground(PROXIMITY_NOTIF_ID, notification)
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to start foreground service", e)
        }

        try {
            val powerManager = getSystemService<PowerManager>()
            wakeLock = powerManager?.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "BVAS:ProximitySensorWakeLock"
            )?.apply {
                setReferenceCounted(false)
                acquire(6 * 60 * 60 * 1000L) // 6 hours safety timeout
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to acquire wake lock for proximity sensor", e)
        }

        try {
            proximitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
            if (proximitySensor != null) {
                sensorManager?.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
                Log.i(LOG_TAG, "Proximity sensor registered successfully")
            } else {
                Log.w(LOG_TAG, "Device does not have a proximity sensor")
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to register proximity sensor listener", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            sensorManager?.unregisterListener(this)
        } catch (_: Exception) {}

        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (_: Exception) {}
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values.getOrNull(0) ?: return
            val maxRange = event.sensor.maximumRange
            val currentlyNear = distance < maxRange || distance == 0f

            if (currentlyNear && !isNear) {
                val now = System.currentTimeMillis()
                val lastWave = waveTimestamps.lastOrNull() ?: 0L

                // Reject jitter (< 200ms)
                if (now - lastWave > 200L) {
                    waveTimestamps.removeAll { now - it > 2000L }
                    waveTimestamps.add(now)

                    if (waveTimestamps.size >= 2) {
                        waveTimestamps.clear()
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                if (hardwareTriggerManager.isProximityTriggerEnabled.first()) {
                                    Log.i(LOG_TAG, "Double Proximity Wave detected! Toggling background recording.")
                                    hardwareTriggerManager.toggleRecording(applicationContext)
                                }
                            } catch (e: Exception) {
                                Log.e(LOG_TAG, "Failed to trigger recording on proximity sensor", e)
                            }
                        }
                    }
                }
            }
            isNear = currentlyNear
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Служба жестов запуска",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Фоновое отслеживание жестов датчика приближения"
                setShowBadge(false)
            }
            val notificationManager = getSystemService<NotificationManager>()
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return applicationContext.buildNotification(CHANNEL_ID) {
            setContentTitle("Датчик быстрого запуска")
            setContentText("Дважды проведите над экраном для записи")
            setSmallIcon(R.mipmap.ic_launcher)
            setOngoing(true)
        }
    }

    companion object {
        private const val CHANNEL_ID = "ProximityTriggerServiceChannel"
        private const val PROXIMITY_NOTIF_ID = 4582
        private const val LOG_TAG = "ProximityTrigger"
    }
}
