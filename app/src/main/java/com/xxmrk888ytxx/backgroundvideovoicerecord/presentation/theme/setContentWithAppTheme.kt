package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.theme

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.corecompose.AppTheme
import com.xxmrk888ytxx.corecompose.LocalNavigator
import com.xxmrk888ytxx.corecompose.LocalTheme

fun ComponentActivity.setContentWithAppThemeNavigatorInterstitialAdShower(
    appTheme: AppTheme,
    navigator: Navigator,
    content: @Composable () -> Unit
) {
    setContent {
        CompositionLocalProvider(
            LocalTheme provides appTheme,
            LocalNavigator provides navigator,
            content = content
        )
    }
}