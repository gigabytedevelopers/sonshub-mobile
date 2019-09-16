package com.gigabytedevelopersinc.apps.sonshub.players.music.logging

import com.crashlytics.android.Crashlytics
import timber.log.Timber

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
 * Time: 12:17 PM
 * Desc: FabricTree
 **/

class FabricTree : Timber.Tree() {

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        try {
            if (t != null) {
                Crashlytics.setString("crash_tag", tag)
                Crashlytics.logException(t)
            } else {
                Crashlytics.log(priority, tag, message)
            }
        } catch (e: IllegalStateException) {
            // TODO this is caught so that Robolelectric tests which test classes that make use of Timber don't crash.
            // TODO they crash because Robolectric initializes the app and this tree in release configurations,
            // TODO and calls to Timber logging ends up here.
            // TODO we should maybe somehow avoid adding this tree to Timber in the context of Robolectric tests,
            // TODO somehow?
            e.printStackTrace()
        }
    }
}
