package com.xxmrk888ytxx.autoexporttoexternalstoragescreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.autoexporttoexternalstoragescreen.contract.ManageExportToExternalStorageSettingContract
import com.xxmrk888ytxx.autoexporttoexternalstoragescreen.model.ScanFolderTime
import com.xxmrk888ytxx.autoexporttoexternalstoragescreen.model.ScreenState
import com.xxmrk888ytxx.coreandroid.ToastManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AutoExportToExternalStorageViewModel @Inject constructor(
    private val manageExportToExternalStorageSettingContract: ManageExportToExternalStorageSettingContract,
    private val toastManager: ToastManager
) : ViewModel() {

    private val isDropDownVisible = MutableStateFlow(false)

    internal val screenState = combine(
        manageExportToExternalStorageSettingContract.externalStorageExportParams,
        isDropDownVisible
    ) { externalStorageExportParams, isDropDownVisible ->
        ScreenState(
            isExportEnabled = externalStorageExportParams.isExportEnabled,
            isExportFolderSelected = externalStorageExportParams.isExportFolderSelected,
            scanFolderTime = externalStorageExportParams.scanFolderTime,
            isAutoExportAfterCreateNewRecordEnabled = externalStorageExportParams.isAutoExportAfterCreateNewRecordEnabled,
            isDropDownVisible = isDropDownVisible
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ScreenState()
    )


    internal fun onExportFolderSelect(folderUri: Uri?) {
        if (folderUri == null) {
            showUserDidNotSelectFolderErrorToast()
            return
        }
        viewModelScope.launch {
            manageExportToExternalStorageSettingContract.setupExportFolderUri(folderUri)
                .onSuccess {
                    withContext(Dispatchers.Main) { toastManager.showToast(R.string.the_export_folder_was_saved) }
                }
                .onFailure { withContext(Dispatchers.Main) { showUserDidNotSelectFolderErrorToast() } }
        }
    }

    private fun showUserDidNotSelectFolderErrorToast() {
        toastManager.showToast(R.string.the_export_folder_is_not_selected_or_the_selected_folder_cannot_be_used)
    }

    internal fun switchExportState() {
        viewModelScope.launch { manageExportToExternalStorageSettingContract.changeExportState(!screenState.value.isExportEnabled) }
    }

    internal fun onScanFolderTimeChanged(newTime: ScanFolderTime) {
        viewModelScope.launch {
            manageExportToExternalStorageSettingContract.changeScanFolderTime(
                newTime
            )
        }
    }

    internal fun onDropDownStateChanged(newState: Boolean) {
        isDropDownVisible.value = newState
    }


    internal fun onAutoExportAfterCreateNewRecordStateChanged() {
        viewModelScope.launch {
            manageExportToExternalStorageSettingContract.changeAutoExportAfterCreateNewRecordState(
                !screenState.value.isAutoExportAfterCreateNewRecordEnabled
            )
        }
    }


}