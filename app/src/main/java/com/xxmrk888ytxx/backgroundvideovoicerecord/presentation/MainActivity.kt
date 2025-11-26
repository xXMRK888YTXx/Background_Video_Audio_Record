package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.xxmrk888ytxx.admobmanager.AdMobBanner
import com.xxmrk888ytxx.admobmanager.AdMobManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.R
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.theme.Themes
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.theme.setContentWithAppThemeNavigatorInterstitialAdShower
import com.xxmrk888ytxx.backgroundvideovoicerecord.utils.Const.VIDEO_URI_KEY
import com.xxmrk888ytxx.backgroundvideovoicerecord.utils.appComponent
import com.xxmrk888ytxx.backgroundvideovoicerecord.utils.composeViewModel
import com.xxmrk888ytxx.bottombarscreen.BottomBarScreen
import com.xxmrk888ytxx.bottombarscreen.models.BottomBarScreenModel
import com.xxmrk888ytxx.coreandroid.InterstitialAdShower
import com.xxmrk888ytxx.corecompose.Shared.AgreeDialog
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.recordaudioscreen.RecordAudioScreen
import com.xxmrk888ytxx.recordaudioscreen.RecordAudioViewModel
import com.xxmrk888ytxx.recordvideoscreen.RecordVideoScreen
import com.xxmrk888ytxx.recordvideoscreen.RecordVideoViewModel
import com.xxmrk888ytxx.settingsscreen.SettingsScreen
import com.xxmrk888ytxx.settingsscreen.SettingsViewModel
import com.xxmrk888ytxx.storagescreen.StorageScreen
import com.xxmrk888ytxx.storagescreen.AudioStorageList.AudioStorageListViewModel
import com.xxmrk888ytxx.storagescreen.VideoStorageList.VideoStorageListViewModel
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.LockBlockerScreen
import com.xxmrk888ytxx.videoplayerscreen.VideoPlayerScreen
import com.xxmrk888ytxx.videoplayerscreen.VideoPlayerViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import javax.inject.Inject
import javax.inject.Provider

internal class MainActivity : ComponentActivity(), LockBlockerScreen, InterstitialAdShower {

    @Inject
    lateinit var recordAudioViewModel: Provider<RecordAudioViewModel>

    @Inject
    lateinit var recordVideoViewModel: Provider<RecordVideoViewModel>

    @Inject
    lateinit var audioStorageListViewModel: AudioStorageListViewModel.Factory

    @Inject
    lateinit var videoStorageListViewModel: Provider<VideoStorageListViewModel>

    @Inject
    lateinit var videoPlayerViewModel: VideoPlayerViewModel.Factory

    @Inject
    lateinit var activityViewModelFactory: ActivityViewModel.Factory

    @Inject
    lateinit var settingsScreenViewModel: Provider<SettingsViewModel>

    @Inject
    lateinit var adMobManager: AdMobManager

    private val activityViewModel by viewModels<ActivityViewModel> { activityViewModelFactory }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        adMobManager.initAdmob()
        activityViewModel.loadConsentForm(this)
        setContentWithAppThemeNavigatorInterstitialAdShower(
            appTheme = Themes.dark,
            navigator = activityViewModel,
            interstitialAdShower = this
        ) {
            val navController = rememberNavController()

            val agreeDialogState by
                activityViewModel.isNeedShowPrivacyPolicyAndTermsOfUseDialogState.collectAsStateWithLifecycle(
                    initialValue = false
                )

            LaunchedEffect(key1 = navController, block = {
                activityViewModel.navController = navController
            })

            NavHost(
                navController = navController,
                startDestination = Screen.MainScreen.route,
                modifier = Modifier
                    .fillMaxSize()
                    .background(themeColors.background)
            ) {
                composable(Screen.MainScreen.route) {
                    BottomBarScreen(
                        bottomBarScreens = bottomScreens,
                        bannerAd = {
                            AdMobBanner(
                                adMobKey = stringResource(R.string.banner_key),
                                background = themeColors.background
                            )
                        }
                    )
                }

                composable(
                    route = "${Screen.VideoPlayerScreen.route}/{$VIDEO_URI_KEY}",
                    arguments = listOf(
                        navArgument(VIDEO_URI_KEY) { NavType.StringType }
                    )
                ) {
                    val uri = it.arguments?.getString(VIDEO_URI_KEY) ?: return@composable


                    this@MainActivity.showAd(getString(R.string.ad_key))

                    VideoPlayerScreen(
                        modifier = Modifier.fillMaxSize(),
                        videoPlayerViewModel = composeViewModel() {
                            videoPlayerViewModel.create(uri.toUri())
                        }
                    )
                }
            }

            if(agreeDialogState) {
                AgreeDialog(
                    openPrivacyPolicySite = activityViewModel::openPrivacyPolicy,
                    openTermsOfUseSite = activityViewModel::openTermsOfUse,
                    onConfirm = activityViewModel::hidePrivacyPolicyAndTermsOfUseDialog,
                    onCancel = this@MainActivity::finish
                )
            }
        }
    }

    private val bottomScreens: ImmutableList<BottomBarScreenModel>
        @SuppressLint("ResourceType") @Composable get() = persistentListOf(
            BottomBarScreenModel(
                title = stringResource(R.string.Sound_record),
                icon = R.drawable.microphone,
                content = {
                    RecordAudioScreen(
                        recordAudioViewModel = composeViewModel { recordAudioViewModel.get() }
                    )
                }
            ),
            BottomBarScreenModel(
                title = getString(R.string.Record_video),
                icon = R.drawable.videocam,
                content = {
                    RecordVideoScreen(
                        recordVideoViewModel = composeViewModel { recordVideoViewModel.get() }
                    )
                }
            ),
            BottomBarScreenModel(
                title = getString(R.string.Storage),
                icon = R.drawable.baseline_storage_24,
                content = {
                    StorageScreen(
                        audioStorageListViewModel = composeViewModel {
                            audioStorageListViewModel.create(
                                this
                            )
                        },
                        videoStorageListViewModel = composeViewModel() { videoStorageListViewModel.get() }
                    )
                }
            ),
            BottomBarScreenModel(
                title = getString(R.string.Settings),
                icon = R.drawable.settings,
                content = {
                    SettingsScreen(
                        composeViewModel { settingsScreenViewModel.get() }
                    )
                }
            ),
        )

    override fun enable() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun cancel() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun showAd(key: String) {
        if(!activityViewModel.isAllowShowAd) {
            Log.i(LOG_TAG_FOR_AD,"Show ad request cancel")
            return
        }

        Log.i(LOG_TAG_FOR_AD,"Show ad request")
        adMobManager.showInterstitialAd(key,this)
        activityViewModel.adShowNotify()
    }

    companion object {
        val LOG_TAG_FOR_AD = "InterstitialAdShower"
    }
}