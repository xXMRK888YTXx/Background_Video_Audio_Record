package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.DelayStartRecordManager.models

import android.os.Parcel
import android.os.Parcelable
import android.text.ParcelableSpan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DelayRecordTask(
    @SerialName("type") val type:Int,
    @SerialName("time") val time:Long
) {
    companion object {
        const val AUDIO_RECORD_TYPE = 0
        const val VIDEO_RECORD_TYPE = 1
    }
}
