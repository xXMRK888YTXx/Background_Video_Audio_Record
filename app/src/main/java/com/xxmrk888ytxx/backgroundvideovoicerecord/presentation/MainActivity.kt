package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.theme.setContentWithAppTheme
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.theme.Themes
import com.xxmrk888ytxx.backgroundvideovoicerecord.utils.appComponent
import com.xxmrk888ytxx.corecompose.themeColors
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentWithAppTheme(appTheme = Themes.dark) {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Screen.MainScreen.route,
                modifier = Modifier
                    .fillMaxSize()
                    .background(themeColors.background)
            ) {
                composable(Screen.MainScreen.route) {}
            }
        }
    }
}