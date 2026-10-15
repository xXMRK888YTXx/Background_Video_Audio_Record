package com.xxmrk888ytxx.coreandroid.models

enum class VideoSplitLimit(
    val id: Int,
    val title: String,
    val maxDurationMs: Long,
    val maxSizeBytes: Long
) {
    DISABLED(0, "Без разделения (Один файл)", Long.MAX_VALUE, Long.MAX_VALUE),
    LIMIT_4GB_OR_30MIN(1, "4 ГБ или 30 минут (Рекомендуется)", 30 * 60 * 1000L, 4_000_000_000L),
    LIMIT_15MIN(2, "Каждые 15 минут", 15 * 60 * 1000L, 4_000_000_000L),
    LIMIT_30MIN(3, "Каждые 30 минут", 30 * 60 * 1000L, 4_000_000_000L),
    LIMIT_60MIN(4, "Каждые 60 минут", 60 * 60 * 1000L, 4_000_000_000L);

    companion object {
        fun fromId(id: Int): VideoSplitLimit = values().firstOrNull { it.id == id } ?: DISABLED
    }
}
