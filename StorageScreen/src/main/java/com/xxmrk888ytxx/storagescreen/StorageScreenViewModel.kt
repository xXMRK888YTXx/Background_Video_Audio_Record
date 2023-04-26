package com.xxmrk888ytxx.storagescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.storagescreen.contracts.ProvideAudioFilesContract
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class StorageScreenViewModel @Inject constructor(
    private val provideAudioFilesContract: ProvideAudioFilesContract
) : ViewModel()  {

    val audioFiles = provideAudioFilesContract.files
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}