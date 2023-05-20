package com.xxmrk888ytxx.audiorecordservice.models

sealed class ForegroundNotificationType(
    open val isPauseResumeButtonActive:Boolean,
    open val isStopRecordButtonEnabled:Boolean
) {

    data class ViewRecordStateType(
        override val isPauseResumeButtonActive:Boolean,
        override val isStopRecordButtonEnabled:Boolean
    ) : ForegroundNotificationType(isPauseResumeButtonActive, isStopRecordButtonEnabled)

    data class CustomNotification(
        override val isPauseResumeButtonActive:Boolean,
        override val isStopRecordButtonEnabled:Boolean,
        val title:String = "",
        val text:String = ""
    ) : ForegroundNotificationType(isPauseResumeButtonActive, isStopRecordButtonEnabled)
}
