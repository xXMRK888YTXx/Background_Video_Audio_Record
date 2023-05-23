package com.xxmrk888ytxx.backgroundvideovoicerecord

import android.app.Application
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.DaggerAppComponent
import com.xxmrk888ytxx.coreandroid.DepsProvider.DepsProviderApp
import com.xxmrk888ytxx.coredeps.DepsProvider.DepsProvider
import com.xxmrk888ytxx.coredeps.Exceptions.DepsProviderNotFoundDeps
import kotlin.reflect.KClass

internal class App : Application(),DepsProvider {

    val appComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }


    private val depsMap: Map<KClass<*>, () -> Any> by lazy {
        mapOf(
            appComponent.recordAudioParams.toProvidedDeps(),
            appComponent.recordVideoParams.toProvidedDeps()
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <DEPS : Any> provide(classType: KClass<DEPS>): DEPS {
        val value = depsMap[classType]?.invoke()

        if(value != null)
            return value as DEPS

        throw DepsProviderNotFoundDeps("DepsProvider cant provide ${classType.simpleName}")
    }

    private inline fun <reified T : Any> T.toProvidedDeps() : Pair<KClass<*>,() -> Any> {
        return Pair(T::class) { this }
    }

    private inline fun <reified TYPE : Any, LAZY : dagger.Lazy<TYPE>> LAZY.toProvidedDeps() : Pair<KClass<*>,() -> Any> {
        return Pair(TYPE::class) { this.get() }
    }
}