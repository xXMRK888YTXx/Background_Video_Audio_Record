package com.xxmrk888ytxx.fastopenappquicksettingsservice

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.xxmrk888ytxx.coredeps.DepsProvider.getDepsByApplication

class FastOpenAppQuickSettingsService : TileService(), TitleServiceActivityRunner {

    private val fastOpenAppQuickSettingsServiceCallback: FastOpenAppQuickSettingsServiceCallback
            by lazy { getDepsByApplication() }

    override fun onTileAdded() {
        super.onTileAdded()
        qsTile.state = Tile.STATE_INACTIVE
    }

    override fun onClick() {
        super.onClick()
        unlockAndRun {
            fastOpenAppQuickSettingsServiceCallback.onClicked(this)
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