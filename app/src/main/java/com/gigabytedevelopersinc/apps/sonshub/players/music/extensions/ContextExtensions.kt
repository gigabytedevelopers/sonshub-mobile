package com.gigabytedevelopersinc.apps.sonshub.players.music.extensions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.drawable.Drawable
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 07 Feb, 2019
 * Time: 4:18 AM
 * Desc: ContextExtensions
 **/

private var toast: Toast? = null

fun Context?.toast(message: String) {
    if (this == null) {
        return
    }
    toast?.cancel()
    toast = Toast.makeText(this, message, LENGTH_SHORT)
            .apply {
                show()
            }
}

fun Context?.toast(@StringRes message: Int) {
    if (this == null) {
        return
    }
    toast?.cancel()
    toast = Toast.makeText(this, message, LENGTH_SHORT)
            .apply {
                show()
            }
}

fun Fragment.drawable(@DrawableRes res: Int): Drawable? {
    val context = activity ?: return null
    return context.drawable(res)
}

fun Activity?.drawable(@DrawableRes res: Int): Drawable? {
    if (this == null) {
        return null
    }
    return ContextCompat.getDrawable(this, res)
}

@Suppress("UNCHECKED_CAST")
fun <T> Context.systemService(name: String): T {
    return getSystemService(name) as T
}

fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
}
