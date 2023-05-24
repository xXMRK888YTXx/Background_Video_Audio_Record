package com.xxmrk888ytxx.videorecorder.models

import android.view.Surface

sealed class CameraRotation(internal val rotation: Int) {

    object ROTATION_0 : CameraRotation(Surface.ROTATION_0)

    object ROTATION_90 : CameraRotation(Surface.ROTATION_90)

    object ROTATION_180 : CameraRotation(Surface.ROTATION_180)

    object ROTATION_270 : CameraRotation(Surface.ROTATION_270)
}
