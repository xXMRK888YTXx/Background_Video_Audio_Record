package com.xxmrk888ytxx.fastopenappquicksettingsservice

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.xxmrk888ytxx.coredeps.DepsProvider.getDepsByApplication

class FastOpenAppQuickSettingsService : TileService(), TitleServiceActivityRunner {

    private val fastOpenAppQuickSettingsServiceCallback: FastOpenAppQuickSettingsServiceCallback
            by lazy { getDepsByApplication() }

    override fun onTileAdded() {
        super.onTileAdded()
        updateTileState()
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        val isRecording = try {
            fastOpenAppQuickSettingsServiceCallback.isRecording()
        } catch (_: Exception) {
            false
        }

        if (isRecording) {
            tile.state = Tile.STATE_ACTIVE
            tile.label = "Идёт запись"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = "Нажмите для остановки"
            }
        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.label = "Быстрая запись"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = "Нажмите для старта"
            }
        }
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        unlockAndRun {
            fastOpenAppQuickSettingsServiceCallback.onClicked(this)
            updateTileState()
        }
    }

    override fun <I : Activity> startActivity(
        activityClass: Class<I>,
    ) {
        val intent = Intent(this, activityClass).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 23432, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        startActivityAndCollapse(pendingIntent)
    }
}