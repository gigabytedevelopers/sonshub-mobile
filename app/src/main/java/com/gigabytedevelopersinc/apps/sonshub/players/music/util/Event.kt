package com.gigabytedevelopersinc.apps.sonshub.players.music.util

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 13 Feb, 2019
 * Time: 7:13 AM
 * Desc: Event
 *
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 *
 * For more information, see:
 * https://medium.com/google-developers/livedata-with-events-ac2622673150
 **/

class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}
