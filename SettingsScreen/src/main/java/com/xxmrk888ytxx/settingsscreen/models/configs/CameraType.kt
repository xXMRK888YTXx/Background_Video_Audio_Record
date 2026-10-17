package com.xxmrk888ytxx.settingsscreen.models.configs

sealed class CameraType(internal val id:Int) {

    object Front : CameraType(0)

    object Back : CameraType(1)

    object Both : CameraType(2)

    companion object {
        private val cameraMap : Map<Int,CameraType>
            get() = mapOf(
                Front.id to Front,
                Back.id to Back,
                Both.id to Both
            )

        fun getCameraTypeById(id:Int) : CameraType = cameraMap.getOrDefault(id,Back)
    }
}
