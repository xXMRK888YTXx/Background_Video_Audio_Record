package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.volumeKey

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.media.session.MediaSessionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.backgroundvideovoicerecord.App
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.volumeKey.VolumeKeyActionManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.quickTile.QuickTileTrampolineActivity
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.coreandroid.models.QuickTileAction
import com.xxmrk888ytxx.coreandroid.models.VolumeKeyAction
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import javax.inject.Inject

class VolumeKeyTriggerService : Service() {

    @Inject
    lateinit var volumeKeyActionManager: VolumeKeyActionManager

    @Inject
    lateinit var videoRecordServiceManager: VideoRecordServiceManager

    @Inject
    lateinit var audioRecordServiceManager: AudioRecordServiceManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var lastDownTime = 0L
    private var firstPressTimeMs = 0L
    private var lastReleaseTimeMs = 0L
    private var waitingForSecondPress = false
    private var resetJob: Job? = null
    private var heartbeatJob: Job? = null

    private var _wakeLock: PowerManager.WakeLock? = null
    private var isReceiverRegistered = false

    private val systemStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            Log.d(LOG_TAG, "System event: $action, refreshing volume key listener")
            registerVolumeKeyListener()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        isExplicitlyStopped = false
        (application as App).appComponent.inject(this)

        startForegroundServiceNotification()
        registerVolumeKeyListener()
        registerSystemStateReceiver()
        startHeartbeat()
        scheduleWatchdog(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            isExplicitlyStopped = true
            stopSelf()
            return START_NOT_STICKY
        }
        isExplicitlyStopped = false
        isRunning = true
        registerVolumeKeyListener()
        scheduleWatchdog(applicationContext)
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.w(LOG_TAG, "onTaskRemoved triggered")
        if (!isExplicitlyStopped) {
            scheduleImmediateRestart(applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        heartbeatJob?.cancel()
        unregisterReceiverSafely()
        unregisterVolumeKeyListener()
        releaseWakeLock()
        serviceScope.cancel()

        if (!isExplicitlyStopped) {
            Log.w(LOG_TAG, "onDestroy triggered unexpectedly, scheduling immediate restart")
            scheduleImmediateRestart(applicationContext)
        }
    }

    private fun startForegroundServiceNotification() {
        applicationContext.buildNotificationChannel(
            id = NOTIFICATION_CHANNEL_ID,
            name = getString(com.xxmrk888ytxx.coreandroid.R.string.volume_trigger_notification_channel)
        ) {
            importance = NotificationManager.IMPORTANCE_LOW
        }

        val notification = applicationContext.buildNotification(NOTIFICATION_CHANNEL_ID) {
            setContentTitle(getString(com.xxmrk888ytxx.coreandroid.R.string.volume_trigger_notification_title))
            setContentText(getString(com.xxmrk888ytxx.coreandroid.R.string.volume_trigger_notification_text))
            setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
            setOngoing(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
            }
            startForeground(NOTIFICATION_ID, notification, serviceType)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun registerSystemStateReceiver() {
        if (isReceiverRegistered) return
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            addAction(Intent.ACTION_HEADSET_PLUG)
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(systemStateReceiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                registerReceiver(systemStateReceiver, filter)
            }
            isReceiverRegistered = true
            Log.d(LOG_TAG, "System state receiver registered successfully")
        } catch (_: Exception) {
            try {
                registerReceiver(systemStateReceiver, filter)
                isReceiverRegistered = true
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Failed to register system state receiver", e)
            }
        }
    }

    private fun unregisterReceiverSafely() {
        if (!isReceiverRegistered) return
        try {
            unregisterReceiver(systemStateReceiver)
            isReceiverRegistered = false
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to unregister system state receiver", e)
        }
    }

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = serviceScope.launch {
            while (isActive) {
                delay(HEARTBEAT_INTERVAL_MS)
                registerVolumeKeyListener()
            }
        }
    }

    private fun registerVolumeKeyListener() {
        try {
            val mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager ?: return
            val listenerClass = Class.forName("android.media.session.MediaSessionManager\$OnVolumeKeyLongPressListener")

            val invocationHandler = InvocationHandler { _, method: Method, args: Array<out Any>? ->
                if (method.name == "onVolumeKeyLongPress") {
                    val keyEvent = args?.getOrNull(0) as? KeyEvent
                    if (keyEvent != null) {
                        handleKeyEvent(keyEvent)
                    }
                }
                null
            }

            val proxy = Proxy.newProxyInstance(
                listenerClass.classLoader,
                arrayOf(listenerClass),
                invocationHandler
            )

            val setMethod = MediaSessionManager::class.java.getMethod(
                "setOnVolumeKeyLongPressListener",
                listenerClass,
                Handler::class.java
            )
            setMethod.invoke(mediaSessionManager, proxy, Handler(Looper.getMainLooper()))
            Log.d(LOG_TAG, "OnVolumeKeyLongPressListener registered successfully")
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to register volume key listener", e)
        }
    }

    private fun unregisterVolumeKeyListener() {
        try {
            val mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager ?: return
            val listenerClass = Class.forName("android.media.session.MediaSessionManager\$OnVolumeKeyLongPressListener")
            val setMethod = MediaSessionManager::class.java.getMethod(
                "setOnVolumeKeyLongPressListener",
                listenerClass,
                Handler::class.java
            )
            setMethod.invoke(mediaSessionManager, null, null)
            Log.i(LOG_TAG, "OnVolumeKeyLongPressListener unregistered successfully")
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to unregister volume key listener", e)
        }
    }

    private fun handleKeyEvent(event: KeyEvent) {
        if (event.keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && event.keyCode != KeyEvent.KEYCODE_VOLUME_UP) {
            return
        }

        when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                acquireWakeLock(5000L)

                // Ignore repeating down events for the same continuous button hold
                if (event.downTime == lastDownTime && lastDownTime != 0L) {
                    return
                }
                lastDownTime = event.downTime

                val now = SystemClock.elapsedRealtime()

                if (!waitingForSecondPress) {
                    // First long-press detected
                    firstPressTimeMs = now
                    waitingForSecondPress = true
                    vibrateFirstPress()
                    resetJob?.cancel()
                    resetJob = serviceScope.launch {
                        delay(2500L)
                        waitingForSecondPress = false
                        releaseWakeLock()
                    }
                } else {
                    // Potential second long-press
                    val diffFromFirst = now - firstPressTimeMs
                    val diffFromRelease = if (lastReleaseTimeMs > 0L) now - lastReleaseTimeMs else diffFromFirst
                    val isWithinWindow = (diffFromRelease in 0..2500L) || (diffFromFirst in 200..3000L)

                    if (isWithinWindow) {
                        // Successfully matched double-long-press!
                        waitingForSecondPress = false
                        resetJob?.cancel()
                        executeTriggerAction()
                    } else {
                        // Window expired, treat as a new first long-press
                        firstPressTimeMs = now
                        waitingForSecondPress = true
                        vibrateFirstPress()
                        resetJob?.cancel()
                        resetJob = serviceScope.launch {
                            delay(2500L)
                            waitingForSecondPress = false
                            releaseWakeLock()
                        }
                    }
                }
            }

            KeyEvent.ACTION_UP -> {
                lastReleaseTimeMs = SystemClock.elapsedRealtime()

                if (waitingForSecondPress) {
                    // Start timeout window from key release, maintain wake lock during window
                    acquireWakeLock(3000L)
                    resetJob?.cancel()
                    resetJob = serviceScope.launch {
                        delay(2000L)
                        waitingForSecondPress = false
                        releaseWakeLock()
                    }
                } else {
                    releaseWakeLock()
                }
            }
        }
    }

    private fun executeTriggerAction() {
        acquireWakeLock(15000L)
        serviceScope.launch {
            try {
                val isVideoRecording = videoRecordServiceManager.currentRecordState.first() !is RecordVideoState.Idle
                val isAudioRecording = audioRecordServiceManager.currentRecordState.first() !is RecordAudioState.Idle

                if (isVideoRecording || isAudioRecording) {
                    // If already recording, stop active recording
                    vibrateStop()
                    if (isVideoRecording) videoRecordServiceManager.stopRecord()
                    if (isAudioRecording) audioRecordServiceManager.stopRecord()
                } else {
                    val action = volumeKeyActionManager.currentAction.first()
                    val hasCamera = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    val hasMic = checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

                    val isMissingPermission = when (action) {
                        VolumeKeyAction.START_VIDEO_RECORD -> !hasCamera || !hasMic
                        VolumeKeyAction.START_AUDIO_RECORD -> !hasMic
                    }

                    if (isMissingPermission) {
                        vibrateError()
                        val errorRes = when (action) {
                            VolumeKeyAction.START_VIDEO_RECORD -> when {
                                !hasCamera && !hasMic -> com.xxmrk888ytxx.coreandroid.R.string.camera_and_mic_required_toast
                                !hasCamera -> com.xxmrk888ytxx.coreandroid.R.string.camera_required_toast
                                else -> com.xxmrk888ytxx.coreandroid.R.string.mic_required_toast
                            }
                            VolumeKeyAction.START_AUDIO_RECORD -> com.xxmrk888ytxx.coreandroid.R.string.mic_required_toast
                        }
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(applicationContext, errorRes, Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }

                    // Not recording: confirm and start
                    vibrateConfirm()
                    launchRecording()
                }
            } finally {
                delay(1500L)
                releaseWakeLock()
            }
        }
    }

    private fun launchRecording() {
        if (!Settings.canDrawOverlays(this)) {
            Log.w(LOG_TAG, "Cannot launch recording: overlay permission (SYSTEM_ALERT_WINDOW) not granted")
            return
        }

        serviceScope.launch {
            val action = volumeKeyActionManager.currentAction.first()
            val cameraType = volumeKeyActionManager.currentCameraType.first()

            val mappedQuickTileAction = when (action) {
                VolumeKeyAction.START_VIDEO_RECORD -> QuickTileAction.START_VIDEO_RECORD
                VolumeKeyAction.START_AUDIO_RECORD -> QuickTileAction.START_AUDIO_RECORD
            }

            val intent = Intent(this@VolumeKeyTriggerService, QuickTileTrampolineActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
                putExtra(QuickTileTrampolineActivity.EXTRA_SOURCE, QuickTileTrampolineActivity.SOURCE_VOLUME_KEY)
                putExtra(QuickTileTrampolineActivity.EXTRA_ACTION_ID, mappedQuickTileAction.id)
                putExtra(QuickTileTrampolineActivity.EXTRA_CAMERA_TYPE_ID, cameraType.id)
            }

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Failed to start trampoline activity", e)
            }
        }
    }

    private fun getOrCreateWakeLock(): PowerManager.WakeLock {
        return _wakeLock ?: run {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            val lock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "$LOG_TAG:WakeLock").apply {
                setReferenceCounted(false)
            }
            _wakeLock = lock
            lock
        }
    }

    private fun acquireWakeLock(timeoutMs: Long) {
        try {
            getOrCreateWakeLock().acquire(timeoutMs)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to acquire wake lock", e)
        }
    }

    private fun releaseWakeLock() {
        try {
            _wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to release wake lock", e)
        }
    }

    private fun vibrateFirstPress() {
        vibrate(longArrayOf(0, 100))
    }

    private fun vibrateConfirm() {
        vibrate(longArrayOf(0, 120, 80, 180))
    }

    private fun vibrateError() {
        vibrate(longArrayOf(0, 80, 80, 80, 80, 80))
    }

    private fun vibrateStop() {
        vibrate(longArrayOf(0, 350))
    }

    private fun vibrate(pattern: LongArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    vibrator?.vibrate(pattern, -1)
                }
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to vibrate", e)
        }
    }

    companion object {
        private const val LOG_TAG = "VolumeKeyTriggerService"
        private const val NOTIFICATION_ID = 2002
        private const val NOTIFICATION_CHANNEL_ID = "VOLUME_TRIGGER_SERVICE_CHANNEL"
        private const val HEARTBEAT_INTERVAL_MS = 60_000L
        private const val WATCHDOG_INTERVAL_MS = 15 * 60 * 1000L // 15 minutes
        private const val RESTART_REQUEST_CODE = 3001
        private const val WATCHDOG_REQUEST_CODE = 3002

        const val ACTION_STOP_SERVICE = "com.xxmrk888ytxx.action.STOP_VOLUME_KEY_TRIGGER_SERVICE"
        const val ACTION_RESTART_VOLUME_KEY_SERVICE = "com.xxmrk888ytxx.action.RESTART_VOLUME_KEY_SERVICE"
        const val ACTION_WATCHDOG_CHECK = "com.xxmrk888ytxx.action.WATCHDOG_CHECK"

        @Volatile
        var isRunning: Boolean = false
            private set

        @Volatile
        private var isExplicitlyStopped: Boolean = false

        fun start(context: Context) {
            isExplicitlyStopped = false
            val intent = Intent(context, VolumeKeyTriggerService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Failed to start VolumeKeyTriggerService", e)
            }
        }

        fun stop(context: Context) {
            isExplicitlyStopped = true
            cancelWatchdog(context)
            cancelImmediateRestart(context)
            val intent = Intent(context, VolumeKeyTriggerService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            try {
                context.startService(intent)
            } catch (_: Exception) {
                context.stopService(Intent(context, VolumeKeyTriggerService::class.java))
            }
        }

        fun scheduleImmediateRestart(context: Context) {
            if (isExplicitlyStopped) return
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
                val intent = Intent(context, BootReceiver::class.java).apply {
                    action = ACTION_RESTART_VOLUME_KEY_SERVICE
                    setPackage(context.packageName)
                }
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
                val pendingIntent = PendingIntent.getBroadcast(context, RESTART_REQUEST_CODE, intent, flags)
                val triggerAt = SystemClock.elapsedRealtime() + 1500L

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
                        } else {
                            alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
                        }
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
                    }
                } else {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
                }
                Log.i(LOG_TAG, "Immediate restart scheduled")
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Failed to schedule restart", e)
            }
        }

        fun cancelImmediateRestart(context: Context) {
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
                val intent = Intent(context, BootReceiver::class.java).apply {
                    action = ACTION_RESTART_VOLUME_KEY_SERVICE
                    setPackage(context.packageName)
                }
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_NO_CREATE
                }
                val pendingIntent = PendingIntent.getBroadcast(context, RESTART_REQUEST_CODE, intent, flags)
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Failed to cancel immediate restart", e)
            }
        }

        fun scheduleWatchdog(context: Context) {
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
                val intent = Intent(context, BootReceiver::class.java).apply {
                    action = ACTION_WATCHDOG_CHECK
                    setPackage(context.packageName)
                }
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
                val pendingIntent = PendingIntent.getBroadcast(context, WATCHDOG_REQUEST_CODE, intent, flags)
                val triggerAt = SystemClock.elapsedRealtime() + WATCHDOG_INTERVAL_MS

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
                        } else {
                            alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
                        }
                    } else {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
                    }
                } else {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
                }
                Log.d(LOG_TAG, "Watchdog scheduled")
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Failed to schedule watchdog", e)
            }
        }

        fun cancelWatchdog(context: Context) {
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
                val intent = Intent(context, BootReceiver::class.java).apply {
                    action = ACTION_WATCHDOG_CHECK
                    setPackage(context.packageName)
                }
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_NO_CREATE
                }
                val pendingIntent = PendingIntent.getBroadcast(context, WATCHDOG_REQUEST_CODE, intent, flags)
                if (pendingIntent != null) {
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Failed to cancel watchdog", e)
            }
        }
    }
}
