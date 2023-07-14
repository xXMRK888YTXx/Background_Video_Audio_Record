package com.xxmrk888ytxx.settingsscreen.models.configs

import androidx.annotation.Keep
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed class CameraMaxQuality(val id:Int,val name:String) {

    object SD : CameraMaxQuality(0,"SD")

    object HD : CameraMaxQuality(1,"HD")

    object FHD : CameraMaxQuality(2,"FHD")

    companion object {
        val allCameraQualities : ImmutableList<CameraMaxQuality>
            get() = persistentListOf(SD,HD,FHD)

        fun fromID(id:Int) : CameraMaxQuality {
            return allCameraQualities.firstOrNull { it.id == id } ?: HD
        }
    }
}