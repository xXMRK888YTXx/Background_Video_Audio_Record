package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.di.AppScope
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.ApplicationScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.RecordStateObserverScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.ServiceManagedScopeQualifier
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob

@Module
class ScopeModule {

    @Provides
    @AppScope
    @RecordStateObserverScopeQualifier
    fun provideRecordStateObserverScope() : CoroutineScope {
        return CoroutineScope(
            SupervisorJob() + Dispatchers.Default
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    @AppScope
    @ServiceManagedScopeQualifier
    fun provideServiceManagedScope() : CoroutineScope {
        return CoroutineScope(
            SupervisorJob() + Dispatchers.Default.limitedParallelism(1)
        )
    }

    @Provides
    @AppScope
    @ApplicationScopeQualifier
    fun provideApplicationScope() : CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}