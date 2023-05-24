package com.xxmrk888ytxx.corecompose.Shared

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.xxmrk888ytxx.corecompose.R
import com.xxmrk888ytxx.corecompose.themeColors

@Composable
fun AgreeDialog(
    openPrivacyPolicySite:() -> Unit,
    openTermsOfUseSite:() -> Unit,
    onConfirm:() -> Unit,
    onCancel:() -> Unit
) {
    val isPrivacyPolicyAgreed = rememberSaveable() {
        mutableStateOf(false)
    }

    val isTermsOfUseAgreed = rememberSaveable() {
        mutableStateOf(false)
    }



    YesNoDialog(
        confirmButtonText = stringResource(R.string.I_accept),
        cancelButtonText = stringResource(R.string.I_dont_accept),
        onCancel = onCancel,
        onConfirm = onConfirm,
        isConfirmButtonEnabled = isPrivacyPolicyAgreed.value && isTermsOfUseAgreed.value,
        dialogProperties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isTermsOfUseAgreed.value,
                onCheckedChange = { isTermsOfUseAgreed.value = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = themeColors.primaryColor,
                    uncheckedColor = themeColors.primaryColor
                )
            )

            Spacer(Modifier.width(10.dp))

            Column() {
                Text(
                    text = stringResource(R.string.I_agree),
                    modifier = Modifier,
                    fontSize = 17.sp,
                    color = themeColors.primaryFontColor,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.Terms_of_Use),
                    modifier = Modifier.clickable {
                        openTermsOfUseSite()
                    },
                    fontSize = 17.sp,
                    color = themeColors.primaryColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.width(5.dp))

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isPrivacyPolicyAgreed.value,
                onCheckedChange = {
                    isPrivacyPolicyAgreed.value = it
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = themeColors.primaryColor,
                    uncheckedColor = themeColors.primaryColor
                )
            )

            Spacer(Modifier.width(10.dp))

            Column() {
                Text(
                    text = stringResource(R.string.I_agree),
                    modifier = Modifier,
                    fontSize = 17.sp,
                    color = themeColors.primaryFontColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.Privacy_policy),
                    modifier = Modifier.clickable {
                        openPrivacyPolicySite()
                    },
                    fontSize = 17.sp,
                    color = themeColors.primaryColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }
}