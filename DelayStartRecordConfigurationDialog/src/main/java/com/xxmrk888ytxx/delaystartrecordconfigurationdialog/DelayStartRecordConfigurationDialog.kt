package com.xxmrk888ytxx.delaystartrecordconfigurationdialog

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.xxmrk888ytxx.corecompose.Shared.StyleCard
import com.xxmrk888ytxx.corecompose.Shared.YesNoButtons
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.corecompose.themeShapes
import com.xxmrk888ytxx.corecompose.themeTypography
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun DelayStartRecordConfigurationDialog(
    title:String,
    onDismiss:() -> Unit,
    onDelayTimePicked:(Long) -> Unit,
    onInvalidInput:(Long) -> Unit
) {

    var currentSelectedDate by rememberSaveable() {
        mutableStateOf(LocalDate.MIN)
    }

    var currentSelectedTime:LocalTime? by rememberSaveable() {
        mutableStateOf(null)
    }

    val timePickerState = rememberMaterialDialogState()

    val datePickerState = rememberMaterialDialogState()


    Dialog(onDismissRequest = onDismiss) {
        StyleCard(
            Modifier
                .fillMaxWidth()
                .padding(themeDimensions.cardOutPaddings)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(themeDimensions.cardInPaddings),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp, alignment = Alignment.CenterVertically)
            ) {
                Text(
                    text = title,
                    style = themeTypography.body,
                    color = themeColors.primaryFontColor,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if(currentSelectedDate == LocalDate.MIN)
                        stringResource(R.string.Date_not_setuped)
                    else
                        "${stringResource(R.string.Seleted_date)}:${currentSelectedDate
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}"  ,
                    style = themeTypography.body,
                    color = themeColors.primaryFontColor
                )

                SelectTimeButton(text = stringResource(R.string.Select_date)) {
                    datePickerState.show()
                }

                Text(
                    text = if(currentSelectedTime == null)
                        stringResource(R.string.Time_not_setuped)
                    else
                        "${stringResource(R.string.Selected_time)} :${currentSelectedTime?.toTimeString()}",
                    style = themeTypography.body,
                    color = themeColors.primaryFontColor
                )

                SelectTimeButton(text = stringResource(R.string.Select_time)) {
                    timePickerState.show()
                }


                YesNoButtons(
                    modifier = Modifier.fillMaxWidth(),
                    onNoButtonClick = onDismiss,
                    isYesButtonEnabled = currentSelectedTime != null && currentSelectedDate != LocalDate.MIN ,
                    onYesButtonClick = {
                        val localDateTime = LocalDateTime.of(currentSelectedDate,currentSelectedTime)
                        val milliSeconds = localDateTime
                            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                        if(System.currentTimeMillis() >= milliSeconds) {
                            onInvalidInput(milliSeconds)
                        } else
                            onDelayTimePicked(milliSeconds)
                    }
                )
            }
        }
    }

    TimePickDialog(
        state = timePickerState,
        title = title,
        onTimePicked = {
            currentSelectedTime = it
        }
    )

    DatePickDialog(
        state = datePickerState,
        title = title,
        onDatePicked = {
            currentSelectedDate = it
        }
    )
}

@Composable
internal fun SelectTimeButton(
    text:String,
    onClick:() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),
        shape = themeShapes.outlineButtonShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = themeColors.cancelButtonColor,
        )
    ) {
        Text(
            text = text,
            maxLines = 1,
            style = themeTypography.body,
            color = themeColors.primaryFontColor
        )
    }
}

@Composable
internal fun TimePickDialog(
    state: MaterialDialogState,
    title:String,

    onTimePicked:(LocalTime) -> Unit
) {
    MaterialDialog(
        dialogState = state,
        backgroundColor = themeColors.cardColor,
        shape = RoundedCornerShape(10.dp),
        buttons = {
            positiveButton(stringResource(R.string.Ok))
            negativeButton(stringResource(R.string.Cancel))
        },

    ) {
        timepicker(
            title = title,
            is24HourClock = true,
            colors = TimePickerDefaults.colors(
                headerTextColor = themeColors.primaryFontColor,
                inactiveTextColor = themeColors.primaryFontColor
            ),
            timeRange = LocalTime.now().plusMinutes(1)..LocalTime.MAX,
            onTimeChange = onTimePicked
        )
    }
}

@Composable
internal fun DatePickDialog(
    state: MaterialDialogState,
    title:String,
    onDatePicked:(LocalDate) -> Unit
) {
    MaterialDialog(
        dialogState = state,
        backgroundColor = themeColors.cardColor,
        shape = RoundedCornerShape(10.dp),
        buttons = {
            positiveButton(stringResource(R.string.Ok))
            negativeButton(stringResource(R.string.Cancel))
        }
    ) {

        datepicker(
            title = title,
            yearRange = LocalDate.now().year..2100,
            allowedDateValidator = { time ->
                LocalDate.now() <= time
            },
            colors = DatePickerDefaults.colors(
                headerTextColor = themeColors.primaryFontColor,
                dateInactiveTextColor = themeColors.primaryFontColor
            ),
            onDateChange = onDatePicked
        )
    }
}
