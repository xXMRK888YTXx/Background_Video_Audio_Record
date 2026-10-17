package com.xxmrk888ytxx.fastopenappquicksettingsservice

import android.app.Activity
import android.content.Intent

interface TitleServiceActivityRunner {
    fun <I : Activity> startActivity(activityClass: Class<I>)
    fun <I : Activity> startActivity(activityClass: Class<I>, configureIntent: (Intent) -> Unit)
}