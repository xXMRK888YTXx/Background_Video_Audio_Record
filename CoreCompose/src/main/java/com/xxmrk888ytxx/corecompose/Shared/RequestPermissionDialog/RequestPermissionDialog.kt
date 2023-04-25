package com.xxmrk888ytxx.corecompose.Shared.RequestPermissionDialog

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.xxmrk888ytxx.corecompose.Shared.GradientButton
import com.xxmrk888ytxx.corecompose.Shared.StyleCard
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.corecompose.themeGradients
import com.xxmrk888ytxx.corecompose.themeShapes
import com.xxmrk888ytxx.corecompose.themeTypography

@SuppressLint("ResourceType")
@Composable
fun RequestPermissionDialog(permissions:List<RequestedPermissionModel>, onDismissRequest:() -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        StyleCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(themeDimensions.cardOutPaddings),
        ) {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .padding(themeDimensions.cardInPaddings)
            ) {
                items(permissions) {
                    Text(
                        text = stringResource(it.description),
                        style = themeTypography.permissionDescription,
                        color = themeColors.primaryFontColor,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    GradientButton(
                        backgroundGradient = if (it.isGranted) themeGradients.disablePermissionButtonGradient
                        else themeGradients.enablePermissionButtonGradient,
                        shape = themeShapes.permissionDialog,
                        onClick = it.onRequest,
                        enabled = !it.isGranted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)

                    ) {
                        Row(
                            Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (it.isGranted) "Предоставлено"
                                else "Предоставить",
                                style = themeTypography.head,
                                color = themeColors.primaryFontColor
                            )

                        }
                    }
                }
            }
        }
    }
}