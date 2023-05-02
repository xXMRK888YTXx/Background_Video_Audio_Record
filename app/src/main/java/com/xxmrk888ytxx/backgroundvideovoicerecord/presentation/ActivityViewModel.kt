package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.xxmrk888ytxx.backgroundvideovoicerecord.utils.Const.VIDEO_URI_KEY
import com.xxmrk888ytxx.coreandroid.Navigator
import javax.inject.Inject
import javax.inject.Provider

internal class ActivityViewModel @Inject constructor() : ViewModel(),Navigator {

    //Handler
    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }
    //

    //Navigator

    var navController:NavController? = null
        internal set

    override fun toVideoPlayerScreen(videoUri: Uri) {
        handler.post {
            navController?.navigateWithData(Screen.VideoPlayerScreen) {
                putString(VIDEO_URI_KEY,videoUri.toString())
            }
        }
    }
    //


    //ViewModel factory
    class Factory @Inject constructor(
        private val activityViewModel: Provider<ActivityViewModel>
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return activityViewModel.get() as T
        }
    }


    private fun NavController.navigateWithData(route:Screen,data:Bundle.() -> Unit) {
        navigate(route.route) { launchSingleTop = true }

        val dataBundle = Bundle().apply(data)

        getBackStackEntry(route.route).arguments?.putAll(dataBundle)
    }
}