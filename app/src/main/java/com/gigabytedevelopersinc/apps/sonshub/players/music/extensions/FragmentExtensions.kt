@file:Suppress("UNCHECKED_CAST")

package com.gigabytedevelopersinc.apps.sonshub.players.music.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

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
 * Time: 4:30 AM
 * Desc: FragmentExtensions
 **/

inline val Fragment.safeActivity: FragmentActivity
    get() = activity ?: throw IllegalStateException("Fragment not attached")

fun <T> Fragment.argument(name: String): T {
    return arguments?.get(name) as? T ?: throw IllegalStateException("Argument $name not found.")
}

fun <T> Fragment.argumentOr(name: String, default: T): T {
    return arguments?.get(name) as? T ?: default
}

fun Fragment.argumentOrEmpty(name: String): String {
    return arguments?.get(name) as? String ?: ""
}
