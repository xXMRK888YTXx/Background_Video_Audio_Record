package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.xxmrk888ytxx.backgroundvideovoicerecord.R
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.OpenUrlUseCase.OpenUrlUseCase
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Provider

internal class ActivityViewModel @Inject constructor(
    private val preferencesStorage: PreferencesStorage,
    private val openUrlUseCase: OpenUrlUseCase
) : ViewModel(), Navigator {

    //Privacy Policy and Terms Of use dialog
    private val isNeedShowPrivacyPolicyAndTermsOfUseDialogKey =
        booleanPreferencesKey("isNeedShowPrivacyPolicyAndTermsOfUseDialogKey")

    val isNeedShowPrivacyPolicyAndTermsOfUseDialogState =
        preferencesStorage.getProperty(isNeedShowPrivacyPolicyAndTermsOfUseDialogKey, true)

    fun hidePrivacyPolicyAndTermsOfUseDialog() {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesStorage.writeProperty(isNeedShowPrivacyPolicyAndTermsOfUseDialogKey, false)
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

    var navController: NavController? = null
        internal set

    override fun toVideoPlayerScreen(videoUri: Uri) {
        handler.post {
            val encodedUrl = URLEncoder.encode(videoUri.toString(), StandardCharsets.UTF_8.toString())
            navController?.navigate("${Screen.VideoPlayerScreen.route}/$encodedUrl")
        }
    }

    override fun toAutoExportToExternalStorageScreen() {
        navController?.navigate(Screen.AutoExportToExternalStorageScreen.route)
    }

    override fun backScreen() {
        navController?.popBackStack()
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
}