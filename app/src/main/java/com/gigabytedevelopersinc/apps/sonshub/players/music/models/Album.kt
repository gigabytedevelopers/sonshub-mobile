package com.gigabytedevelopersinc.apps.sonshub.players.music.models

import android.database.Cursor
import android.provider.MediaStore.Audio.Albums.ALBUM
import android.provider.MediaStore.Audio.Albums.ARTIST
import android.provider.MediaStore.Audio.Albums.FIRST_YEAR
import android.provider.MediaStore.Audio.Albums.NUMBER_OF_SONGS
import android.provider.MediaStore.Audio.Albums._ID
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ALBUM
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.value
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.valueOrEmpty
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.Utils
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
 * Time: 12:03 AM
 * Desc: Album
 **/

@Parcelize
data class Album(
    var id: Long = 0,
    var title: String = "",
    var artist: String = "",
    var artistId: Long = 0,
    var songCount: Int = 0,
    var year: Int = 0
) : MediaBrowserCompat.MediaItem(
        MediaDescriptionCompat.Builder()
                .setMediaId(MediaID(TYPE_ALBUM.toString(), id.toString()).asString())
                .setTitle(title)
                .setIconUri(Utils.getAlbumArtUri(id))
                .setSubtitle(artist)
                .build(), FLAG_BROWSABLE) {

    companion object {
        fun fromCursor(cursor: Cursor, artistId: Long = -1): Album {
            return Album(
                    id = cursor.value(_ID),
                    title = cursor.valueOrEmpty(ALBUM),
                    artist = cursor.valueOrEmpty(ARTIST),
                    artistId = artistId,
                    songCount = cursor.value(NUMBER_OF_SONGS),
                    year = cursor.value(FIRST_YEAR)
            )
        }
    }
}
