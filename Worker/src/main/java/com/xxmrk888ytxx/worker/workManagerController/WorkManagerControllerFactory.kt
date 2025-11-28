package com.xxmrk888ytxx.worker.workManagerController

import android.content.Context

object WorkManagerControllerFactory {

    fun create(context: Context) : WorkManagerController = WorkManagerControllerImpl(context)
}