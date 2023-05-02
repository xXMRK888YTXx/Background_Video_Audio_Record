package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.theme

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.corecompose.AppTheme
import com.xxmrk888ytxx.corecompose.LocalNavigator
import com.xxmrk888ytxx.corecompose.LocalTheme


fun ComponentActivity.setContentWithAppThemeAndNavigator(
    appTheme: AppTheme,
    navigator: Navigator,
    content: @Composable () -> Unit
) {
    setContent {
        val systemUiController = rememberSystemUiController()

        systemUiController.setStatusBarColor(appTheme.colors.statusBar)
        systemUiController.setNavigationBarColor(appTheme.colors.navigationBar)

        CompositionLocalProvider(
            LocalTheme provides appTheme,
            LocalNavigator provides navigator,
            content = content
        )
    }
}