package com.xxmrk888ytxx.backgroundvideovoicerecord.di

import android.content.Context
import com.xxmrk888ytxx.audiorecordservice.RecordAudioParams
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.AdManagerModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.AudioRecordServiceModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.AutoExportToExternalStorageScreenModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.DataModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.DatabaseModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.DomainModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.PreferencesStorageModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.RecordAudioScreenModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.RecordVideoScreenModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.RecordVideoServiceModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.ScopeModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.SettingsScreenModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.StorageScreenModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.UseCaseModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.WorkerDepsModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.MainActivity
import com.xxmrk888ytxx.recordvideoservice.RecordVideoParams
import com.xxmrk888ytxx.worker.contract.NotificationInfoProviderContract
import com.xxmrk888ytxx.worker.contract.SingleFileExportWorkerWork
import com.xxmrk888ytxx.worker.contract.WorkerDeps
import dagger.BindsInstance
import dagger.Component
import dagger.Lazy

@Component(
    modules = [
        ScopeModule::class,
        DomainModule::class,
        RecordAudioScreenModule::class,
        AudioRecordServiceModule::class,
        StorageScreenModule::class,
        RecordVideoScreenModule::class,
        RecordVideoServiceModule::class,
        PreferencesStorageModule::class,
        DatabaseModule::class,
        DataModule::class,
        SettingsScreenModule::class,
        UseCaseModule::class,
        AdManagerModule::class,
        AutoExportToExternalStorageScreenModule::class,
        WorkerDepsModule::class
    ]
)
@AppScope
internal interface AppComponent : WorkerDeps {
    fun inject(mainActivity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context) : AppComponent
    }

    val recordAudioParams:Lazy<RecordAudioParams>

    val recordVideoParams:Lazy<RecordVideoParams>

    override val notificationInfoProviderContract: NotificationInfoProviderContract

    override val singleFileExportWorkerWork: SingleFileExportWorkerWork
}