package com.xxmrk888ytxx.backgroundvideovoicerecord

import android.app.Application
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.DaggerAppComponent
import com.xxmrk888ytxx.coreandroid.DepsProvider.DepsProviderApp
import kotlin.reflect.KClass

class App : DepsProviderApp() {

    val appComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }


    override val depsMap: Map<KClass<*>, () -> Any> by lazy {
        mapOf(
            appComponent.recordAudioParams.toProvidedDeps(),
            appComponent.recordVideoParams.toProvidedDeps()
        )
    }
}