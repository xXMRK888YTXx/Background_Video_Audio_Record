package com.xxmrk888ytxx.audiorecordservice.models

sealed class ForegroundNotificationType(
    open val isPauseResumeButtonActive: Boolean,
    open val isStopRecordButtonEnabled: Boolean,
    open val iconId: Int = 0
) {
    data class ViewRecordStateType(
        override val isPauseResumeButtonActive: Boolean,
        override val isStopRecordButtonEnabled: Boolean,
        override val iconId: Int = 0
    ) : ForegroundNotificationType(isPauseResumeButtonActive, isStopRecordButtonEnabled, iconId)

    data class CustomNotification(
        override val isPauseResumeButtonActive: Boolean,
        override val isStopRecordButtonEnabled: Boolean,
        val title: String = "",
        val text: String = "",
        override val iconId: Int = 0
    ) : ForegroundNotificationType(isPauseResumeButtonActive, isStopRecordButtonEnabled, iconId)
}
