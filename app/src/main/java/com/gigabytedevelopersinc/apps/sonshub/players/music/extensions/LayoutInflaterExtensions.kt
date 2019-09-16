@file:Suppress("UNCHECKED_CAST")

package com.gigabytedevelopersinc.apps.sonshub.players.music.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

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
 * Time: 4:40 AM
 * Desc: LayoutInflaterExtensions
 **/

fun <T : View> LayoutInflater.inflateTo(
    @LayoutRes layoutRes: Int,
    parent: ViewGroup? = null,
    attachToRoot: Boolean = false
): T = inflate(layoutRes, parent, attachToRoot) as T

fun <T : ViewDataBinding> LayoutInflater.inflateWithBinding(
    @LayoutRes layoutRes: Int,
    parent: ViewGroup?,
    attachToRoot: Boolean = false
): T {
    return DataBindingUtil.inflate(this, layoutRes, parent, attachToRoot) as T
}
