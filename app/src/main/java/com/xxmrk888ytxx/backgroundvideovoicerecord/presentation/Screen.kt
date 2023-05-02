package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation

sealed class Screen(val route:String) {

    object MainScreen : Screen("MainScreen")

    object VideoPlayerScreen : Screen("VideoPlayerScreen")
}
