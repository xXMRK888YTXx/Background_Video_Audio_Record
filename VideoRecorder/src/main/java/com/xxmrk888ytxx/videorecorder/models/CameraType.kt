package com.xxmrk888ytxx.videorecorder.models

import androidx.camera.core.CameraSelector

sealed class CameraType(internal val cameraSelector: CameraSelector) {

    object Front : CameraType(CameraSelector.DEFAULT_FRONT_CAMERA)

    object Back : CameraType(CameraSelector.DEFAULT_BACK_CAMERA)
}
