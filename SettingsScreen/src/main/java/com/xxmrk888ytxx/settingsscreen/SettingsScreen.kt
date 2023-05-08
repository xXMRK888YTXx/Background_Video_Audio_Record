package com.xxmrk888ytxx.settingsscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.corecompose.Shared.SelectDialog
import com.xxmrk888ytxx.corecompose.Shared.models.SelectDialogModel
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraTypeSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraType
import kotlinx.collections.immutable.persistentListOf

@SuppressLint("ResourceType")
@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {

    val dialogState by settingsViewModel.dialogState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.Transparent
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = it.calculateStartPadding(LocalLayoutDirection.current),
                    end = it.calculateEndPadding(LocalLayoutDirection.current),
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding()
                )
        ) {
            item {
                SettingsCategory(stringResource(R.string.Camera), cameraParams(settingsViewModel))
            }
        }
    }

    if (dialogState.isCameraTypeSelectDialogVisible is CameraTypeSelectDialogState.Showed) {
        CameraTypeSelectDialog(
            settingsViewModel = settingsViewModel,
            initialCameraType = (dialogState.isCameraTypeSelectDialogVisible as CameraTypeSelectDialogState.Showed)
                    .currentSelected
        )
    }
}

@Composable
fun CameraTypeSelectDialog(
    settingsViewModel: SettingsViewModel,
    initialCameraType: CameraType,
) {

    var currentSelectedCameraType by rememberSaveable {
        mutableStateOf(initialCameraType.id)
    }

    SelectDialog(
        onConfirm = {
            settingsViewModel.changeCurrentCameraType(
                CameraType.getCameraTypeById(
                    currentSelectedCameraType
                )
            )

            settingsViewModel.hideCameraTypeSelectDialog()
        },
        onCancel = settingsViewModel::hideCameraTypeSelectDialog,
        items = persistentListOf(
            SelectDialogModel(
                title = stringResource(R.string.Front_camera),
                isSelected = currentSelectedCameraType == CameraType.Front.id,
                onClick = { currentSelectedCameraType = CameraType.Front.id }
            ),
            SelectDialogModel(
                title = stringResource(R.string.Back_camera),
                isSelected = currentSelectedCameraType == CameraType.Back.id,
                onClick = { currentSelectedCameraType = CameraType.Back.id }
            )
        )
    )
}
