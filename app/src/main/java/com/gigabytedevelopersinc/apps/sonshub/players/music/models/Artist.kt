package com.gigabytedevelopersinc.apps.sonshub.players.music.models

import android.database.Cursor
import android.provider.MediaStore.Audio.Artists.ARTIST
import android.provider.MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
import android.provider.MediaStore.Audio.Artists.NUMBER_OF_TRACKS
import android.provider.MediaStore.Audio.Artists._ID
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ARTIST
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.value
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
 * Time: 12:09 AM
 * Desc: Artist
 **/

@Parcelize
data class Artist(
    var id: Long = 0,
    var name: String = "",
    var songCount: Int = 0,
    var albumCount: Int = 0
) : MediaBrowserCompat.MediaItem(
        MediaDescriptionCompat.Builder()
                .setMediaId(MediaID(TYPE_ARTIST.toString(), id.toString()).asString())
                .setTitle(name)
                .setSubtitle("$albumCount albums")
                .build(), FLAG_BROWSABLE) {
    companion object {
        fun fromCursor(cursor: Cursor): Artist {
            return Artist(
                    id = cursor.value(_ID),
                    name = cursor.value(ARTIST),
                    songCount = cursor.value(NUMBER_OF_TRACKS),
                    albumCount = cursor.value(NUMBER_OF_ALBUMS)
            )
        }
    }
}
