package com.xxmrk888ytxx.recordvideoscreen.models

sealed class CurrentSelectedCameraModel {
    object Front : CurrentSelectedCameraModel()

    object Back :CurrentSelectedCameraModel()
}
