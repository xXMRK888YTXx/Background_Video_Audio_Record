package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.xxmrk888ytxx.backgroundvideovoicerecord.R
import com.xxmrk888ytxx.settingsscreen.contracts.OpenActivityContract
import javax.inject.Inject

class OpenActivityContractImpl @Inject constructor(
    private val context: Context
) : OpenActivityContract {


    override fun openSourceCode() {
        //TODO()
    }

    override fun openEmailSender() {
        try {
            val email = context.getString(R.string.email)
            val chooserDescription = context.getString(R.string.Choose_a_client)

            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
            val chooserIntent = Intent.createChooser(emailIntent,chooserDescription).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(chooserIntent)
        }catch (e:Exception) {
            Log.e(LOG_TAG,"Exception when try send ACTION_SENDTO intent ${e.stackTraceToString()}")
        }
    }

    override fun openPrivacyPolicy() {
        //TODO("Not yet implemented")
    }

    override fun openTermsOfUse() {
        //TODO("Not yet implemented")
    }


    private fun openUrl(url:String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(browserIntent)
        }catch (e:Exception) {
            Log.e(LOG_TAG,"Exception when try send ACTION_VIEW intent ${e.stackTraceToString()}")
        }
    }

    companion object {
        private const val LOG_TAG = "OpenActivityContractImpl"
    }
}