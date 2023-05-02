package com.xxmrk888ytxx.storagescreen.VideoStorageList.contract

import com.xxmrk888ytxx.coreandroid.Navigator

interface OpenVideoContract {

    suspend fun openVideoContract(videoId:Long,navigator: Navigator)
}