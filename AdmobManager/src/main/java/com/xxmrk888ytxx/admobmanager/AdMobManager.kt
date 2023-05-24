package com.xxmrk888ytxx.admobmanager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import javax.inject.Inject

class AdMobManager @Inject constructor(
    private val context:Context
) {

    /**
     * [Ru]
     * Инициализация admob сервисов
     */

    /** * [En] * Initializing admob services */
    fun initAdmob() {
        MobileAds.initialize(context)
    }

    /**
     * [Ru]
     * Показ межстраничной рекламы
     */

    /**
     * [En]
     * Show interstitial ads
     */
    fun showInterstitialAd(key:String,activity: Activity) {

        InterstitialAd.load(
            context,
            key,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    Log.d(LOG_TAG,"InterstitialAd load error:${error.message}")
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    super.onAdLoaded(ad)
                    ad.show(activity)
                }
            }
        )
    }

    companion object {
        const val LOG_TAG = "AdMobManager"
    }
}