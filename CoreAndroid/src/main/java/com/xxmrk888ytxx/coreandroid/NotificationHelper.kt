package com.xxmrk888ytxx.coreandroid

import android.app.Notification
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

object NotificationHelper {

    fun createNotificationIconBitmap(context: Context, @DrawableRes drawableRes: Int, sizeDp: Int = 48): Bitmap? {
        return try {
            val drawable: Drawable = ContextCompat.getDrawable(context, drawableRes) ?: return null
            val density = context.resources.displayMetrics.density
            val sizePx = (sizeDp * density).toInt().coerceAtLeast(32)

            if (drawable is BitmapDrawable && drawable.bitmap != null) {
                return Bitmap.createScaledBitmap(drawable.bitmap, sizePx, sizePx, true)
            }

            val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (_: Exception) {
            null
        }
    }

    fun applyNotificationIcon(
        builder: Notification.Builder,
        context: Context,
        @DrawableRes iconRes: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setSmallIcon(Icon.createWithResource(context, iconRes))
        } else {
            builder.setSmallIcon(iconRes)
        }

        // Set large icon so OEM ROMs (MIUI/HyperOS, Samsung OneUI, EMUI, ColorOS)
        // do not fallback to the app's camera launcher icon in the notification drawer!
        val bitmap = createNotificationIconBitmap(context, iconRes)
        if (bitmap != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                builder.setLargeIcon(Icon.createWithBitmap(bitmap))
            } else {
                @Suppress("DEPRECATION")
                builder.setLargeIcon(bitmap)
            }
        }
    }
}
