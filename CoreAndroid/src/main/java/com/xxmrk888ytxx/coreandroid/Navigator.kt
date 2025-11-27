package com.xxmrk888ytxx.coreandroid

import android.net.Uri

interface Navigator {
    fun toVideoPlayerScreen(videoUri: Uri)

    fun toAutoExportToExternalStorageScreen()
}