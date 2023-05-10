package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.ToastManager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.xxmrk888ytxx.coreandroid.ToastManager
import javax.inject.Inject

class ToastManagerImpl @Inject constructor(
    private val context: Context
) : ToastManager {

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun showToast(text: String) {
        handler.post { Toast.makeText(context,text, Toast.LENGTH_SHORT).show() }
    }

    @SuppressLint("ResourceType")
    override fun showToast(resId: Int) {
        val text = context.getString(resId)
        showToast(text)
    }

}