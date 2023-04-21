package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.theme

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.xxmrk888ytxx.corecompose.AppTheme
import com.xxmrk888ytxx.corecompose.LocalTheme


fun ComponentActivity.setContentWithAppTheme(
    appTheme: AppTheme,
    content: @Composable () -> Unit
) {
    setContent {
        val systemUiController = rememberSystemUiController()

        systemUiController.setStatusBarColor(appTheme.colors.statusBar)
        systemUiController.setNavigationBarColor(appTheme.colors.navigationBar)

        CompositionLocalProvider(
            LocalTheme provides appTheme,
            content = content
        )
    }
}