package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.xxmrk888ytxx.backgroundvideovoicerecord.R
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.OpenUrlUseCase.OpenUrlUseCase
import com.xxmrk888ytxx.settingsscreen.contracts.OpenActivityContract
import javax.inject.Inject

class OpenActivityContractImpl @Inject constructor(
    private val context: Context,
    private val openUrlUseCase: OpenUrlUseCase
) : OpenActivityContract {


    override fun openSourceCode() {
        openUrlUseCase.execute(context.getString(R.string.source_code_url))
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
        openUrlUseCase.execute(context.getString(R.string.privacy_policy_url))
    }

    override fun openTermsOfUse() {
        openUrlUseCase.execute(context.getString(R.string.terms_of_use_url))
    }

    companion object {
        private const val LOG_TAG = "OpenActivityContractImpl"
    }
}