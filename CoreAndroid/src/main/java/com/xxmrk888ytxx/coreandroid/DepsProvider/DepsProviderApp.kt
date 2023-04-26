package com.xxmrk888ytxx.coreandroid.DepsProvider

import android.app.Application
import com.xxmrk888ytxx.coredeps.DepsProvider.DepsProvider
import com.xxmrk888ytxx.coredeps.Exceptions.DepsProviderNotFoundDeps
import kotlin.reflect.KClass

abstract class DepsProviderApp : Application(),DepsProvider {

    protected abstract val depsMap:Map<KClass<*>,() -> Any>

    @Suppress("UNCHECKED_CAST")
    override fun <DEPS : Any> provide(classType: KClass<DEPS>): DEPS {
        val value = depsMap[classType]?.invoke()

        if(value != null)
            return value as DEPS

        throw DepsProviderNotFoundDeps("DepsProvider cant provide ${classType.simpleName}")
    }

    protected inline fun <reified T : Any> T.toProvidedDeps() : Pair<KClass<*>,() -> Any> {
        return Pair(T::class) { this }
    }

    protected inline fun <reified TYPE : Any, LAZY : dagger.Lazy<TYPE>> LAZY.toProvidedDeps() : Pair<KClass<*>,() -> Any> {
        return Pair(TYPE::class) { this.get() }
    }
}