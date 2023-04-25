package com.xxmrk888ytxx.backgroundvideovoicerecord.di

import android.content.Context
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.DomainModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.RecordAudioScreenModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules.ScopeModule
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.MainActivity
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        ScopeModule::class,
        DomainModule::class,
        RecordAudioScreenModule::class
    ]
)
@AppScope
interface AppComponent {

    fun inject(mainActivity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context) : AppComponent
    }
}