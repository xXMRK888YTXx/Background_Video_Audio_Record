package com.xxmrk888ytxx.storagescreen.VideoStorageList.contract

import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.coreandroid.models.DualCameraSaveOption

interface OpenVideoContract {

    suspend fun openVideoContract(videoId: Long, navigator: Navigator)

    suspend fun openDualVideoContract(videoId: Long, option: DualCameraSaveOption, navigator: Navigator)
}