package com.gigabytedevelopersinc.apps.sonshub.players.music.notifications

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.systemService
import org.koin.dsl.module.module

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 10 Feb, 2019
 * Time: 7:10 AM
 * Desc: NotificationsModule
 **/

val notificationModule = module {

    factory<NotificationManager> {
        get<Application>().systemService(Context.NOTIFICATION_SERVICE)
    }

    single {
        RealNotifications(get(), get())
    } bind Notifications::class
}
