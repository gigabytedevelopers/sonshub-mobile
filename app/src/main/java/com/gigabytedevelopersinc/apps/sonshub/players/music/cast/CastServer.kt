package com.gigabytedevelopersinc.apps.sonshub.players.music.cast

import android.content.Context
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.MusicUtils.getRealPathFromURI
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.MusicUtils.getSongUri
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.Utils.getAlbumArtUri
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.Status.INTERNAL_ERROR
import fi.iki.elonen.NanoHTTPD.Response.Status.NOT_FOUND
import fi.iki.elonen.NanoHTTPD.Response.Status.OK
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

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
 * Time: 1:20 PM
 * Desc: CastServer
 **/

const val CAST_SERVER_PORT = 5050

class CastServer(private val context: Context) : NanoHTTPD(CAST_SERVER_PORT) {
    companion object {
        private const val MIME_TYPE_IMAGE = "image/jpg"
        private const val MIME_TYPE_AUDIO = "audio/mp3"
        private const val MIME_TYPE_TEXT = "text/plain"

        const val PART_ALBUM_ART = "albumart"
        const val PART_SONG = "song"
        const val PARAM_ID = "id"
    }

    override fun serve(
        uri: String?,
        method: NanoHTTPD.Method?,
        header: Map<String, String>?,
        parameters: Map<String, String>?,
        files: Map<String, String>?
    ): NanoHTTPD.Response {
        if (uri?.contains(PART_ALBUM_ART) == true) {
            // SERVE ALBUM ART
            val albumId = parameters?.get(PARAM_ID) ?: return errorResponse()
            val albumArtUri = getAlbumArtUri(albumId.toLong())
            var fisAlbumArt: InputStream? = null
            try {
                fisAlbumArt = context.contentResolver.openInputStream(albumArtUri)
            } catch (e: FileNotFoundException) {
                Timber.e(e, "Failed to read album art from $albumArtUri")
                return errorResponse(e.message)
            }
            return newChunkedResponse(OK, MIME_TYPE_IMAGE, fisAlbumArt)
        } else if (uri?.contains(PART_SONG) == true) {
            // SERVE AUDIO
            val songId = parameters?.get(PARAM_ID) ?: return errorResponse()
            val songUri = getSongUri(songId.toLong())
            val songPath = getRealPathFromURI(context, songUri)
            val song = File(songPath)

            var fisSong: FileInputStream? = null
            try {
                fisSong = FileInputStream(song)
            } catch (e: FileNotFoundException) {
                Timber.e(e, "Failed to read song from $songUri")
                return errorResponse(e.message)
            }
            return newFixedLengthResponse(OK, MIME_TYPE_AUDIO, fisSong, song.length())
        }

        // ELSE DEFAULT IS NOT_FOUND
        return newFixedLengthResponse(NOT_FOUND, MIME_TYPE_TEXT, "Not Found")
    }

    private fun errorResponse(message: String? = "Error"): Response {
        return newFixedLengthResponse(INTERNAL_ERROR, MIME_TYPE_TEXT, message ?: "Error")
    }
}
