package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen

import com.xxmrk888ytxx.settingsscreen.contracts.ProvideAppVersionContract
import javax.inject.Inject
import com.xxmrk888ytxx.backgroundvideovoicerecord.BuildConfig

class ProvideAppVersionContractImpl @Inject constructor(

) : ProvideAppVersionContract {

    override val appVersion: String
        get() = BuildConfig.VERSION_NAME
}