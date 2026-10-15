package com.xxmrk888ytxx.settingsscreen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.coreandroid.Navigator
import com.xxmrk888ytxx.corecompose.LocalNavigator
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
import androidx.compose.foundation.lazy.LazyRow
import com.xxmrk888ytxx.coreandroid.models.NotificationIconType
import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.NotificationConfigurationDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.DualCameraPrimarySelectDialogState
import com.xxmrk888ytxx.settingsscreen.models.DialogModels.PipPositionSelectDialogState
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

    val navigator = LocalNavigator.current

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

            item {
                SettingsCategory(
                    categoryName = stringResource(R.string.export_settings),
                    settingsParams = autoExportToExternalStorageParams(settingsViewModel, navigator)
                )
            }

            item {
                SettingsCategory(
                    categoryName = stringResource(R.string.About_the_application),
                    settingsParams = aboutApplicationParams(settingsViewModel)
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

    if (dialogState.dualCameraPrimarySelectDialogState is DualCameraPrimarySelectDialogState.Showed) {
        DualCameraPrimarySelectDialog(
            settingsViewModel = settingsViewModel,
            initialPrimary = (dialogState.dualCameraPrimarySelectDialogState as DualCameraPrimarySelectDialogState.Showed).currentSetup
        )
    }

    if (dialogState.pipPositionSelectDialogState is PipPositionSelectDialogState.Showed) {
        PipPositionSelectDialog(
            settingsViewModel = settingsViewModel,
            initialPosition = (dialogState.pipPositionSelectDialogState as PipPositionSelectDialogState.Showed).currentSetup
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

    SelectDialog(
        onConfirm = {
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
                title = it.name,
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
            ),
            SelectDialogModel(
                title = stringResource(R.string.Both_cameras),
                isSelected = currentSelectedCameraType == CameraType.Both.id,
                onClick = { currentSelectedCameraType = CameraType.Both.id }
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

    data class AdditionalNotificationParameters(
        val text: String,
        val isSelect: Boolean,
        val onClick: (Boolean) -> Unit,
    )

    val supportedNotificationTypes = persistentListOf<SupportedNotificationType>(
        SupportedNotificationType(
            isSelected = currentState is NotificationConfigType.ViewRecordStateType,
            text = stringResource(R.string.Show_recording_status),
            onClick = {
                currentState = NotificationConfigType.ViewRecordStateType(
                    currentState.isStopRecordButtonEnabled,
                    currentState.isPauseResumeButtonActive
                )
            }

        ),
        SupportedNotificationType(
            isSelected = currentState is NotificationConfigType.CustomNotification,
            text = stringResource(R.string.Set_up_a_notification),
            onClick = {
                currentState = NotificationConfigType.CustomNotification(
                    currentState.isStopRecordButtonEnabled,
                    currentState.isPauseResumeButtonActive
                )
            }

        )
    )

    val additionalNotificationParametersList = persistentListOf(
        AdditionalNotificationParameters(
            text = stringResource(R.string.Add_a_button_to_pause_resume_the_recording),
            isSelect = currentState.isPauseResumeButtonActive,
            onClick = { newPauseResumeButtonState ->
                when (currentState) {
                    is NotificationConfigType.CustomNotification -> {
                        currentState = NotificationConfigType.CustomNotification(
                            isPauseResumeButtonActive = newPauseResumeButtonState,
                            isStopRecordButtonEnabled = currentState.isStopRecordButtonEnabled,
                            title = (currentState as NotificationConfigType.CustomNotification).title,
                            text = (currentState as NotificationConfigType.CustomNotification).text
                        )
                    }

                    is NotificationConfigType.ViewRecordStateType -> {
                        currentState = NotificationConfigType.ViewRecordStateType(
                            isStopRecordButtonEnabled = currentState.isStopRecordButtonEnabled,
                            isPauseResumeButtonActive = newPauseResumeButtonState
                        )
                    }
                }
            }
        ),
        AdditionalNotificationParameters(
            text = stringResource(R.string.Add_a_button_to_stop_recording),
            isSelect = currentState.isStopRecordButtonEnabled,
            onClick = { newStopRecordButtonEnabled ->
                when (currentState) {
                    is NotificationConfigType.CustomNotification -> {
                        currentState = NotificationConfigType.CustomNotification(
                            isPauseResumeButtonActive = currentState.isPauseResumeButtonActive,
                            isStopRecordButtonEnabled = newStopRecordButtonEnabled,
                            title = (currentState as NotificationConfigType.CustomNotification).title,
                            text = (currentState as NotificationConfigType.CustomNotification).text
                        )
                    }

                    is NotificationConfigType.ViewRecordStateType -> {
                        currentState = NotificationConfigType.ViewRecordStateType(
                            isStopRecordButtonEnabled = newStopRecordButtonEnabled,
                            isPauseResumeButtonActive = currentState.isPauseResumeButtonActive
                        )
                    }
                }
            }
        ),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(supportedNotificationTypes) {
                        val isSelected = it.isSelected
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) themeColors.primaryColor.copy(alpha = 0.15f) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isSelected) themeColors.primaryColor.copy(alpha = 0.5f) else Color(0xFF263346),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable(onClick = it.onClick)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = it.onClick,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = themeColors.primaryColor,
                                    unselectedColor = themeColors.secondFontColor
                                )
                            )

                            Text(
                                text = it.text,
                                color = if (isSelected) Color.White else themeColors.primaryFontColor,
                                style = themeTypography.body.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }
                    }

                    items(additionalNotificationParametersList) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (it.isSelect) themeColors.primaryColor.copy(alpha = 0.12f) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (it.isSelect) themeColors.primaryColor.copy(alpha = 0.4f) else Color(0xFF263346),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { it.onClick(!it.isSelect) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Checkbox(
                                checked = it.isSelect,
                                onCheckedChange = it.onClick,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = themeColors.primaryColor,
                                    uncheckedColor = themeColors.secondFontColor
                                )
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = it.text,
                                color = if (it.isSelect) Color.White else themeColors.primaryFontColor,
                                style = themeTypography.body,
                                modifier = Modifier.horizontalScroll(rememberScrollState())
                            )
                        }
                    }

                    item {
                        Column(
                            Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(
                                10.dp,
                                Alignment.CenterVertically
                            ),
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

                            val currentIconId = currentState.iconId
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Иконка уведомления (маскировка):",
                                style = themeTypography.head.copy(fontSize = 14.sp),
                                color = themeColors.primaryFontColor,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(NotificationIconType.values().toList()) { iconType ->
                                    val isSelected = iconType.id == currentIconId
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) themeColors.primaryColor else Color(0xFF1E293B))
                                            .border(
                                                1.dp,
                                                if (isSelected) themeColors.primaryColor else Color(0xFF334155),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                currentState = when (val s = currentState) {
                                                    is NotificationConfigType.CustomNotification -> s.copy(iconId = iconType.id)
                                                    is NotificationConfigType.ViewRecordStateType -> s.copy(iconId = iconType.id)
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 7.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = iconType.emoji, fontSize = 15.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = iconType.title,
                                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }

                            // Live Status Bar Notification Preview Card
                            Spacer(modifier = Modifier.height(14.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val selectedIconType = NotificationIconType.fromId(currentIconId)
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E293B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        androidx.compose.material3.Icon(
                                            painter = painterResource(id = selectedIconType.iconRes),
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        val previewTitle = when (val s = currentState) {
                                            is NotificationConfigType.CustomNotification -> s.title.ifEmpty { "Заголовок уведомления" }
                                            is NotificationConfigType.ViewRecordStateType -> "Фоновая запись звука/видео"
                                        }
                                        val previewText = when (val s = currentState) {
                                            is NotificationConfigType.CustomNotification -> s.text.ifEmpty { "Текст маскировки" }
                                            is NotificationConfigType.ViewRecordStateType -> "Идёт запись: 00:01:23"
                                        }
                                        Text(
                                            text = previewTitle,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = previewText,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
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

@Composable
internal fun DualCameraPrimarySelectDialog(
    settingsViewModel: SettingsViewModel,
    initialPrimary: DualCameraPrimary,
) {
    var selected by rememberSaveable {
        mutableStateOf(initialPrimary.name)
    }

    SelectDialog(
        onConfirm = {
            val primary = try {
                DualCameraPrimary.valueOf(selected)
            } catch (e: Exception) {
                DualCameraPrimary.BACK
            }
            settingsViewModel.changeDualCameraPrimary(primary)
            settingsViewModel.hideDualCameraPrimarySelectDialogState()
        },
        onCancel = settingsViewModel::hideDualCameraPrimarySelectDialogState,
        items = persistentListOf(
            SelectDialogModel(
                title = stringResource(R.string.Back_camera),
                isSelected = selected == DualCameraPrimary.BACK.name,
                onClick = { selected = DualCameraPrimary.BACK.name }
            ),
            SelectDialogModel(
                title = stringResource(R.string.Front_camera),
                isSelected = selected == DualCameraPrimary.FRONT.name,
                onClick = { selected = DualCameraPrimary.FRONT.name }
            )
        )
    )
}

@Composable
internal fun PipPositionSelectDialog(
    settingsViewModel: SettingsViewModel,
    initialPosition: PipPosition,
) {
    var selected by rememberSaveable {
        mutableStateOf(initialPosition.name)
    }

    SelectDialog(
        onConfirm = {
            val position = try {
                PipPosition.valueOf(selected)
            } catch (e: Exception) {
                PipPosition.TOP_RIGHT
            }
            settingsViewModel.changePipPosition(position)
            settingsViewModel.hidePipPositionSelectDialogState()
        },
        onCancel = settingsViewModel::hidePipPositionSelectDialogState,
        items = persistentListOf(
            SelectDialogModel(
                title = stringResource(R.string.pip_top_right),
                isSelected = selected == PipPosition.TOP_RIGHT.name,
                onClick = { selected = PipPosition.TOP_RIGHT.name }
            ),
            SelectDialogModel(
                title = stringResource(R.string.pip_top_left),
                isSelected = selected == PipPosition.TOP_LEFT.name,
                onClick = { selected = PipPosition.TOP_LEFT.name }
            ),
            SelectDialogModel(
                title = stringResource(R.string.pip_bottom_right),
                isSelected = selected == PipPosition.BOTTOM_RIGHT.name,
                onClick = { selected = PipPosition.BOTTOM_RIGHT.name }
            ),
            SelectDialogModel(
                title = stringResource(R.string.pip_bottom_left),
                isSelected = selected == PipPosition.BOTTOM_LEFT.name,
                onClick = { selected = PipPosition.BOTTOM_LEFT.name }
            )
        )
    )
}

