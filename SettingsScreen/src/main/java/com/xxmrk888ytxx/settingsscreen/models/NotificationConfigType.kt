package com.xxmrk888ytxx.settingsscreen.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class NotificationConfigType(
    open val isPauseResumeButtonActive: Boolean,
    open val isStopRecordButtonEnabled: Boolean,
    open val iconId: Int = 0
) : Parcelable {

    @Parcelize
    data class ViewRecordStateType(
        override val isStopRecordButtonEnabled: Boolean,
        override val isPauseResumeButtonActive: Boolean,
        override val iconId: Int = 0
    ) : NotificationConfigType(isPauseResumeButtonActive, isStopRecordButtonEnabled, iconId), Parcelable

    @Parcelize
    data class CustomNotification(
        override val isStopRecordButtonEnabled: Boolean,
        override val isPauseResumeButtonActive: Boolean,
        val title: String = "",
        val text: String = "",
        override val iconId: Int = 0,
    ) : NotificationConfigType(isPauseResumeButtonActive, isStopRecordButtonEnabled, iconId),
        Parcelable
}
