package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioForegroundNotificationConfig.AudioForegroundNotificationConfig
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.models.ForegroundNotificationConfig
import com.xxmrk888ytxx.settingsscreen.contracts.ManageAudioNotificationConfigContract
import com.xxmrk888ytxx.settingsscreen.models.NotificationConfigType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ManageAudioNotificationConfigContractImpl @Inject constructor(
    private val audioForegroundNotificationConfig: AudioForegroundNotificationConfig,
) : ManageAudioNotificationConfigContract {

    override val currentConfig: Flow<NotificationConfigType> =
        audioForegroundNotificationConfig.config.map {
            when (it) {
                is ForegroundNotificationConfig.CustomNotification -> NotificationConfigType.CustomNotification(
                    it.isStopRecordButtonEnabled,
                    it.isPauseResumeButtonActive,
                    it.title,
                    it.text
                )

                is ForegroundNotificationConfig.ViewRecordStateType -> NotificationConfigType.ViewRecordStateType(
                    it.isStopRecordButtonEnabled,it.isPauseResumeButtonActive
                )
            }
        }

    override suspend fun setConfig(notificationConfigType: NotificationConfigType) {
        audioForegroundNotificationConfig.setConfig(notificationConfigType.toForegroundConfig())
    }

    private fun NotificationConfigType.toForegroundConfig() : ForegroundNotificationConfig {
        return when(this) {
            is NotificationConfigType.CustomNotification -> ForegroundNotificationConfig.CustomNotification(
                isPauseResumeButtonActive,isStopRecordButtonEnabled,title,text
            )
            is NotificationConfigType.ViewRecordStateType -> ForegroundNotificationConfig.ViewRecordStateType(
                isPauseResumeButtonActive,isStopRecordButtonEnabled
            )
        }
    }
}