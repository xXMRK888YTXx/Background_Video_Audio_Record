package com.xxmrk888ytxx.settingsscreen.models.DialogModels

import com.xxmrk888ytxx.settingsscreen.models.NotificationConfigType

sealed class NotificationConfigurationDialogState {

    object Hide : NotificationConfigurationDialogState()

    data class Showed(
        val initialState:NotificationConfigType,
        val configurationForState: ConfigurationForState
    ) : NotificationConfigurationDialogState()


    enum class ConfigurationForState {
        AUDIO_NOTIFICATION,VIDEO_NOTIFICATION
    }
}
