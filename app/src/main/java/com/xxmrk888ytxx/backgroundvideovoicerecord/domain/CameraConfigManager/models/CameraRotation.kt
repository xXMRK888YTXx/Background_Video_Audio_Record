package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models

sealed class CameraRotation(val id:Int) {

    object ROTATION_0 : CameraRotation(0)

    object ROTATION_90 : CameraRotation(1)

    object ROTATION_180 : CameraRotation(2)

    object ROTATION_270 : CameraRotation(3)
}