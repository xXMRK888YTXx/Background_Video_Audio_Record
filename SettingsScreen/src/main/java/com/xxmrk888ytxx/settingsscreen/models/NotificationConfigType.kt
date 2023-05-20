package com.xxmrk888ytxx.settingsscreen.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class NotificationConfigType(
    open val isPauseResumeButtonActive:Boolean,
    open val isStopRecordButtonEnabled:Boolean
) : Parcelable {

    @Parcelize
    data class ViewRecordStateType(
        override val isStopRecordButtonEnabled: Boolean,
        override val isPauseResumeButtonActive: Boolean
    ) : NotificationConfigType(isPauseResumeButtonActive, isStopRecordButtonEnabled),Parcelable

    @Parcelize
    data class CustomNotification(
        override val isStopRecordButtonEnabled: Boolean,
        override val isPauseResumeButtonActive: Boolean,
        val title:String = "",
        val text:String = "",
    ) : NotificationConfigType(isPauseResumeButtonActive, isStopRecordButtonEnabled),
        Parcelable
}
