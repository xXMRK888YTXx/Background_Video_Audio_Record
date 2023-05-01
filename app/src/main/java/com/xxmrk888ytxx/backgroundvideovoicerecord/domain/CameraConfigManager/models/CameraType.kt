package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models

sealed class CameraType(val id:Int) {

    object Back : CameraType(0)

    object Front : CameraType(1)
}