package com.xxmrk888ytxx.coreandroid.models

import androidx.annotation.DrawableRes
import com.xxmrk888ytxx.coreandroid.R

enum class NotificationIconType(
    val id: Int,
    @DrawableRes val iconRes: Int,
    val title: String,
    val emoji: String
) {
    VIDEOCAM(0, R.drawable.ic_notif_videocam, "Камера", "📹"),
    MIC(1, R.drawable.ic_notif_mic, "Микрофон", "🎙"),
    DOWNLOAD(2, R.drawable.ic_notif_download, "Загрузка", "⬇️"),
    SETTINGS(3, R.drawable.ic_notif_settings, "Система", "⚙️"),
    BATTERY(4, R.drawable.ic_notif_battery, "Батарея", "🔋"),
    SYNC(5, R.drawable.ic_notif_sync, "Синхронизация", "🔄"),
    SHIELD(6, R.drawable.ic_notif_shield, "Безопасность", "🛡"),
    MUSIC(7, R.drawable.ic_notif_music, "Плеер", "🎵"),
    CLOUD(8, R.drawable.ic_notif_cloud, "Облако", "☁️"),
    CHECK(9, R.drawable.ic_notif_check, "Готово", "✅");

    companion object {
        fun fromId(id: Int): NotificationIconType = values().firstOrNull { it.id == id } ?: VIDEOCAM
    }
}
