package com.xxmrk888ytxx.coreandroid

/**
 * [Ru]
 * Интерфейс для показа межэкранной рекламы
 */

/**
 * [En]
 * Interface for displaying inter-screen ads
 */
interface InterstitialAdShower {

    /**
     * [Ru]
     * Запрашивает показ рекламы
     *
     * @param key - ключ баннера в admob
     */

    /**
     * [En]
     * Requests to show ads
     *
     * @param key - banner key in admob
     */
    fun showAd(key:String)
}