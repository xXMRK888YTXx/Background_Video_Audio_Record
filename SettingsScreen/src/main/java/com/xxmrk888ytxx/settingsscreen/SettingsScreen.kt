package com.xxmrk888ytxx.settingsscreen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.corecompose.Shared.SelectDialog
import com.xxmrk888ytxx.corecompose.Shared.StyleCard
import com.xxmrk888ytxx.corecompose.Shared.StyleIcon
import com.xxmrk888ytxx.corecompose.Shared.YesNoButtons
import com.xxmrk888ytxx.corecompose.Shared.models.SelectDialogModel
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeTypography
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraMaxQualitySelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraRotationSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.CameraTypeSelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.NotificationConfigurationDialogState
import com.xxmrk888ytxx.settingsscreen.models.NotificationConfigType
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

            item {
                SettingsCategory(
                    categoryName = stringResource(R.string.Notifications),
                    settingsParams = notificationParams(settingsViewModel)
                )
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

    if (dialogState.notificationConfigurationDialog is NotificationConfigurationDialogState.Showed) {
        NotificationConfigurationDialog(
            initialState = (dialogState.notificationConfigurationDialog as NotificationConfigurationDialogState.Showed).initialState,
            settingsViewModel = settingsViewModel
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

    val warmingAboutPreview: @Composable () -> Unit = remember {
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

    val warmingAboutPreview: @Composable () -> Unit = remember {
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
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StyleIcon(size = 20.dp, painter = painterResource(id = R.drawable.baseline_warning_24))

        Text(
            text = stringResource(R.string.Warming_about_camera_preview),
            style = themeTypography.body.copy(fontSize = 14.sp),
            color = themeColors.primaryFontColor
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun NotificationConfigurationDialog(
    initialState: NotificationConfigType,
    settingsViewModel: SettingsViewModel,
) {

    var currentState by rememberSaveable() {
        mutableStateOf(initialState)
    }

    data class SupportedNotificationType(
        val isSelected: Boolean,
        val text: String,
        val onClick: () -> Unit,
    )

    val supportedNotificationTypes = persistentListOf<SupportedNotificationType>(
        SupportedNotificationType(
            isSelected = currentState is NotificationConfigType.ViewRecordStateType,
            text = stringResource(R.string.Show_recording_status),
            onClick = remember {
                { currentState = NotificationConfigType.ViewRecordStateType }
            }
        ),
        SupportedNotificationType(
            isSelected = currentState is NotificationConfigType.CustomNotification,
            text = stringResource(R.string.Set_up_a_notification),
            onClick = remember {
                { currentState = NotificationConfigType.CustomNotification() }
            }
        )
    )



    Dialog(
        onDismissRequest = settingsViewModel::hideNotificationConfigurationDialog
    ) {
        StyleCard(
            modifier = Modifier
                .fillMaxWidth(),
            ) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {

                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    items(supportedNotificationTypes) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.clickable(onClick = it.onClick)
                        ) {
                            RadioButton(
                                selected = it.isSelected,
                                onClick = it.onClick,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = themeColors.primaryColor,
                                    unselectedColor = themeColors.primaryFontColor
                                )
                            )

                            Text(
                                text = it.text,
                                color = themeColors.primaryFontColor,
                                style = themeTypography.body
                            )

                        }
                    }

                    item {
                        Column(
                            Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp,Alignment.CenterVertically),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            OutlinedTextField(
                                value = (currentState as? NotificationConfigType.CustomNotification)?.title
                                    ?: "",
                                onValueChange = {
                                    val changeResult =
                                        (currentState as? NotificationConfigType.CustomNotification)?.copy(
                                            title = it
                                        )

                                    changeResult?.run {
                                        currentState = this
                                    }
                                },
                                label = {
                                    Text(
                                        text = stringResource(R.string.Notice_header),
                                        style = themeTypography.head.copy(fontSize = 16.sp)
                                    )
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = themeColors.primaryColor,
                                    unfocusedIndicatorColor = themeColors.primaryFontColor,
                                    focusedLabelColor = themeColors.primaryColor,
                                    unfocusedLabelColor = themeColors.primaryFontColor,
                                ),
                                enabled = currentState is NotificationConfigType.CustomNotification,
                                textStyle = themeTypography.body.copy(
                                    color = themeColors.primaryFontColor
                                )
                            )

                            OutlinedTextField(
                                value = (currentState as? NotificationConfigType.CustomNotification)?.text
                                    ?: "",
                                onValueChange = {
                                    val changeResult =
                                        (currentState as? NotificationConfigType.CustomNotification)?.copy(
                                            text = it
                                        )

                                    changeResult?.run {
                                        currentState = this
                                    }
                                },
                                label = {
                                    Text(
                                        text = stringResource(R.string.Notification_text),
                                        style = themeTypography.head.copy(fontSize = 16.sp)
                                    )
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = themeColors.primaryColor,
                                    unfocusedIndicatorColor = themeColors.primaryFontColor,
                                    focusedLabelColor = themeColors.primaryColor,
                                    unfocusedLabelColor = themeColors.primaryFontColor,
                                ),
                                enabled = currentState is NotificationConfigType.CustomNotification,
                                textStyle = themeTypography.body.copy(
                                    color = themeColors.primaryFontColor
                                )
                            )
                        }
                    }
                }


                YesNoButtons(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    onNoButtonClick = settingsViewModel::hideNotificationConfigurationDialog,
                    onYesButtonClick = { settingsViewModel.setNotificationConfig(currentState) }
                )
            }
        }
    }
}
