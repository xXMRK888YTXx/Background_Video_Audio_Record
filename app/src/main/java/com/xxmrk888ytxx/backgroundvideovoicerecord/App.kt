package com.xxmrk888ytxx.backgroundvideovoicerecord

import android.app.Application
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.DaggerAppComponent

class App : Application() {

    val appComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}