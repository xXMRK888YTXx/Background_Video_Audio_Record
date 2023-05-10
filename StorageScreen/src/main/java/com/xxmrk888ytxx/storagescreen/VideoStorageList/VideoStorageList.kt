package com.xxmrk888ytxx.storagescreen.VideoStorageList

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.corecompose.LocalNavigator
import com.xxmrk888ytxx.corecompose.Shared.StyleCard
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeTypography
import com.xxmrk888ytxx.privatenote.presentation.ActivityLaunchContacts.SingleAccessExternalFileContract
import com.xxmrk888ytxx.storagescreen.MediaFileItem.MediaFileItem
import com.xxmrk888ytxx.storagescreen.MediaFileItem.models.MediaFileButton
import com.xxmrk888ytxx.storagescreen.R
import com.xxmrk888ytxx.storagescreen.RenameDialog
import com.xxmrk888ytxx.storagescreen.Stub
import com.xxmrk888ytxx.storagescreen.VideoStorageList.models.RenameDialogState
import kotlinx.collections.immutable.persistentListOf

@SuppressLint("ResourceType")
@Composable
fun VideoStorageList(videoStorageListViewModel: VideoStorageListViewModel) {
    val videoFiles by videoStorageListViewModel.videoFiles.collectAsStateWithLifecycle()

    val dialogState by videoStorageListViewModel.dialogState.collectAsStateWithLifecycle()

    val navigator = LocalNavigator.current

    val selectExportPathContract = rememberLauncherForActivityResult(
        contract = SingleAccessExternalFileContract(),
        onResult = videoStorageListViewModel::onExportPathSelected
    )

    if(videoFiles.isEmpty()) {
        Stub(text = stringResource(R.string.Videos_missing))
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(videoFiles, key = { it.id }) {
                val buttons = persistentListOf(
                    MediaFileButton(
                        icon = R.drawable.baseline_play_arrow_24,
                        onClick = { videoStorageListViewModel.openVideo(it.id,navigator) }
                    ),
                    MediaFileButton(
                        icon = R.drawable.baseline_delete_24,
                        onClick = { videoStorageListViewModel.removeFile(it.id) }
                    ),
                    MediaFileButton(
                        icon = R.drawable.remame,
                        onClick = { videoStorageListViewModel.showRenameDialog(it) }
                    ),
                    MediaFileButton(
                        icon = R.drawable.export,
                        onClick = { videoStorageListViewModel.sendExportPathRequest(
                            videoId = it.id,
                            activityResultLauncher = selectExportPathContract
                        ) }
                    )
                )

                MediaFileItem(
                    duration = it.duration,
                    created = it.created,
                    name = it.name ?: "",
                    buttons = buttons
                )
            }
        }
    }

    if(dialogState.renameDialogState is RenameDialogState.Showed) {
        val state = dialogState.renameDialogState as RenameDialogState.Showed

        RenameDialog(
            initialName = state.initialName,
            onDismiss = videoStorageListViewModel::hideRenameDialog,
            onRenamed = { videoStorageListViewModel.changeFileName(state.videoId,it) }
        )
    }

    if(dialogState.isExportLoadingDialogVisible) {
        ExportLoadingDialog()
    }
}

@Composable
fun ExportLoadingDialog() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false,dismissOnClickOutside = false)
    ) {
        StyleCard(Modifier.fillMaxWidth().padding(10.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator()

                Text(
                    text = "Пожалуйста, подождите",
                    style = themeTypography.body,
                    color = themeColors.primaryFontColor
                )
            }
        }
    }
}