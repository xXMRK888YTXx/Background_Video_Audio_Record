package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.volumeKey

import android.Manifest
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.widget.Toast
import android.media.session.MediaSessionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.backgroundvideovoicerecord.App
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.volumeKey.VolumeKeyActionManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.quickTile.QuickTileTrampolineActivity
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.coreandroid.models.QuickTileAction
import com.xxmrk888ytxx.coreandroid.models.QuickTileCameraType
import com.xxmrk888ytxx.coreandroid.models.VolumeKeyAction
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
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

    private var isKeyCurrentlyDown = false
    private var waitingForSecondPress = false
    private var lastReleaseTimeMs = 0L
    private var resetJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        (application as App).appComponent.inject(this)

        startForegroundServiceNotification()
        registerVolumeKeyListener()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterVolumeKeyListener()
        serviceScope.cancel()
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
            Log.i(LOG_TAG, "OnVolumeKeyLongPressListener registered successfully")
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
                if (isKeyCurrentlyDown) {
                    // Ignore repeating down events while button is continuously held
                    return
                }
                isKeyCurrentlyDown = true

                if (!waitingForSecondPress) {
                    // First long-press detected
                    vibrateFirstPress()
                    waitingForSecondPress = true
                    resetJob?.cancel()
                } else {
                    // Potential second long-press
                    val now = SystemClock.elapsedRealtime()
                    val diff = now - lastReleaseTimeMs
                    if (diff in 0..2000L) {
                        // Successfully matched double-long-press within 2 seconds!
                        waitingForSecondPress = false
                        resetJob?.cancel()
                        executeTriggerAction()
                    } else {
                        // Window expired, treat as a new first long-press
                        vibrateFirstPress()
                        waitingForSecondPress = true
                        resetJob?.cancel()
                    }
                }
            }

            KeyEvent.ACTION_UP -> {
                isKeyCurrentlyDown = false
                lastReleaseTimeMs = SystemClock.elapsedRealtime()

                if (waitingForSecondPress) {
                    // Start 2-second timeout window from key release
                    resetJob?.cancel()
                    resetJob = serviceScope.launch {
                        delay(2000L)
                        waitingForSecondPress = false
                    }
                }
            }
        }
    }

    private fun executeTriggerAction() {
        serviceScope.launch {
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

        fun start(context: Context) {
            val intent = Intent(context, VolumeKeyTriggerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, VolumeKeyTriggerService::class.java))
        }
    }
}
