package com.xxmrk888ytxx.corecompose.Shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.xxmrk888ytxx.corecompose.R
import com.xxmrk888ytxx.corecompose.Shared.models.SelectDialogModel
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.corecompose.themeTypography
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SelectDialog(
    confirmButtonText:String = stringResource(R.string.Ok),
    cancelButtonText:String = stringResource(R.string.Cancel),
    isConfirmButtonEnabled:Boolean = true,
    onConfirm:() -> Unit,
    onCancel:() -> Unit,
    items:ImmutableList<SelectDialogModel>,
    additionalContent:@Composable (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onCancel) {
        StyleCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(themeDimensions.cardOutPaddings)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(themeDimensions.cardInPaddings)
            ) {
                LazyColumn(Modifier.fillMaxWidth()) {
                    items(items) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable(onClick = it.onClick),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = it.isSelected,
                                onClick = it.onClick,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = themeColors.yesButtonColor,
                                    unselectedColor = themeColors.yesButtonColor.copy(0.5f)
                                )
                            )

                            Spacer(modifier =Modifier.width(15.dp))

                            Text(
                                text = it.title,
                                color = themeColors.primaryFontColor,
                                style = themeTypography.selectDialog
                            )
                        }
                    }

                    additionalContent?.let {
                        item {
                            it()
                        }
                    }
                }

                YesNoButtons(
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                    yesButtonText = confirmButtonText,
                    noButtonText = cancelButtonText,
                    onNoButtonClick = onCancel,
                    onYesButtonClick = onConfirm,
                    isYesButtonEnabled = isConfirmButtonEnabled
                )
            }
        }
    }
}