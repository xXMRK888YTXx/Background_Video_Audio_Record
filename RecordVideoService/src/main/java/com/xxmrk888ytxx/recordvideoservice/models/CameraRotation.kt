package com.xxmrk888ytxx.recordvideoservice.models

import android.view.Surface
import com.xxmrk888ytxx.videorecorder.models.CameraRotation as VideoRecorderCameraRotation


/**
 * VideoRecorderCameraRotation = com.xxmrk888ytxx.videorecorder.models.CameraRotation
 */
sealed class CameraRotation(internal val cameraRotation:VideoRecorderCameraRotation) {

    object ROTATION_0 : CameraRotation(VideoRecorderCameraRotation.ROTATION_0)

    object ROTATION_90 : CameraRotation(VideoRecorderCameraRotation.ROTATION_90)

    object ROTATION_180 : CameraRotation(VideoRecorderCameraRotation.ROTATION_180)

    object ROTATION_270 : CameraRotation(VideoRecorderCameraRotation.ROTATION_270)
}