package com.xxmrk888ytxx.storagescreen.VideoStorageList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.DeleteVideoFileContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.OpenVideoContract
import com.xxmrk888ytxx.storagescreen.VideoStorageList.contract.ProvideVideoFilesContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class VideoStorageListViewModel @Inject constructor(
    private val deleteVideoFileContract: DeleteVideoFileContract,
    private val provideVideoFileContract: ProvideVideoFilesContract,
    private val openVideoContract: OpenVideoContract
) : ViewModel() {

    //files
    internal val videoFiles = provideVideoFileContract.videoFiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeFile(id:Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteVideoFileContract.removeFile(id)
        }
    }
    //

    //Open video
    internal fun openVideo(videoId:Long,navigator: Navigator) {
        viewModelScope.launch(Dispatchers.IO) {
            openVideoContract.openVideoContract(videoId, navigator)
        }
    }
    //

}