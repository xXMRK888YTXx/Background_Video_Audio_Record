package com.xxmrk888ytxx.storagescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeTypography

@Composable
fun Stub(
    text:String
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp,Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¯\\(ツ)/¯",
            style = themeTypography.stub,
            color = themeColors.primaryFontColor
        )


        Text(
            text = text,
            style = themeTypography.stub,
            color = themeColors.primaryFontColor
        )
    }
}