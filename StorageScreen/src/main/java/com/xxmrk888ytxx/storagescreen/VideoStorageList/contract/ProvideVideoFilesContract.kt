package com.xxmrk888ytxx.storagescreen.VideoStorageList.contract

import com.xxmrk888ytxx.storagescreen.VideoStorageList.models.VideoFileModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProvideVideoFilesContract {

    val videoFiles: Flow<ImmutableList<VideoFileModel>>
}