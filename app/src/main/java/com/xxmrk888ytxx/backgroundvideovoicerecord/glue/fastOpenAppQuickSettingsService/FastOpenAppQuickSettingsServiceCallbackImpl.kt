package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.fastOpenAppQuickSettingsService

import android.content.Context
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.quickTile.QuickTileActionManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.MainActivity
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.quickTile.QuickTileTrampolineActivity
import com.xxmrk888ytxx.coreandroid.models.QuickTileAction
import com.xxmrk888ytxx.fastopenappquicksettingsservice.FastOpenAppQuickSettingsServiceCallback
import com.xxmrk888ytxx.fastopenappquicksettingsservice.TitleServiceActivityRunner
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class FastOpenAppQuickSettingsServiceCallbackImpl @Inject constructor(
    private val context: Context,
    private val videoRecordServiceManager: VideoRecordServiceManager,
    private val audioRecordServiceManager: AudioRecordServiceManager,
    private val quickTileActionManager: QuickTileActionManager
) : FastOpenAppQuickSettingsServiceCallback {

    override val isRecordingFlow: Flow<Boolean> = combine(
        videoRecordServiceManager.currentRecordState,
        audioRecordServiceManager.currentRecordState
    ) { videoState, audioState ->
        videoState !is RecordVideoState.Idle || audioState !is RecordAudioState.Idle
    }

    override fun isRecording(): Boolean = runBlocking {
        try {
            val videoState = videoRecordServiceManager.currentRecordState.first()
            if (videoState !is RecordVideoState.Idle) return@runBlocking true

            val audioState = audioRecordServiceManager.currentRecordState.first()
            if (audioState !is RecordAudioState.Idle) return@runBlocking true

            false
        } catch (_: Exception) {
            false
        }
    }

    override fun getActionTitleRes(): Int = runBlocking {
        try {
            quickTileActionManager.currentAction.first().titleRes
        } catch (_: Exception) {
            com.xxmrk888ytxx.coreandroid.R.string.quick_tile_action_open_app
        }
    }

    override fun onClicked(titleServiceActivityRunner: TitleServiceActivityRunner) {
        runBlocking {
            try {
                val videoState = videoRecordServiceManager.currentRecordState.first()
                if (videoState !is RecordVideoState.Idle) {
                    videoRecordServiceManager.stopRecord()
                    return@runBlocking
                }

                val audioState = audioRecordServiceManager.currentRecordState.first()
                if (audioState !is RecordAudioState.Idle) {
                    audioRecordServiceManager.stopRecord()
                    return@runBlocking
                }

                val action = quickTileActionManager.currentAction.first()
                when (action) {
                    QuickTileAction.OPEN_APP -> {
                        titleServiceActivityRunner.startActivity(MainActivity::class.java)
                    }
                    QuickTileAction.START_VIDEO_RECORD -> {
                        titleServiceActivityRunner.startActivity(QuickTileTrampolineActivity::class.java) { intent ->
                            intent.putExtra(QuickTileTrampolineActivity.EXTRA_ACTION_ID, QuickTileAction.START_VIDEO_RECORD.id)
                        }
                    }
                    QuickTileAction.START_AUDIO_RECORD -> {
                        titleServiceActivityRunner.startActivity(QuickTileTrampolineActivity::class.java) { intent ->
                            intent.putExtra(QuickTileTrampolineActivity.EXTRA_ACTION_ID, QuickTileAction.START_AUDIO_RECORD.id)
                        }
                    }
                }
            } catch (_: Exception) {
                titleServiceActivityRunner.startActivity(MainActivity::class.java)
            }
        }
    }
}