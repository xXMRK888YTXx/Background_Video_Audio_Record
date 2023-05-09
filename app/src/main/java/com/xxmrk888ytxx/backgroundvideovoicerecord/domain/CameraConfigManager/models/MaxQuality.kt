package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models

sealed class MaxQuality(val id:Int) {

    object SD : MaxQuality(0)

    object HD : MaxQuality(1)

    object FHD : MaxQuality(2)
}
