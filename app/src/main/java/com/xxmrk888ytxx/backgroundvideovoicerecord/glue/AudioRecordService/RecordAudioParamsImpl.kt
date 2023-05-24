package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.AudioRecordService

import android.content.Context
import com.xxmrk888ytxx.audiorecordservice.RecordAudioParams
import com.xxmrk888ytxx.audiorecordservice.SaveAudioRecordStrategy
import com.xxmrk888ytxx.audiorecordservice.models.ForegroundNotificationType as AudioForegroundNotificationType
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioConfig
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.ApplicationScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioForegroundNotificationConfig.AudioForegroundNotificationConfig
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.models.ForegroundNotificationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject


/**
 * AudioForegroundNotificationType = com.xxmrk888ytxx.audiorecordservice.models.ForegroundNotificationType
 */
class RecordAudioParamsImpl @Inject constructor(
    override val saveAudioRecordStrategy: SaveAudioRecordStrategy,
    private val audioForegroundNotificationConfig: AudioForegroundNotificationConfig,
) : RecordAudioParams {


    override val recordAudioConfig: Flow<RecordAudioConfig> =
        audioForegroundNotificationConfig.config.map {
            val foregroundType = when (it) {
                is ForegroundNotificationConfig.CustomNotification -> AudioForegroundNotificationType.CustomNotification(
                    it.isPauseResumeButtonActive,it.isStopRecordButtonEnabled,it.title,it.text
                )

                is ForegroundNotificationConfig.ViewRecordStateType -> AudioForegroundNotificationType.ViewRecordStateType(
                    it.isPauseResumeButtonActive,it.isStopRecordButtonEnabled
                )
            }

            RecordAudioConfig(foregroundType)
        }


}