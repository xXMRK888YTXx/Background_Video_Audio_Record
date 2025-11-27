package com.xxmrk888ytxx.autoexporttoexternalstoragescreen.model

import androidx.annotation.StringRes
import com.xxmrk888ytxx.autoexporttoexternalstoragescreen.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed class ScanFolderTime(@param:StringRes internal val uiText: Int) {
    object OneHour : ScanFolderTime(R.string._1_hour)
    object ThreeHour : ScanFolderTime(R.string._3_hours)
    object SixHour : ScanFolderTime(R.string._6_hours)
    object TwelveHour : ScanFolderTime(R.string._12_hours)
    object OneDay : ScanFolderTime(R.string._1_day)

    companion object {
        val scanFolderTimeList: ImmutableList<ScanFolderTime> by lazy { persistentListOf(OneHour, ThreeHour, SixHour, TwelveHour, OneDay) }
    }
}