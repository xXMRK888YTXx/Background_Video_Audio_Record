package com.xxmrk888ytxx.backgroundvideovoicerecord.di

import android.content.Context
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.MainActivity
import dagger.BindsInstance
import dagger.Component

@Component
@AppScope
interface AppComponent {

    fun inject(mainActivity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context) : AppComponent
    }
}