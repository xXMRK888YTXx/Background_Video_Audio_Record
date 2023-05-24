package com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.OpenUrlUseCase

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen.OpenActivityContractImpl
import javax.inject.Inject

class OpenUrlUseCaseImpl @Inject constructor(
    private val context:Context
) : OpenUrlUseCase {

    override fun execute(url: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(browserIntent)
        }catch (e:Exception) {
            Log.e(LOG_TAG,"Exception when try send ACTION_VIEW intent ${e.stackTraceToString()}")
        }
    }

    @SuppressLint("ResourceType")
    override fun execute(resId: Int) {
        execute(context.getString(resId))
    }

    companion object {
        private const val LOG_TAG = "OpenUrlUseCaseImpl"
    }
}