package com.gigabytedevelopersinc.apps.sonshub.players.music.constants

import android.provider.MediaStore

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
 * Time: 3:40 PM
 * Desc: SongSortOrder
 **/

enum class SongSortOrder(val rawValue: String) {
    /* Song sort order A-Z */
    SONG_A_Z(MediaStore.Audio.Media.DEFAULT_SORT_ORDER),
    /* Song sort order Z-A */
    SONG_Z_A(MediaStore.Audio.Media.DEFAULT_SORT_ORDER + " DESC"),
    /* Song sort order year */
    SONG_YEAR(MediaStore.Audio.Media.YEAR + " DESC"),
    /* Song sort order duration */
    SONG_DURATION(MediaStore.Audio.Media.DURATION + " DESC");

    companion object {
        fun fromString(raw: String): SongSortOrder {
            return SongSortOrder.values().single { it.rawValue == raw }
        }

        fun toString(value: SongSortOrder): String = value.rawValue
    }
}
