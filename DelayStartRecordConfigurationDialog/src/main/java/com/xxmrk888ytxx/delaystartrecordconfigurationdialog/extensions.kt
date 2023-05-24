package com.xxmrk888ytxx.delaystartrecordconfigurationdialog

import java.time.LocalTime

fun LocalTime.toTimeString() : String {
    val hour = hour
    val minute = minute

    return buildString {
        append(if(hour < 10) "0$hour" else hour.toString())
        append(":")
        append(if(minute < 10) "0$minute" else minute.toString())
    }
}