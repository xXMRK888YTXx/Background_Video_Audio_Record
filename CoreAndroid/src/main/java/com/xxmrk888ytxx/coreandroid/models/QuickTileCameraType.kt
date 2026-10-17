package com.xxmrk888ytxx.coreandroid.models

import androidx.annotation.StringRes
import com.xxmrk888ytxx.coreandroid.R

enum class QuickTileCameraType(val id: Int, @StringRes val titleRes: Int) {
    AS_IN_SETTINGS(0, R.string.quick_tile_camera_as_in_settings),
    BACK(1, R.string.quick_tile_camera_back),
    FRONT(2, R.string.quick_tile_camera_front),
    BOTH(3, R.string.quick_tile_camera_both);

    companion object {
        fun fromId(id: Int): QuickTileCameraType = entries.firstOrNull { it.id == id } ?: AS_IN_SETTINGS
    }
}
