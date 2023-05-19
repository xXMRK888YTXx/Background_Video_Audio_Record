package com.xxmrk888ytxx.settingsscreen.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class NotificationConfigType : Parcelable {

    @Parcelize
    object ViewRecordStateType : NotificationConfigType(),Parcelable

    @Parcelize
    data class CustomNotification(val title:String = "",val text:String = "") : NotificationConfigType(),
        Parcelable
}
