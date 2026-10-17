package com.xxmrk888ytxx.coreandroid.models

import androidx.annotation.StringRes
import com.xxmrk888ytxx.coreandroid.R

enum class VideoSplitLimit(
    val id: Int,
    @StringRes val titleRes: Int,
    val maxDurationMs: Long,
    val maxSizeBytes: Long
) {
    DISABLED(0, R.string.split_limit_disabled, Long.MAX_VALUE, Long.MAX_VALUE),
    LIMIT_4GB_OR_30MIN(1, R.string.split_limit_4gb_or_30min, 30 * 60 * 1000L, 4_000_000_000L),
    LIMIT_15MIN(2, R.string.split_limit_15min, 15 * 60 * 1000L, 4_000_000_000L),
    LIMIT_30MIN(3, R.string.split_limit_30min, 30 * 60 * 1000L, 4_000_000_000L),
    LIMIT_60MIN(4, R.string.split_limit_60min, 60 * 60 * 1000L, 4_000_000_000L);

    companion object {
        fun fromId(id: Int): VideoSplitLimit = values().firstOrNull { it.id == id } ?: DISABLED
    }
}
