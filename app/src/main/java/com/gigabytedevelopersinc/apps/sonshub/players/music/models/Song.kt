package com.gigabytedevelopersinc.apps.sonshub.players.music.models

import android.database.Cursor
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.ALBUM
import android.provider.MediaStore.Audio.Media.ALBUM_ID
import android.provider.MediaStore.Audio.Media.ARTIST
import android.provider.MediaStore.Audio.Media.ARTIST_ID
import android.provider.MediaStore.Audio.Media.DURATION
import android.provider.MediaStore.Audio.Media.TITLE
import android.provider.MediaStore.Audio.Media.TRACK
import android.provider.MediaStore.Audio.Media._ID
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_SONG
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.value
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.valueOrDefault
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.valueOrEmpty
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.Utils.getAlbumArtUri
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
 * Time: 5:15 PM
 * Desc: Song
 **/

@Parcelize
data class Song(
    var id: Long = 0,
    var albumId: Long = 0,
    var artistId: Long = 0,
    var title: String = "",
    var artist: String = "",
    var album: String = "",
    var duration: Int = 0,
    var trackNumber: Int = 0
) : MediaBrowserCompat.MediaItem(
        MediaDescriptionCompat.Builder()
                .setMediaId(MediaID("$TYPE_SONG", "$id").asString())
                .setTitle(title)
                .setIconUri(getAlbumArtUri(albumId))
                .setSubtitle(artist)
                .build(), FLAG_PLAYABLE) {
    companion object {
        fun fromCursor(cursor: Cursor, albumId: Long = -1, artistId: Long = -1): Song {
            return Song(
                    id = cursor.value(_ID),
                    albumId = cursor.valueOrDefault(ALBUM_ID, albumId),
                    artistId = cursor.valueOrDefault(ARTIST_ID, artistId),
                    title = cursor.valueOrEmpty(TITLE),
                    artist = cursor.valueOrEmpty(ARTIST),
                    album = cursor.valueOrEmpty(ALBUM),
                    duration = cursor.value(DURATION),
                    trackNumber = cursor.value<Int>(TRACK).normalizeTrackNumber()
            )
        }

        fun fromPlaylistMembersCursor(cursor: Cursor): Song {
            return Song(
                    id = cursor.value(MediaStore.Audio.Playlists.Members.AUDIO_ID),
                    albumId = cursor.value(MediaStore.Audio.AudioColumns.ALBUM_ID),
                    artistId = cursor.value(MediaStore.Audio.AudioColumns.ARTIST_ID),
                    title = cursor.valueOrEmpty(MediaStore.Audio.AudioColumns.TITLE),
                    artist = cursor.valueOrEmpty(MediaStore.Audio.AudioColumns.ARTIST),
                    album = cursor.valueOrEmpty(MediaStore.Audio.AudioColumns.ALBUM),
                    duration = (cursor.value<Long>(MediaStore.Audio.AudioColumns.DURATION) / 1000).toInt(),
                    trackNumber = cursor.value<Int>(MediaStore.Audio.AudioColumns.TRACK).normalizeTrackNumber()
            )
        }
    }
}

private fun Int.normalizeTrackNumber(): Int {
    var returnValue = this
    // This fixes bug where some track numbers displayed as 100 or 200.
    while (returnValue >= 1000) {
        // When error occurs the track numbers have an extra 1000 or 2000 added, so decrease till normal.
        returnValue -= 1000
    }
    return returnValue
}
