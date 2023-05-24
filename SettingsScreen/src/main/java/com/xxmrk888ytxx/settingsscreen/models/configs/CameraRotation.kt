package com.xxmrk888ytxx.settingsscreen.models.configs

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

sealed class CameraRotation(internal val id:Int) {

    object ROTATION_0 : CameraRotation(0)

    object ROTATION_90 : CameraRotation(1)

    object ROTATION_180 : CameraRotation(2)

    object ROTATION_270 : CameraRotation(3)

    companion object {
        private val rotationMap : Map<Int,CameraRotation>
            get() = mapOf(
                ROTATION_0.id to ROTATION_0,
                ROTATION_90.id to ROTATION_90,
                ROTATION_180.id to ROTATION_180,
                ROTATION_270.id to ROTATION_270
            )

        val rotationList:ImmutableList<CameraRotation>
            get() = rotationMap.values.toImmutableList()

        fun fromId(id:Int) : CameraRotation {

            return rotationMap[id] ?: CameraRotation.ROTATION_0
        }
    }
}