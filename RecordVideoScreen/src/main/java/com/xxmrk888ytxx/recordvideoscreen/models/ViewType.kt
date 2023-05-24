package com.xxmrk888ytxx.recordvideoscreen.models

sealed class ViewType {

    object RecordWidget : ViewType()

    object CameraPreview : ViewType()
}