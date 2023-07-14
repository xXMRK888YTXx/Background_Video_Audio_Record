package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.xxmrk888ytxx.admobmanager.ConsentFormLoader
import com.xxmrk888ytxx.backgroundvideovoicerecord.BuildConfig
import com.xxmrk888ytxx.backgroundvideovoicerecord.R
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.OpenUrlUseCase.OpenUrlUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.MainActivity.Companion.LOG_TAG_FOR_AD
import com.xxmrk888ytxx.backgroundvideovoicerecord.utils.Const.VIDEO_URI_KEY
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Provider

internal class ActivityViewModel @Inject constructor(
    private val preferencesStorage: PreferencesStorage,
    private val openUrlUseCase: OpenUrlUseCase
) : ViewModel(),Navigator {

    internal var isAllowShowAd = true
        private set

    private var currentShowAdCount = 0
        private set

    private var isConsentChecked:Boolean = false

    fun loadConsentForm(activity: Activity) {
        if(isConsentChecked) return

        isConsentChecked = true

        val logTag = "ConsentFormLoader"

        val loader = ConsentFormLoader.create(
            activity,
            BuildConfig.DEBUG,
            true
        )

        loader.checkFormState(
            onFormPrepared = {
                Log.i(logTag, "onFormPrepared")

                loader.loadAndShowForm(
                    onSuccessLoad = {
                        Log.i(logTag, "onSuccessLoad")
                    },
                    onLoadError = {
                        Log.e(logTag, "onLoadError")

                    },
                    onDismissed = {
                        Log.i(logTag, "onDismissed")
                    }
                )
            },
            onFormNotAvailable = {
                Log.e(logTag, "onFormNotAvailable")
            },
            onError = {
                Log.e(logTag, "onError")
            }
        )
    }

    fun adShowNotify() {
        if(!isAllowShowAd) return
        currentShowAdCount += 1

        if(currentShowAdCount >= MAX_SHOW_AD_IN_TIME_SPAN) {
            isAllowShowAd = false
            Log.i(LOG_TAG_FOR_AD,"Ad lock active")

            viewModelScope.launch(Dispatchers.Default) {
                withTimeoutOrNull(TIME_SPAN) {
                    while (isActive) {
                        delay(10000)
                    }
                }

                Log.i(LOG_TAG_FOR_AD,"Ad lock cancel")
                isAllowShowAd = true
                currentShowAdCount = 0
            }
        }

    }

    //Privacy Policy and Terms Of use dialog
    private val isNeedShowPrivacyPolicyAndTermsOfUseDialogKey = booleanPreferencesKey("isNeedShowPrivacyPolicyAndTermsOfUseDialogKey")

    val isNeedShowPrivacyPolicyAndTermsOfUseDialogState = preferencesStorage.getProperty(isNeedShowPrivacyPolicyAndTermsOfUseDialogKey,true)

    fun hidePrivacyPolicyAndTermsOfUseDialog() {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesStorage.writeProperty(isNeedShowPrivacyPolicyAndTermsOfUseDialogKey,false)
        }
    }

    @SuppressLint("ResourceType")
    fun openPrivacyPolicy() {
        openUrlUseCase.execute(R.string.privacy_policy_url)
    }

    @SuppressLint("ResourceType")
    fun openTermsOfUse() {
        openUrlUseCase.execute(R.string.terms_of_use_url)
    }
    //



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

    companion object {
        private const val MAX_SHOW_AD_IN_TIME_SPAN = 2
        private const val TIME_SPAN = 600_000L //Millis
    }
}