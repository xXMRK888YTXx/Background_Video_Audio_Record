package com.xxmrk888ytxx.backgroundvideovoicerecord.di

import android.content.Context
import com.xxmrk888ytxx.audiorecordservice.RecordAudioParams
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.AudioRecordServiceModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.DomainModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.RecordAudioScreenModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.RecordVideoScreenModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.ScopeModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.StorageScreenModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.MainActivity
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
        RecordVideoScreenModule::class
    ]
)
@AppScope
interface AppComponent {

    fun inject(mainActivity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context) : AppComponent
    }

    val recordAudioParams:Lazy<RecordAudioParams>
}