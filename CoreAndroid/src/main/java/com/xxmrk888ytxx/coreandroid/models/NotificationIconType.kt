package com.xxmrk888ytxx.coreandroid.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.xxmrk888ytxx.coreandroid.R

enum class NotificationIconType(
    val id: Int,
    @DrawableRes val iconRes: Int,
    @StringRes val titleRes: Int,
    val emoji: String
) {
    VIDEOCAM(0, R.drawable.ic_notif_videocam, R.string.notif_icon_camera, "📹"),
    MIC(1, R.drawable.ic_notif_mic, R.string.notif_icon_mic, "🎙"),
    DOWNLOAD(2, R.drawable.ic_notif_download, R.string.notif_icon_download, "⬇️"),
    SETTINGS(3, R.drawable.ic_notif_settings, R.string.notif_icon_settings, "⚙️"),
    BATTERY(4, R.drawable.ic_notif_battery, R.string.notif_icon_battery, "🔋"),
    SYNC(5, R.drawable.ic_notif_sync, R.string.notif_icon_sync, "🔄"),
    SHIELD(6, R.drawable.ic_notif_shield, R.string.notif_icon_shield, "🛡"),
    MUSIC(7, R.drawable.ic_notif_music, R.string.notif_icon_music, "🎵"),
    CLOUD(8, R.drawable.ic_notif_cloud, R.string.notif_icon_cloud, "☁️"),
    CHECK(9, R.drawable.ic_notif_check, R.string.notif_icon_check, "✅");

    companion object {
        fun fromId(id: Int): NotificationIconType = values().firstOrNull { it.id == id } ?: VIDEOCAM
    }
}
