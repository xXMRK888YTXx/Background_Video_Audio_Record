package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.fastOpenAppQuickSettingsService

import android.content.Context
import android.content.Intent
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.MainActivity
import com.xxmrk888ytxx.fastopenappquicksettingsservice.FastOpenAppQuickSettingsServiceCallback
import com.xxmrk888ytxx.fastopenappquicksettingsservice.TitleServiceActivityRunner
import javax.inject.Inject

class FastOpenAppQuickSettingsServiceCallbackImpl @Inject constructor(
    private val context: Context
) : FastOpenAppQuickSettingsServiceCallback {
    override fun onClicked(titleServiceActivityRunner: TitleServiceActivityRunner) {
        titleServiceActivityRunner.startActivity(MainActivity::class.java)
    }
}