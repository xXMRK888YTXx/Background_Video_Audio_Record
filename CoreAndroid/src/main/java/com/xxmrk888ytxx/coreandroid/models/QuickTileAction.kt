package com.xxmrk888ytxx.coreandroid.models

import androidx.annotation.StringRes
import com.xxmrk888ytxx.coreandroid.R

enum class QuickTileAction(val id: Int, @StringRes val titleRes: Int) {
    OPEN_APP(0, R.string.quick_tile_action_open_app),
    START_VIDEO_RECORD(1, R.string.quick_tile_action_record_video),
    START_AUDIO_RECORD(2, R.string.quick_tile_action_record_audio);

    companion object {
        fun fromId(id: Int): QuickTileAction = entries.firstOrNull { it.id == id } ?: OPEN_APP
    }
}
