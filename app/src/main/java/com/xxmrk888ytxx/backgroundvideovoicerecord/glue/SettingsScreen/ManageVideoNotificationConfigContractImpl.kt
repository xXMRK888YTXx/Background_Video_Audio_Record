package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoForegroundNotificationConfig.VideoForegroundNotificationConfig
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.models.ForegroundNotificationConfig
import com.xxmrk888ytxx.settingsscreen.contracts.ManageVideoNotificationConfigContract
import com.xxmrk888ytxx.settingsscreen.models.NotificationConfigType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ManageVideoNotificationConfigContractImpl @Inject constructor(
    private val videoForegroundNotificationConfig: VideoForegroundNotificationConfig,
) : ManageVideoNotificationConfigContract {

    override val currentConfig: Flow<NotificationConfigType> =
        videoForegroundNotificationConfig.config.map {
            when (it) {
                is ForegroundNotificationConfig.CustomNotification -> NotificationConfigType.CustomNotification(
                    isStopRecordButtonEnabled = it.isStopRecordButtonEnabled,
                    isPauseResumeButtonActive = it.isPauseResumeButtonActive,
                    title = it.title,
                    text = it.text,
                    iconId = it.iconId
                )

                is ForegroundNotificationConfig.ViewRecordStateType -> NotificationConfigType.ViewRecordStateType(
                    isStopRecordButtonEnabled = it.isStopRecordButtonEnabled,
                    isPauseResumeButtonActive = it.isPauseResumeButtonActive,
                    iconId = it.iconId
                )
            }
        }

    override suspend fun setConfig(notificationConfigType: NotificationConfigType) {
        videoForegroundNotificationConfig.setConfig(notificationConfigType.toForegroundConfig())
    }

    private fun NotificationConfigType.toForegroundConfig() : ForegroundNotificationConfig {
        return when(this) {
            is NotificationConfigType.CustomNotification -> ForegroundNotificationConfig.CustomNotification(
                _isPauseResumeButtonActive = isPauseResumeButtonActive,
                _isStopRecordButtonEnabled = isStopRecordButtonEnabled,
                title = title,
                text = text,
                _iconId = iconId
            )
            is NotificationConfigType.ViewRecordStateType -> ForegroundNotificationConfig.ViewRecordStateType(
                _isPauseResumeButtonActive = isPauseResumeButtonActive,
                _isStopRecordButtonEnabled = isStopRecordButtonEnabled,
                _iconId = iconId
            )
        }
    }
}