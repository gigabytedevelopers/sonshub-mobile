package com.gigabytedevelopersinc.apps.sonshub.players.music

import android.app.Application
import android.os.Environment.DIRECTORY_MUSIC
import android.os.Environment.getExternalStoragePublicDirectory
import com.afollestad.rxkprefs.RxkPrefs
import com.afollestad.rxkprefs.rxkPrefs
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.AlbumSortOrder
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.AlbumSortOrder.ALBUM_A_Z
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.SongSortOrder
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.SongSortOrder.SONG_A_Z
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.StartPage
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.StartPage.SONGS
import org.koin.core.qualifier.named
import org.koin.dsl.module

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
 * Time: 9:18 PM
 * Desc: PrefsModule
 **/

const val PREF_SONG_SORT_ORDER = "song_sort_order"
const val PREF_ALBUM_SORT_ORDER = "album_sort_order"
const val PREF_START_PAGE = "start_page_preference"
const val PREF_LAST_FOLDER = "last_folder"

val prefsModule = module {
    single { rxkPrefs(get<Application>()) }

    factory(named(PREF_SONG_SORT_ORDER)) {
        get<RxkPrefs>().enum(PREF_SONG_SORT_ORDER, SONG_A_Z,
                SongSortOrder.Companion::fromString, SongSortOrder.Companion::toString)
    }

    factory(named(PREF_ALBUM_SORT_ORDER)) {
        get<RxkPrefs>().enum(PREF_ALBUM_SORT_ORDER, ALBUM_A_Z,
                AlbumSortOrder.Companion::fromString, AlbumSortOrder.Companion::toString)
    }

    factory(named(PREF_START_PAGE)) {
        get<RxkPrefs>().enum(PREF_START_PAGE, SONGS,
                StartPage.Companion::fromString, StartPage.Companion::toString)
    }

    factory(named(PREF_LAST_FOLDER)) {
        val defaultFolder = getExternalStoragePublicDirectory(DIRECTORY_MUSIC).path
        get<RxkPrefs>().string(PREF_LAST_FOLDER, defaultFolder)
    }
}
