package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.models

import kotlinx.serialization.Serializable

@Serializable
sealed class ForegroundNotificationConfig(
    val isPauseResumeButtonActive: Boolean,
    val isStopRecordButtonEnabled: Boolean,
    val iconId: Int = 0
) {

    @Serializable
    data class ViewRecordStateType(
        val _isPauseResumeButtonActive: Boolean,
        val _isStopRecordButtonEnabled: Boolean,
        val _iconId: Int = 0
    ) : ForegroundNotificationConfig(_isPauseResumeButtonActive, _isStopRecordButtonEnabled, _iconId)

    @Serializable
    data class CustomNotification(
        val _isPauseResumeButtonActive: Boolean,
        val _isStopRecordButtonEnabled: Boolean,
        val title: String = "",
        val text: String = "",
        val _iconId: Int = 0
    ) : ForegroundNotificationConfig(_isPauseResumeButtonActive, _isStopRecordButtonEnabled, _iconId)
}
