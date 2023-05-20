package com.xxmrk888ytxx.coreandroid

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun Long.milliSecondToString(): String {
    if(this < 0) return "00:00:00"
    val seconds = this / 1000

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val second = seconds % 60

    val stringConverter: (Long) -> String = {
        if (it < 10) "0$it" else it.toString()
    }

    return "${stringConverter(hours)}:${stringConverter(minutes)}:${stringConverter(second)}"
}

inline fun Context.buildNotificationChannel(
    id: String,
    name: String,
    configuration: NotificationChannel.() -> Unit = {}
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT).apply(configuration)

        val notificationManager = getSystemService<NotificationManager>()

        notificationManager?.createNotificationChannel(channel)
    }
}

inline fun Context.buildNotification(
    channelId:String,
    configuration:Notification.Builder.() -> Unit
) : Notification {
    val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(this,channelId)
    } else {
        Notification.Builder(this)
    }

    return notificationBuilder.apply(configuration).build()
}

fun CoroutineScope.cancelChillersAndLaunch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    coroutineContext.cancelChildren()

    launch(context, start, block)
}


/**
 * [Ru]
 * Данная функция расширения предназначена для конвертации числа с типом [Long]
 * в строку с датой типа [Day Month Year]
 */

/**
 * [En]
 * This extension function is designed to convert a number with type [Long]
 * to a date string like [Day Month Year]
 */
fun Long.toDateString(context: Context) : String {
    val calendar = Calendar.getInstance()
    calendar.time = Date(this)

    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val dayString = if(day < 10) "0$day" else day.toString()

    val month = calendar.get(Calendar.MONTH)
    val monthString = monthToString(month,context)

    val year = calendar.get(Calendar.YEAR)
    val yearString = year.toString()

    return "$dayString $monthString $yearString"
}

/**
 * [Ru]
 * Данная функция преобразует цифру месяца, в строку с названием месяца
 */

/**
 * [En]
 * This function converts the digit of the month into a string with the name of the month
 */
internal fun monthToString(month:Int,context: Context) : String {
    context.resources.apply {
        return when(month) {
            0 -> getString(R.string.January)
            1 -> getString(R.string.February)
            2 -> getString(R.string.March)
            3 -> getString(R.string.April)
            4 -> getString(R.string.May)
            5 -> getString(R.string.June)
            6 -> getString(R.string.July)
            7 -> getString(R.string.August)
            8 -> getString(R.string.September)
            9 -> getString(R.string.October)
            10 -> getString(R.string.November)
            11 -> getString(R.string.December)
            else -> return ""
        }
    }
}

/**
 * [Ru]
 * Данная функция расширения предназначена для конвертации числа с типом [Long]
 * в строку времени типа [XX:XX:XX]
 */

/**
 * [En]
 * This extension function is designed to convert a number with type [Long]
 * into a time string like [XX:XX:XX]
 */
fun Long.toTimeString() : String {
    val calendar = Calendar.getInstance()
    calendar.time = Date(this)

    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val hoursString = if(hours < 10) "0$hours" else hours.toString()

    val minute = calendar.get(Calendar.MINUTE)
    val minuteString = if(minute < 10) "0$minute" else minute.toString()

    return "$hoursString:$minuteString"
}