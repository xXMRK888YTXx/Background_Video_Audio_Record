package com.xxmrk888ytxx.storagescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.xxmrk888ytxx.corecompose.Shared.StyleCard
import com.xxmrk888ytxx.corecompose.Shared.YesNoButtons
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeTypography

@Composable
fun RenameDialog(
    initialName:String,
    onDismiss:() -> Unit,
    onRenamed:(String) -> Unit
) {
    var name by rememberSaveable() {
        mutableStateOf(initialName)
    }

    Dialog(onDismissRequest = onDismiss) {
        StyleCard(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp,Alignment.CenterVertically)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    label = {
                        Text(
                            text = stringResource(R.string.New_file_name),
                            style = themeTypography.body.copy(fontSize = 15.sp),
                            color = themeColors.primaryFontColor
                        )
                    },
                    textStyle = themeTypography.body.copy(color = themeColors.primaryFontColor)
                )

                YesNoButtons(
                    modifier = Modifier.fillMaxWidth(),
                    onNoButtonClick = onDismiss,
                    onYesButtonClick = { onRenamed(name) }
                )
            }
        }
    }
}