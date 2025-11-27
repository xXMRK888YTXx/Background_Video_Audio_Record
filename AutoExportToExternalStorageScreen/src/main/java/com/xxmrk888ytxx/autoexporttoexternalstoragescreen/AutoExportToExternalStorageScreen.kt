package com.xxmrk888ytxx.autoexporttoexternalstoragescreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.xxmrk888ytxx.autoexporttoexternalstoragescreen.activityContract.ChooseExternalStorageFolderContract
import com.xxmrk888ytxx.corecompose.LocalNavigator
import com.xxmrk888ytxx.corecompose.TopBar
import com.xxmrk888ytxx.settingsscreen.SettingsCategory
import com.xxmrk888ytxx.settingsscreen.models.SettingsParamType
import kotlinx.collections.immutable.persistentListOf

@Composable
fun AutoExportToExternalStorageScreen(autoExportToExternalStorageViewModel: AutoExportToExternalStorageViewModel) {

    val screenState by autoExportToExternalStorageViewModel.screenState.collectAsState()

    val navigator = LocalNavigator.current

    val contract = rememberLauncherForActivityResult(
        ChooseExternalStorageFolderContract(LocalContext.current)
    ) { folderUri ->
        autoExportToExternalStorageViewModel.onExportFolderSelect(folderUri)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.Transparent,
        topBar = {
            TopBar { navigator.backScreen() }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                SettingsCategory(
                    categoryName = null,
                    settingsParams = persistentListOf(

                        SettingsParamType.Switch(
                            text = stringResource(R.string.export),
                            icon = R.drawable.file_export,
                            isSwitched = screenState.isExportEnabled,
                            isVisible = screenState.isExportFolderSelected,
                            onStateChanged = { autoExportToExternalStorageViewModel.switchExportState() }
                        ),
                        SettingsParamType.Button(
                            stringResource(R.string.select_a_folder_to_export),
                            icon = R.drawable.folder,
                            onClick = { contract.launch(Unit) }
                        )
                    )
                )
            }
        }
    }
}