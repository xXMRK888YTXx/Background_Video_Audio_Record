package com.xxmrk888ytxx.corecompose

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.xxmrk888ytxx.coreandroid.InterstitialAdShower
import com.xxmrk888ytxx.coreandroid.Navigator

val LocalTheme = staticCompositionLocalOf<AppTheme> {
    error("AppTheme not provided")
}

val LocalNavigator = compositionLocalOf<Navigator> {
    error("Navigator not provided")
}

val LocalInterstitialAdShower = compositionLocalOf<InterstitialAdShower> {
    error("InterstitialAdShower not provided")
}