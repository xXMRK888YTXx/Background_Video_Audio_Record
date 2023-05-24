package com.xxmrk888ytxx.settingsscreen.contracts

import com.xxmrk888ytxx.settingsscreen.models.NotificationConfigType
import kotlinx.coroutines.flow.Flow

interface ManageAudioNotificationConfigContract {

    val currentConfig : Flow<NotificationConfigType>

    suspend fun setConfig(notificationConfigType: NotificationConfigType)
}