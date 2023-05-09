package com.xxmrk888ytxx.settingsscreen.models.configs

data class CameraConfig(
    val cameraType: CameraType = CameraType.Back,
    val cameraMaxQuality: CameraMaxQuality = CameraMaxQuality.HD
)