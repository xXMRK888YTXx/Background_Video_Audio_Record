package com.xxmrk888ytxx.recordvideoservice.models

import com.xxmrk888ytxx.videorecorder.models.MaxQuality as VideoRecorderMaxQuality


/**
 * VideoRecorderMaxQuality = com.xxmrk888ytxx.videorecorder.models.MaxQuality
 */
sealed class MaxQuality(val videoRecorderMaxQuality:VideoRecorderMaxQuality) {

    object SD : MaxQuality(VideoRecorderMaxQuality.SD)

    object HD : MaxQuality(VideoRecorderMaxQuality.HD)

    object FHD : MaxQuality(VideoRecorderMaxQuality.FHD)
}
