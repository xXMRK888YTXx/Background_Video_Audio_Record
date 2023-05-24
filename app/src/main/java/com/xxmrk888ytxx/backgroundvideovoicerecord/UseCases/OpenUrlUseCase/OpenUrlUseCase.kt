package com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.OpenUrlUseCase

import androidx.annotation.IdRes

interface OpenUrlUseCase {

    fun execute(url:String)

    fun execute(@IdRes resId:Int)
}