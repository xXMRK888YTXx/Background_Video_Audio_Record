package com.xxmrk888ytxx.videorecorder.models

import androidx.camera.video.Quality

sealed class MaxQuality(internal val qualityList: List<Quality>) {

    object SD : MaxQuality(listOf(Quality.SD))

    object HD : MaxQuality(listOf(Quality.HD, Quality.SD))

    object FHD : MaxQuality(listOf(Quality.FHD, Quality.HD, Quality.SD))
}