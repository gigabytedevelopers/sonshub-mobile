package com.gigabytedevelopersinc.apps.sonshub.players.music

import android.app.Application
import android.content.ComponentName
import android.content.ContentResolver
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.MediaSessionConnection
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.RealMediaSessionConnection
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService
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
 * Date: 06 Feb, 2019
 * Time: 9:58 PM
 * Desc: MainModule
 **/

val mainModule = module {

    factory<ContentResolver> {
        get<Application>().contentResolver
    }

    single {
        val component = ComponentName(get(), TimberMusicService::class.java)
        RealMediaSessionConnection(get(), component)
    } bind MediaSessionConnection::class
}
