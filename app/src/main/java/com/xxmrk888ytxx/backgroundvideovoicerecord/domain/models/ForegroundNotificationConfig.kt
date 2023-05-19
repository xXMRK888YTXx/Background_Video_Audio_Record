package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.models

import android.os.Parcelable
import com.xxmrk888ytxx.settingsscreen.models.NotificationConfigType
import kotlinx.serialization.Serializable

@Serializable
sealed class ForegroundNotificationConfig {

    @Serializable
    object ViewRecordStateType : ForegroundNotificationConfig()

    @Serializable
    data class CustomNotification(
        val title:String = "",
        val text:String = ""
    ) : ForegroundNotificationConfig()
}
