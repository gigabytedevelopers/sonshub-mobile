@file:Suppress("unused")

package com.gigabytedevelopersinc.apps.sonshub.players.music.cast

import android.content.Context
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import com.google.android.gms.cast.framework.media.CastMediaOptions
import com.google.android.gms.cast.framework.media.MediaIntentReceiver.ACTION_STOP_CASTING
import com.google.android.gms.cast.framework.media.MediaIntentReceiver.ACTION_TOGGLE_PLAYBACK
import com.google.android.gms.cast.framework.media.NotificationOptions
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.activities.ExpandedControlsActivity

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 06 Feb, 2019
 * Time: 11:58 PM
 * Desc: CastOptionsProvider
 **/

class CastOptionsProvider : OptionsProvider {

    override fun getCastOptions(context: Context): CastOptions {
        val buttonActions = arrayListOf(ACTION_TOGGLE_PLAYBACK, ACTION_STOP_CASTING)
        val compatButtonActionsIndicies = intArrayOf(0, 1)

        val notificationOptions = NotificationOptions.Builder().apply {
            setActions(buttonActions, compatButtonActionsIndicies)
            setTargetActivityClassName(ExpandedControlsActivity::class.java.name)
        }.build()

        val mediaOptions = CastMediaOptions.Builder().apply {
            setNotificationOptions(notificationOptions)
            setExpandedControllerActivityClassName(ExpandedControlsActivity::class.java.name)
        }.build()

        return CastOptions.Builder().apply {
            setReceiverApplicationId(context.getString(R.string.cast_app_id))
            setCastMediaOptions(mediaOptions)
        }.build()
    }

    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? = null
}
