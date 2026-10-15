package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.fastOpenAppQuickSettingsService

import android.content.Context
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordVideoServiceUseCase.IsCanStartRecordVideoServiceUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.appLock.AppLockManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.MainActivity
import com.xxmrk888ytxx.fastopenappquicksettingsservice.FastOpenAppQuickSettingsServiceCallback
import com.xxmrk888ytxx.fastopenappquicksettingsservice.TitleServiceActivityRunner
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class FastOpenAppQuickSettingsServiceCallbackImpl @Inject constructor(
    private val context: Context,
    private val videoRecordServiceManager: VideoRecordServiceManager,
    private val audioRecordServiceManager: AudioRecordServiceManager,
    private val isCanStartRecordVideoServiceUseCase: IsCanStartRecordVideoServiceUseCase,
    private val appLockManager: AppLockManager
) : FastOpenAppQuickSettingsServiceCallback {

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

                val isLockEnabled = appLockManager.isAppLockEnabled.first()
                val isUnlocked = appLockManager.isSessionUnlocked.first()
                if (isLockEnabled && !isUnlocked) {
                    titleServiceActivityRunner.startActivity(MainActivity::class.java)
                    return@runBlocking
                }

                if (isCanStartRecordVideoServiceUseCase.execute()) {
                    videoRecordServiceManager.startRecord()
                } else {
                    titleServiceActivityRunner.startActivity(MainActivity::class.java)
                }
            } catch (_: Exception) {
                titleServiceActivityRunner.startActivity(MainActivity::class.java)
            }
        }
    }
}