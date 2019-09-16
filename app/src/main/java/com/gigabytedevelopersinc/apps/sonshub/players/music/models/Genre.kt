package com.gigabytedevelopersinc.apps.sonshub.players.music.models

import android.database.Cursor
import android.provider.MediaStore.Audio.Genres.NAME
import android.provider.MediaStore.Audio.Genres._ID
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_GENRE
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.value
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.valueOrEmpty
import kotlinx.android.parcel.Parcelize

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 08 Feb, 2019
 * Time: 1:59 PM
 * Desc: Genre
 **/

@Parcelize
data class Genre(
    val id: Long,
    val name: String,
    val songCount: Int
) : MediaBrowserCompat.MediaItem(
        MediaDescriptionCompat.Builder()
                .setMediaId(MediaID("$TYPE_GENRE", "$id").asString())
                .setTitle(name)
                .setSubtitle("$songCount songs")
                .build(), FLAG_BROWSABLE) {
    companion object {
        fun fromCursor(cursor: Cursor, songCount: Int): Genre {
            return Genre(
                    id = cursor.value(_ID),
                    name = cursor.valueOrEmpty(NAME),
                    songCount = songCount
            )
        }
    }
}
