package com.xxmrk888ytxx.backgroundvideovoicerecord

import android.app.Application
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.DaggerAppComponent
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.volumeKey.VolumeKeyTriggerService
import com.xxmrk888ytxx.coreandroid.DepsProvider.DepsProviderApp
import com.xxmrk888ytxx.coredeps.DepsProvider.DepsProvider
import com.xxmrk888ytxx.coredeps.Exceptions.DepsProviderNotFoundDeps
import com.xxmrk888ytxx.worker.contract.WorkerDeps
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

internal class App : Application(), DepsProvider {

    val appComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        initVolumeKeyTriggerService()
    }

    private fun initVolumeKeyTriggerService() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val volumeKeyActionManager = appComponent.volumeKeyActionManager
                if (volumeKeyActionManager.isTriggerEnabled.first() && volumeKeyActionManager.isAllPermissionsGranted()) {
                    if (!VolumeKeyTriggerService.isRunning) {
                        VolumeKeyTriggerService.start(this@App)
                    }
                    VolumeKeyTriggerService.scheduleWatchdog(this@App)
                }
            } catch (_: Exception) {}
        }
    }

    private val depsMap: Map<KClass<*>, () -> Any> by lazy {
        mapOf(
            appComponent.recordAudioParams.toProvidedDeps(),
            appComponent.recordVideoParams.toProvidedDeps(),
            (appComponent as WorkerDeps).toProvidedDeps(),
            appComponent.fastOpenAppQuickSettingsServiceCallback.toProvidedDeps()
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <DEPS : Any> provide(classType: KClass<DEPS>): DEPS {
        val value = depsMap[classType]?.invoke()

        if (value != null)
            return value as DEPS

        throw DepsProviderNotFoundDeps("DepsProvider cant provide ${classType.simpleName}")
    }

    private inline fun <reified T : Any> T.toProvidedDeps(): Pair<KClass<*>, () -> Any> {
        return Pair(T::class) { this }
    }

    private inline fun <reified TYPE : Any, LAZY : dagger.Lazy<TYPE>> LAZY.toProvidedDeps(): Pair<KClass<*>, () -> Any> {
        return Pair(TYPE::class) { this.get() }
    }
}