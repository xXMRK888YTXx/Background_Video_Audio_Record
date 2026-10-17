package com.xxmrk888ytxx.recordvideoservice.models

sealed class CameraType(internal val cameraTypeForRecorder: com.xxmrk888ytxx.videorecorder.models.CameraType) {

    object Front : CameraType(com.xxmrk888ytxx.videorecorder.models.CameraType.Front)

    object Back : CameraType(com.xxmrk888ytxx.videorecorder.models.CameraType.Back)

    object Both : CameraType(com.xxmrk888ytxx.videorecorder.models.CameraType.Both)
}