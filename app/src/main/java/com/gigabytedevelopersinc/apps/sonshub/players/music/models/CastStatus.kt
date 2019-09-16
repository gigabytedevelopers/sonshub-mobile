package com.gigabytedevelopersinc.apps.sonshub.players.music.models

import com.google.android.gms.cast.MediaMetadata.KEY_ARTIST
import com.google.android.gms.cast.MediaMetadata.KEY_TITLE
import com.google.android.gms.cast.MediaMetadata.KEY_ALBUM_TITLE
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.gigabytedevelopersinc.apps.sonshub.players.music.cast.CastHelper.CAST_MUSIC_METADATA_ALBUM_ID
import com.gigabytedevelopersinc.apps.sonshub.players.music.cast.CastHelper.CAST_MUSIC_METADATA_ID

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 09 Feb, 2019
 * Time: 7:39 PM
 * Desc: CastStatus
 **/

data class CastStatus(
    var isCasting: Boolean = false,
    var castDeviceName: String = "",
    var castSongId: Int = -1,
    var castAlbumId: Int = -1,
    var castSongTitle: String = "",
    var castSongAlbum: String = "",
    var castSongArtist: String = "",
    var state: Int = STATUS_NONE
) {

    companion object {
        const val STATUS_NONE = -1
        const val STATUS_PLAYING = 0
        const val STATUS_PAUSED = 1
        const val STATUS_BUFFERING = 2
    }

    fun fromRemoteMediaClient(deviceName: String, remoteMediaClient: RemoteMediaClient?): CastStatus {
        remoteMediaClient ?: return this.apply { isCasting = false }

        castDeviceName = deviceName

        state = when {
            remoteMediaClient.isBuffering -> STATUS_BUFFERING
            remoteMediaClient.isPlaying -> STATUS_PLAYING
            remoteMediaClient.isPaused -> STATUS_PAUSED
            else -> STATUS_NONE
        }

        remoteMediaClient.currentItem?.media?.metadata?.let {
            castSongTitle = it.getString(KEY_TITLE)
            castSongArtist = it.getString(KEY_ARTIST)
            castSongAlbum = it.getString(KEY_ALBUM_TITLE)
            castSongId = it.getInt(CAST_MUSIC_METADATA_ID)
            castAlbumId = it.getInt(CAST_MUSIC_METADATA_ALBUM_ID)
        }

        isCasting = true
        return this
    }
}
