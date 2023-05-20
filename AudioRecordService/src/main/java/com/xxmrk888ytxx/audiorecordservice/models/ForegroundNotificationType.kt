package com.xxmrk888ytxx.audiorecordservice.models

sealed class ForegroundNotificationType {

    object ViewRecordStateType : ForegroundNotificationType()

    data class CustomNotification(
        val title:String = "",
        val text:String = ""
    ) : ForegroundNotificationType()
}
