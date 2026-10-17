package com.xxmrk888ytxx.corecompose.Shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(items) {
                        val isSelected = it.isSelected
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) themeColors.primaryColor.copy(alpha = 0.15f) else Color.Transparent
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) themeColors.primaryColor.copy(alpha = 0.5f) else Color(0xFF263346),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable(onClick = it.onClick)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = it.onClick,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = themeColors.primaryColor,
                                    unselectedColor = themeColors.secondFontColor
                                )
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = it.title,
                                color = if (isSelected) Color.White else themeColors.primaryFontColor,
                                style = themeTypography.selectDialog.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            )
                        }
                    }

                    additionalContent?.let {
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
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