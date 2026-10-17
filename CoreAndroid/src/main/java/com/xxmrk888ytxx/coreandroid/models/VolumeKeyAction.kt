package com.xxmrk888ytxx.coreandroid.models

import androidx.annotation.StringRes
import com.xxmrk888ytxx.coreandroid.R

enum class VolumeKeyAction(val id: Int, @StringRes val titleRes: Int) {
    START_VIDEO_RECORD(0, R.string.volume_key_action_record_video),
    START_AUDIO_RECORD(1, R.string.volume_key_action_record_audio);

    companion object {
        fun fromId(id: Int): VolumeKeyAction = entries.firstOrNull { it.id == id } ?: START_VIDEO_RECORD
    }
}
