package com.xxmrk888ytxx.corecompose.Shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.xxmrk888ytxx.corecompose.R
import com.xxmrk888ytxx.corecompose.themeDimensions

@Composable
fun YesNoDialog(
    confirmButtonText: String = stringResource(id = R.string.Ok),
    cancelButtonText: String = stringResource(id = R.string.Cancel),
    isConfirmButtonEnabled: Boolean = true,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    dialogProperties: DialogProperties = DialogProperties(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onCancel,properties = dialogProperties) {
        StyleCard(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(themeDimensions.cardOutPaddings)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(themeDimensions.cardInPaddings)
            ) {
                content(this)

                YesNoButtons(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    yesButtonText = confirmButtonText,
                    noButtonText = cancelButtonText,
                    isYesButtonEnabled = isConfirmButtonEnabled,
                    onNoButtonClick = onCancel,
                    onYesButtonClick = onConfirm
                )
            }
        }
    }
}