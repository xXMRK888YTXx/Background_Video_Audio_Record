package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.theme

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.xxmrk888ytxx.coreandroid.InterstitialAdShower
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.corecompose.AppTheme
import com.xxmrk888ytxx.corecompose.LocalInterstitialAdShower
import com.xxmrk888ytxx.corecompose.LocalNavigator
import com.xxmrk888ytxx.corecompose.LocalTheme


fun ComponentActivity.setContentWithAppThemeNavigatorInterstitialAdShower(
    appTheme: AppTheme,
    navigator: Navigator,
    interstitialAdShower: InterstitialAdShower,
    content: @Composable () -> Unit
) {
    setContent {
        val systemUiController = rememberSystemUiController()

        systemUiController.setStatusBarColor(appTheme.colors.statusBar)
        systemUiController.setNavigationBarColor(appTheme.colors.navigationBar)

        CompositionLocalProvider(
            LocalTheme provides appTheme,
            LocalNavigator provides navigator,
            LocalInterstitialAdShower provides interstitialAdShower,
            content = content
        )
    }
}