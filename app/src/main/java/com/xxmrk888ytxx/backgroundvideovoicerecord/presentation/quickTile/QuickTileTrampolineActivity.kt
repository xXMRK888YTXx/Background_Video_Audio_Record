package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.quickTile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.backgroundvideovoicerecord.App
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordAudioServiceUseCase.IsCanStartRecordAudioServiceUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordVideoServiceUseCase.IsCanStartRecordVideoServiceUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.quickTile.QuickTileActionManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.MainActivity
import com.xxmrk888ytxx.coreandroid.R as CoreR
import com.xxmrk888ytxx.coreandroid.models.QuickTileAction
import com.xxmrk888ytxx.coreandroid.models.QuickTileCameraType
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuickTileTrampolineActivity : ComponentActivity() {

    @Inject
    lateinit var videoRecordServiceManager: VideoRecordServiceManager

    @Inject
    lateinit var audioRecordServiceManager: AudioRecordServiceManager

    @Inject
    lateinit var isCanStartRecordVideoServiceUseCase: IsCanStartRecordVideoServiceUseCase

    @Inject
    lateinit var isCanStartRecordAudioServiceUseCase: IsCanStartRecordAudioServiceUseCase

    @Inject
    lateinit var quickTileActionManager: QuickTileActionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).appComponent.inject(this)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) {
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val action = if (intent.hasExtra(EXTRA_ACTION_ID)) {
                    val actionId = intent.getIntExtra(EXTRA_ACTION_ID, QuickTileAction.OPEN_APP.id)
                    QuickTileAction.fromId(actionId)
                } else {
                    quickTileActionManager.currentAction.first()
                }

                if (action == QuickTileAction.OPEN_APP) {
                    startActivity(Intent(this@QuickTileTrampolineActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    return@launch
                }

                val isVideoRecording = videoRecordServiceManager.currentRecordState.first() !is RecordVideoState.Idle
                val isAudioRecording = audioRecordServiceManager.currentRecordState.first() !is RecordAudioState.Idle

                // Toggle behavior: if any recording is already active, stop it
                if (isVideoRecording || isAudioRecording) {
                    if (isVideoRecording) videoRecordServiceManager.stopRecord()
                    if (isAudioRecording) audioRecordServiceManager.stopRecord()
                    return@launch
                }

                // If not recording, start the requested recording
                when (action) {
                    QuickTileAction.START_VIDEO_RECORD -> {
                        val hasCamera = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        val hasMic = checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

                        if (!hasCamera || !hasMic) {
                            val errorRes = when {
                                !hasCamera && !hasMic -> CoreR.string.camera_and_mic_required_toast
                                !hasCamera -> CoreR.string.camera_required_toast
                                else -> CoreR.string.mic_required_toast
                            }
                            withContext(Dispatchers.Main) {
                                Toast.makeText(applicationContext, errorRes, Toast.LENGTH_LONG).show()
                            }
                            return@launch
                        }

                        if (isCanStartRecordVideoServiceUseCase.execute()) {
                            val cameraType = if (intent.hasExtra(EXTRA_CAMERA_TYPE_ID)) {
                                QuickTileCameraType.fromId(intent.getIntExtra(EXTRA_CAMERA_TYPE_ID, QuickTileCameraType.AS_IN_SETTINGS.id))
                            } else {
                                quickTileActionManager.currentCameraType.first()
                            }
                            val cameraTypeOverride = when (cameraType) {
                                QuickTileCameraType.AS_IN_SETTINGS -> null
                                QuickTileCameraType.BACK -> CameraType.Back
                                QuickTileCameraType.FRONT -> CameraType.Front
                                QuickTileCameraType.BOTH -> CameraType.Both
                            }
                            videoRecordServiceManager.startRecord(cameraTypeOverride)
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(applicationContext, CoreR.string.another_recording_already_active_toast, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    QuickTileAction.START_AUDIO_RECORD -> {
                        val hasMic = checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

                        if (!hasMic) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(applicationContext, CoreR.string.mic_required_toast, Toast.LENGTH_LONG).show()
                            }
                            return@launch
                        }

                        if (isCanStartRecordAudioServiceUseCase.execute()) {
                            audioRecordServiceManager.startRecord()
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(applicationContext, CoreR.string.another_recording_already_active_toast, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    QuickTileAction.OPEN_APP -> Unit
                }
            } catch (e: Exception) {
                Log.e("TrampolineActivity", "Error during record launch or toggle", e)
            } finally {
                finish()
                overridePendingTransition(0, 0)
            }
        }
    }

    companion object {
        const val EXTRA_ACTION_ID = "EXTRA_ACTION_ID"
        const val EXTRA_CAMERA_TYPE_ID = "EXTRA_CAMERA_TYPE_ID"
        const val EXTRA_SOURCE = "EXTRA_SOURCE"
        const val SOURCE_VOLUME_KEY = "SOURCE_VOLUME_KEY"
        const val SOURCE_QUICK_TILE = "SOURCE_QUICK_TILE"
    }
}
