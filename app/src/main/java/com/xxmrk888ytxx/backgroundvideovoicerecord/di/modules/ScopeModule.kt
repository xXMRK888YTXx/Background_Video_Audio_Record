package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import com.xxmrk888ytxx.backgroundvideovoicerecord.di.AppScope
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.ApplicationScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.RecordAudioStateObserverScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.AudioServiceManagedScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.RecordVideoStateObserverScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.VideoServiceManagedScopeQualifier
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
    @RecordAudioStateObserverScopeQualifier
    fun provideRecordAudioStateObserverScope() : CoroutineScope {
        return CoroutineScope(
            SupervisorJob() + Dispatchers.Default
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    @AppScope
    @AudioServiceManagedScopeQualifier
    fun provideAudioServiceManagedScope() : CoroutineScope {
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

    @Provides
    @AppScope
    @RecordVideoStateObserverScopeQualifier
    fun provideRecordVideoStateObserverScope() : CoroutineScope {
        return CoroutineScope(
            SupervisorJob() + Dispatchers.Default
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    @AppScope
    @VideoServiceManagedScopeQualifier
    fun provideVideoServiceManagedScope() : CoroutineScope {
        return CoroutineScope(
            SupervisorJob() + Dispatchers.Default.limitedParallelism(1)
        )
    }
}