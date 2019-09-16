package com.gigabytedevelopersinc.apps.sonshub.players.music.extensions

import android.content.Context
import android.util.DisplayMetrics.DENSITY_DEFAULT

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
 * Time: 4:25 AM
 * Desc: FloatExt
 **/

fun Float.dpToPixels(context: Context): Int {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return (this * (metrics.densityDpi.toFloat() / DENSITY_DEFAULT)).toInt()
}
