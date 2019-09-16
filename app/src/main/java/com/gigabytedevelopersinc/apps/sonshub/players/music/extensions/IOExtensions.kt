package com.gigabytedevelopersinc.apps.sonshub.players.music.extensions

import java.io.Closeable

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
 * Time: 4:37 AM
 * Desc: IOExtensions
 **/

fun Closeable?.closeQuietly() {
    try {
        this?.close()
    } catch (_: Throwable) {
    }
}
