package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xxmrk888ytxx.backgroundvideovoicerecord.R
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.theme.setContentWithAppTheme
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.theme.Themes
import com.xxmrk888ytxx.backgroundvideovoicerecord.utils.appComponent
import com.xxmrk888ytxx.bottombarscreen.BottomBarScreen
import com.xxmrk888ytxx.bottombarscreen.models.BottomBarScreenModel
import com.xxmrk888ytxx.corecompose.themeColors
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @SuppressLint("ResourceType")
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
                composable(Screen.MainScreen.route) {
                    BottomBarScreen(
                        bottomBarScreens = bottomScreens
                    )
                }
            }
        }
    }

    private val bottomScreens:List<BottomBarScreenModel>
        @SuppressLint("ResourceType") @Composable get() = listOf(
            BottomBarScreenModel(
                title = stringResource(R.string.Sound_record),
                icon = R.drawable.microphone,
                content = {
                    Box(Modifier.fillMaxSize()) {

                    }
                }
            ),
            BottomBarScreenModel(
                title = getString(R.string.Record_video),
                icon = R.drawable.videocam,
                content = {
                    Box(Modifier.fillMaxSize()) {

                    }
                }
            ),
            BottomBarScreenModel(
                title = getString(R.string.Settings),
                icon = R.drawable.settings,
                content = {
                    Box(Modifier.fillMaxSize()) {

                    }
                }
            ),
        )
}