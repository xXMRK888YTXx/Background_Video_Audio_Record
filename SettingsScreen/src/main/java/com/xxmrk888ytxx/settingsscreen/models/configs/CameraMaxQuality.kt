package com.xxmrk888ytxx.settingsscreen.models.configs

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed class CameraMaxQuality(val id:Int) {

    object SD : CameraMaxQuality(0)

    object HD : CameraMaxQuality(1)

    object FHD : CameraMaxQuality(2)

    companion object {
        val allCameraQualities : ImmutableList<CameraMaxQuality>
            get() = persistentListOf(SD,HD,FHD)

        fun fromID(id:Int) : CameraMaxQuality {
            return allCameraQualities.firstOrNull { it.id == id } ?: HD
        }
    }
}