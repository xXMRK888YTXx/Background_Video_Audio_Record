package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.models

import android.os.Parcelable
import com.xxmrk888ytxx.settingsscreen.models.NotificationConfigType
import kotlinx.serialization.Serializable

@Serializable
sealed class ForegroundNotificationConfig(
    val isPauseResumeButtonActive:Boolean,
    val isStopRecordButtonEnabled:Boolean
) {

    @Serializable
    data class ViewRecordStateType(
        val _isPauseResumeButtonActive:Boolean,
        val _isStopRecordButtonEnabled:Boolean
    ) : ForegroundNotificationConfig(_isPauseResumeButtonActive, _isStopRecordButtonEnabled)

    @Serializable
    data class CustomNotification(
        val _isPauseResumeButtonActive:Boolean,
        val _isStopRecordButtonEnabled:Boolean,
        val title:String = "",
        val text:String = ""
    ) : ForegroundNotificationConfig(_isPauseResumeButtonActive, _isStopRecordButtonEnabled)
}
