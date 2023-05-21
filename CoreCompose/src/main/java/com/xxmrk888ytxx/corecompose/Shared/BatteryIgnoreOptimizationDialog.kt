package com.xxmrk888ytxx.corecompose.Shared

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.xxmrk888ytxx.corecompose.R
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeTypography

@Composable
fun BatteryIgnoreOptimizationDialog(
    onHide:(Boolean) -> Unit,
    onConfirm:(Boolean) -> Unit
) {
    var notShowInFuture by rememberSaveable {
        mutableStateOf(false)
    }



    YesNoDialog(
        onCancel = { onHide(notShowInFuture) },
        onConfirm = { onConfirm(notShowInFuture) }
    ) {
        Text(
            text = stringResource(R.string.Ignore_battery_dialog_text),
            color = themeColors.primaryFontColor,
            style = themeTypography.yesNoDialog,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = notShowInFuture,
                onCheckedChange = { notShowInFuture = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = themeColors.primaryColor,
                    uncheckedColor = themeColors.primaryColor
                )
            )

            Text(
                text = stringResource(R.string.Dont_show_any_more),
                color = themeColors.primaryFontColor,
                style = themeTypography.yesNoDialog,
            )
        }
    }
}