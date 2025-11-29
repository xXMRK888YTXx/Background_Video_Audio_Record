package com.xxmrk888ytxx.fastopenappquicksettingsservice

import android.app.Activity
import android.os.Bundle

interface TitleServiceActivityRunner {
    fun <I : Activity> startActivity(activityClass: Class<I>)
}