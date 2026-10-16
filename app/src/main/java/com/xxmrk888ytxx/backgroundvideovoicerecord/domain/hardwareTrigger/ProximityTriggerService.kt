package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.hardwareTrigger

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
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

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        ensureNotificationChannel()
        startForeground(PROXIMITY_NOTIF_ID, createNotification())

        proximitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        proximitySensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            val maxRange = event.sensor.maximumRange
            val currentlyNear = distance < maxRange

            if (currentlyNear && !isNear) {
                // Wave event detected
                val now = System.currentTimeMillis()
                waveTimestamps.removeAll { now - it > 1500L }
                waveTimestamps.add(now)

                if (waveTimestamps.size >= 2) {
                    waveTimestamps.clear()
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            if (hardwareTriggerManager.isProximityTriggerEnabled.first()) {
                                Log.i("HardwareTrigger", "Double Proximity Wave detected! Toggling background recording.")
                                hardwareTriggerManager.toggleRecording(applicationContext)
                            }
                        } catch (e: Exception) {
                            Log.e("HardwareTrigger", "Failed to trigger recording on proximity sensor", e)
                        }
                    }
                }
            }
            isNear = currentlyNear
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun ensureNotificationChannel() {
        applicationContext.buildNotificationChannel(
            id = CHANNEL_ID,
            name = "Служба жестов запуска"
        )
    }

    private fun createNotification(): Notification {
        return applicationContext.buildNotification(CHANNEL_ID) {
            setContentTitle("Датчик быстрого запуска активен")
            setContentText("Дважды проведите над датчиком приближения для записи")
            setSmallIcon(android.R.drawable.ic_menu_camera)
            setOngoing(true)
        }
    }

    companion object {
        private const val CHANNEL_ID = "ProximityTriggerServiceChannel"
        private const val PROXIMITY_NOTIF_ID = 4582
    }
}
