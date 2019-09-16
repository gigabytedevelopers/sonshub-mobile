package com.gigabytedevelopersinc.apps.sonshub.players.music.cast

import androidx.core.net.toUri
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaInfo.STREAM_TYPE_BUFFERED
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.cast.MediaMetadata.KEY_ALBUM_TITLE
import com.google.android.gms.cast.MediaMetadata.KEY_ARTIST
import com.google.android.gms.cast.MediaMetadata.KEY_TITLE
import com.google.android.gms.cast.MediaMetadata.KEY_TRACK_NUMBER
import com.google.android.gms.cast.MediaMetadata.MEDIA_TYPE_MUSIC_TRACK
import com.google.android.gms.cast.MediaQueueItem
import com.google.android.gms.cast.MediaStatus
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.common.images.WebImage
import com.gigabytedevelopersinc.apps.sonshub.players.music.cast.CastServer.Companion.PARAM_ID
import com.gigabytedevelopersinc.apps.sonshub.players.music.cast.CastServer.Companion.PART_ALBUM_ART
import com.gigabytedevelopersinc.apps.sonshub.players.music.cast.CastServer.Companion.PART_SONG
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Song
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.Utils.getIPAddress
import timber.log.Timber
import java.net.MalformedURLException
import java.net.URL

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
 * Time: 12:58 PM
 * Desc: CastHelper
 * TODO: This should be a part of an injectable cast service
 **/

object CastHelper {
    const val CAST_MUSIC_METADATA_ID = "metadata_id"
    const val CAST_MUSIC_METADATA_ALBUM_ID = "metadata_album_id"

    private const val CAST_URL_PROTOCOL = "http"
    private const val MIME_TYPE_AUDIO_MPEG = "audio/mpeg"

    fun castSong(castSession: CastSession, song: Song) {
        try {
            val remoteMediaClient = castSession.remoteMediaClient
            // TODO replace deprecated usage with correct usage
            remoteMediaClient.load(song.toMediaInfo(), true, 0)
        } catch (e: Exception) {
            Timber.d(e, "castSong failed")
        }
    }

    fun castSongQueue(castSession: CastSession, songs: List<Song>, currentPos: Int) {
        try {
            val remoteMediaClient = castSession.remoteMediaClient
            remoteMediaClient.queueLoad(songs.toQueueInfoList(), currentPos, MediaStatus.REPEAT_MODE_REPEAT_OFF, 0, null)
        } catch (e: Exception) {
            Timber.d(e, "castSongQueue failed")
        }
    }

    private fun List<Song>.toQueueInfoList(): Array<MediaQueueItem> {
        return map { MediaQueueItem.Builder(it.toMediaInfo()).build() }.toTypedArray()
    }

    private fun Song.toMediaInfo(): MediaInfo? {
        val song = this
        val ipAddress = getIPAddress(true)
        val baseUrl: URL

        try {
            baseUrl = URL(CAST_URL_PROTOCOL, ipAddress, CAST_SERVER_PORT, "")
        } catch (e: MalformedURLException) {
            Timber.e(e)
            return null
        }

        val songUrl = "$baseUrl/$PART_SONG?$PARAM_ID=${song.id}"
        val albumArtUrl = "$baseUrl/$PART_ALBUM_ART?$PARAM_ID=${song.albumId}"
        val musicMetadata = MediaMetadata(MEDIA_TYPE_MUSIC_TRACK).apply {
            putInt(CAST_MUSIC_METADATA_ID, song.id.toInt())
            putInt(CAST_MUSIC_METADATA_ALBUM_ID, song.albumId.toInt())
            putString(KEY_TITLE, song.title)
            putString(KEY_ARTIST, song.artist)
            putString(KEY_ALBUM_TITLE, song.album)
            putInt(KEY_TRACK_NUMBER, song.trackNumber)
            addImage(WebImage(albumArtUrl.toUri()))
        }

        return MediaInfo.Builder(songUrl).apply {
            setStreamType(STREAM_TYPE_BUFFERED)
            setContentType(MIME_TYPE_AUDIO_MPEG)
            setMetadata(musicMetadata)
            setStreamDuration(song.duration.toLong())
        }.build()
    }
}
