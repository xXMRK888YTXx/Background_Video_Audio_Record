package com.xxmrk888ytxx.settingsscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.corecompose.Shared.SelectDialog
import com.xxmrk888ytxx.corecompose.Shared.StyleIcon
import com.xxmrk888ytxx.corecompose.Shared.models.SelectDialogModel
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeTypography
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraMaxQualitySelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraRotationSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraTypeSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraMaxQuality
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraRotation
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

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

    if (dialogState.cameraTypeSelectDialogState is CameraTypeSelectDialogState.Showed) {
        CameraTypeSelectDialog(
            settingsViewModel = settingsViewModel,
            initialCameraType = (dialogState.cameraTypeSelectDialogState
                    as CameraTypeSelectDialogState.Showed)
                .currentSetup
        )
    }

    if (dialogState.cameraMaxQualitySelectDialogState is CameraMaxQualitySelectDialogState.Showed) {
        CameraMaxQualitySelectDialog(
            settingsViewModel = settingsViewModel,
            initialCameraMaxQuality = (dialogState.cameraMaxQualitySelectDialogState as CameraMaxQualitySelectDialogState.Showed).currentSetup
        )
    }

    if (dialogState.cameraRotationSelectDialogState is CameraRotationSelectDialogState.Showed) {
        CameraRotationSelectDialog(
            settingsViewModel,
            initialCameraRotation = (dialogState.cameraRotationSelectDialogState
                    as CameraRotationSelectDialogState.Showed).initialCameraRotation
        )
    }
}

@Composable
internal fun CameraRotationSelectDialog(
    settingsViewModel: SettingsViewModel,
    initialCameraRotation: CameraRotation,
) {
    var currentSelectedCameraRotation by rememberSaveable {
        mutableStateOf(initialCameraRotation.id)
    }

    val warmingAboutPreview:@Composable () -> Unit = remember {
        { WarmingAboutPreview() }
    }

    SelectDialog(onConfirm = {
        settingsViewModel.changeCameraRotation(
            CameraRotation.fromId(
                currentSelectedCameraRotation
            )
        )

        settingsViewModel.hideCameraRotationSelectDialogState()
    },
        onCancel = settingsViewModel::hideCameraRotationSelectDialogState,
        items = persistentListOf(
            SelectDialogModel(
                title = "0 ${stringResource(R.string.degree)}",
                isSelected = CameraRotation.ROTATION_0.id == currentSelectedCameraRotation,
                onClick = { currentSelectedCameraRotation = CameraRotation.ROTATION_0.id }
            ),

            SelectDialogModel(
                title = "90 ${stringResource(R.string.degree)}",
                isSelected = CameraRotation.ROTATION_90.id == currentSelectedCameraRotation,
                onClick = { currentSelectedCameraRotation = CameraRotation.ROTATION_90.id }
            ),

            SelectDialogModel(
                title = "180 ${stringResource(R.string.degree)}",
                isSelected = CameraRotation.ROTATION_180.id == currentSelectedCameraRotation,
                onClick = { currentSelectedCameraRotation = CameraRotation.ROTATION_180.id }
            ),

            SelectDialogModel(
                title = "270 ${stringResource(R.string.degree)}",
                isSelected = CameraRotation.ROTATION_270.id == currentSelectedCameraRotation,
                onClick = { currentSelectedCameraRotation = CameraRotation.ROTATION_270.id }
            ),
        ),
        additionalContent = warmingAboutPreview
    )
}


@Composable
fun CameraMaxQualitySelectDialog(
    settingsViewModel: SettingsViewModel,
    initialCameraMaxQuality: CameraMaxQuality,
) {
    var currentSelectedCameraMaxQuality by rememberSaveable {
        mutableStateOf(initialCameraMaxQuality.id)
    }

    val warmingAboutPreview:@Composable () -> Unit = remember {
        { WarmingAboutPreview() }
    }

    SelectDialog(
        onConfirm = {
            settingsViewModel.changeCurrentCameraMaxQuality(
                CameraMaxQuality.fromID(currentSelectedCameraMaxQuality)
            )

            settingsViewModel.hideCameraMaxQualitySelectDialogState()
        },
        onCancel = settingsViewModel::hideCameraMaxQualitySelectDialogState,
        items = CameraMaxQuality.allCameraQualities.map {
            SelectDialogModel(
                title = it::class.simpleName ?: "",
                isSelected = currentSelectedCameraMaxQuality == it.id,
                onClick = { currentSelectedCameraMaxQuality = it.id }
            )
        }.toImmutableList(),
        additionalContent = warmingAboutPreview
    )
}

@Composable
internal fun CameraTypeSelectDialog(
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

@Composable
internal fun WarmingAboutPreview() {
    Row(
        Modifier.fillMaxWidth().padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StyleIcon(size = 20.dp,painter = painterResource(id = R.drawable.baseline_warning_24))

        Text(
            text = stringResource(R.string.Warming_about_camera_preview),
            style = themeTypography.body.copy(fontSize = 14.sp),
            color = themeColors.primaryFontColor
        )
    }
}
