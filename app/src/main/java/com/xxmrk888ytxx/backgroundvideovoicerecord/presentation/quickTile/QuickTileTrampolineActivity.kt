package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.quickTile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.xxmrk888ytxx.backgroundvideovoicerecord.App
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordAudioServiceUseCase.IsCanStartRecordAudioServiceUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordVideoServiceUseCase.IsCanStartRecordVideoServiceUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.quickTile.QuickTileActionManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.MainActivity
import com.xxmrk888ytxx.coreandroid.models.QuickTileAction
import com.xxmrk888ytxx.coreandroid.models.QuickTileCameraType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

        val actionId = intent.getIntExtra(EXTRA_ACTION_ID, QuickTileAction.OPEN_APP.id)
        val action = QuickTileAction.fromId(actionId)

        lifecycleScope.launch {
            try {
                when (action) {
                    QuickTileAction.START_VIDEO_RECORD -> {
                        if (isCanStartRecordVideoServiceUseCase.execute()) {
                            val tileCameraType = quickTileActionManager.currentCameraType.first()
                            val cameraTypeOverride = when (tileCameraType) {
                                QuickTileCameraType.AS_IN_SETTINGS -> null
                                QuickTileCameraType.BACK -> CameraType.Back
                                QuickTileCameraType.FRONT -> CameraType.Front
                                QuickTileCameraType.BOTH -> CameraType.Both
                            }
                            videoRecordServiceManager.startRecord(cameraTypeOverride)
                        } else {
                            startActivity(Intent(this@QuickTileTrampolineActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            })
                        }
                    }
                    QuickTileAction.START_AUDIO_RECORD -> {
                        if (isCanStartRecordAudioServiceUseCase.execute()) {
                            audioRecordServiceManager.startRecord()
                        } else {
                            startActivity(Intent(this@QuickTileTrampolineActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            })
                        }
                    }
                    QuickTileAction.OPEN_APP -> {
                        startActivity(Intent(this@QuickTileTrampolineActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        })
                    }
                }
            } catch (_: Exception) {
                startActivity(Intent(this@QuickTileTrampolineActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            } finally {
                finish()
                overridePendingTransition(0, 0)
            }
        }
    }

    companion object {
        const val EXTRA_ACTION_ID = "EXTRA_ACTION_ID"
    }
}
