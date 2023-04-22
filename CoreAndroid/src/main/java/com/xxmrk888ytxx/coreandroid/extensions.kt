package com.xxmrk888ytxx.coreandroid

fun Long.milliSecondToString() : String {
    val seconds = this / 1000

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val second = seconds % 60

    val stringConverter:(Long) -> String = {
        if(it < 10) "0$it" else it.toString()
    }

    return "${stringConverter(hours)}:${stringConverter(minutes)}:${stringConverter(second)}"
}