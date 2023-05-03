package com.xxmrk888ytxx.corecompose.Shared

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.corecompose.R
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeShapes
import com.xxmrk888ytxx.corecompose.themeTypography

@Composable
fun YesNoButtons(
    modifier: Modifier,
    yesButtonText:String = stringResource(R.string.Ok),
    noButtonText:String = stringResource(R.string.Cancel),
    isYesButtonEnabled:Boolean = true,
    onNoButtonClick:() -> Unit,
    onYesButtonClick:() -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        OutlinedButton(
            onClick = onNoButtonClick,
            modifier = Modifier
                .weight(1f)
                .padding(start = 5.dp, end = 5.dp),
            shape = themeShapes.outlineButtonShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = themeColors.cancelButtonColor,
            )
        ) {
            Text(
                text = noButtonText,
                maxLines = 1,
                style = themeTypography.body,
                color = themeColors.primaryFontColor
            )
        }


        OutlinedButton(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = themeColors.yesButtonColor,
                disabledBackgroundColor = themeColors.yesButtonColor.copy(0.5f)
            ),
            onClick = onYesButtonClick,
            modifier = Modifier
                .weight(1f)
                .padding(start = 5.dp, end = 5.dp),
            shape = themeShapes.outlineButtonShape,
            enabled = isYesButtonEnabled
        ) {
            Text(
                text = yesButtonText,
                maxLines = 1,
                style = themeTypography.body,
                color = themeColors.primaryFontColor
            )
        }
    }
}