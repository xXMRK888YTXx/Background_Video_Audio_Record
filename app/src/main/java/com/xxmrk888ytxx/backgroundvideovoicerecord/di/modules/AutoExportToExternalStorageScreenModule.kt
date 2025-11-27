package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.autoexporttoexternalstoragescreen.contract.ManageExportToExternalStorageSettingContract
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.AutoExportToExternalStorageScreen.ManageExportToExternalStorageSettingContractImpl
import dagger.Binds
import dagger.Module

@Module
interface AutoExportToExternalStorageScreenModule {
    @Binds
    fun bindsManageExportToExternalStorageSettingContract(
        manageExportToExternalStorageSettingContractImpl: ManageExportToExternalStorageSettingContractImpl
    ) :  ManageExportToExternalStorageSettingContract
}