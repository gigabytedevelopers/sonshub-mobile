package com.gigabytedevelopersinc.apps.sonshub.Repository

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.ContentValues
import android.content.OperationApplicationException
import android.database.Cursor
import android.os.RemoteException
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Playlists._ID
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.mapList
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.value
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.MediaID
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Playlist
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Song
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.Utils.MUSIC_ONLY_SELECTION
import android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI as PLAYLISTS_URI
import android.provider.MediaStore.Audio.Playlists.Members.AUDIO_ID as PLAYLIST_AUDIO_ID
import android.provider.MediaStore.Audio.Playlists.Members.PLAY_ORDER as PLAYLIST_PLAY_ORDER
import android.provider.MediaStore.Audio.PlaylistsColumns.NAME as PLAYLIST_COLUMN_NAME

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 10 Feb, 2019
 * Time: 10:50 PM
 * Desc: PlaylistRepository
 **/

private const val YIELD_FREQUENCY = 100

interface PlaylistRepository {

    fun createPlaylist(name: String?): Long

    fun getPlaylists(caller: String?): List<Playlist>

    fun addToPlaylist(playlistId: Long, ids: LongArray): Int

    fun getSongsInPlaylist(playlistId: Long, caller: String?): List<Song>

    fun removeFromPlaylist(playlistId: Long, id: Long)

    fun deletePlaylist(playlistId: Long): Int
}

class RealPlaylistRepository(
    private val contentResolver: ContentResolver
) : PlaylistRepository {

    override fun createPlaylist(name: String?): Long {
        if (name.isNullOrEmpty()) {
            return -1
        }
        val projection = arrayOf(PLAYLIST_COLUMN_NAME)
        val selection = "$PLAYLIST_COLUMN_NAME = ?"
        val selectionArgs = arrayOf(name)

        return contentResolver.query(
                PLAYLISTS_URI,
                projection,
                selection,
                selectionArgs,
                null
        )?.use {
            return if (it.count <= 0) {
                val values = ContentValues(1).apply {
                    put(PLAYLIST_COLUMN_NAME, name)
                }
                contentResolver.insert(PLAYLISTS_URI, values)?.lastPathSegment?.toLong() ?: -1
            } else {
                -1
            }
        } ?: throw IllegalStateException("Unable to query $PLAYLISTS_URI, system returned null.")
    }

    override fun getPlaylists(caller: String?): List<Playlist> {
        MediaID.currentCaller = caller
        return makePlaylistCursor().mapList(true) {
            val id: Long = value(_ID)
            val songCount = getSongCountForPlaylist(id)
            Playlist.fromCursor(this, songCount)
        }.filter { it.name.isNotEmpty() }
    }

    override fun addToPlaylist(playlistId: Long, ids: LongArray): Int {
        val projection = arrayOf("max($PLAYLIST_PLAY_ORDER)")
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)

        val base: Int = contentResolver.query(uri, projection, null, null, null)?.use {
            if (it.moveToFirst()) {
                it.getInt(0) + 1
            } else {
                0
            }
        } ?: throw IllegalStateException("Unable to query $uri, system returned null.")

        var numInserted = 0
        var offset = 0
        while (offset < ids.size) {
            val bulkValues = makeInsertItems(ids, offset, 1000, base)
            numInserted += contentResolver.bulkInsert(uri, bulkValues)
            offset += 1000
        }

        return numInserted
    }

    override fun getSongsInPlaylist(playlistId: Long, caller: String?): List<Song> {
        MediaID.currentCaller = caller
        val playlistCount = countPlaylist(playlistId)

        makePlaylistSongCursor(playlistId)?.use {
            var runCleanup = false
            if (it.count != playlistCount) {
                runCleanup = true
            }

            if (!runCleanup && it.moveToFirst()) {
                val playOrderCol = it.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.PLAY_ORDER)
                var lastPlayOrder = -1
                do {
                    val playOrder = it.getInt(playOrderCol)
                    if (playOrder == lastPlayOrder) {
                        runCleanup = true
                        break
                    }
                    lastPlayOrder = playOrder
                } while (it.moveToNext())
            }

            if (runCleanup) {
                cleanupPlaylist(playlistId, it, true)
            }
        }

        return makePlaylistSongCursor(playlistId)
                .mapList(true, Song.Companion::fromPlaylistMembersCursor)
    }

    override fun removeFromPlaylist(playlistId: Long, id: Long) {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        contentResolver.delete(
                uri,
                "$PLAYLIST_AUDIO_ID = ?",
                arrayOf(id.toString())
        )
    }

    override fun deletePlaylist(playlistId: Long): Int {
        val localUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
        val localStringBuilder = StringBuilder().apply {
            append("_id IN (")
            append(playlistId)
            append(")")
        }
        return contentResolver.delete(localUri, localStringBuilder.toString(), null)
    }

    private fun makePlaylistCursor(): Cursor? {
        return contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                arrayOf(BaseColumns._ID, MediaStore.Audio.PlaylistsColumns.NAME), null, null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER)
    }

    private fun getSongCountForPlaylist(playlistId: Long): Int {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        return contentResolver.query(uri, arrayOf(_ID), MUSIC_ONLY_SELECTION, null, null)?.use {
            if (it.moveToFirst()) {
                it.count
            } else {
                0
            }
        } ?: 0
    }

    private fun cleanupPlaylist(
        playlistId: Long,
        cursor: Cursor,
        closeCursorAfter: Boolean
    ) {
        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.Members.AUDIO_ID)
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        val ops = arrayListOf<ContentProviderOperation>().apply {
            add(ContentProviderOperation.newDelete(uri).build())
        }

        if (cursor.moveToFirst() && cursor.count > 0) {
            do {
                val builder = ContentProviderOperation.newInsert(uri)
                        .withValue(MediaStore.Audio.Playlists.Members.PLAY_ORDER, cursor.position)
                        .withValue(MediaStore.Audio.Playlists.Members.AUDIO_ID, cursor.getLong(idCol))
                if ((cursor.position + 1) % YIELD_FREQUENCY == 0) {
                    builder.withYieldAllowed(true)
                }
                ops.add(builder.build())
            } while (cursor.moveToNext())
        }

        try {
            contentResolver.applyBatch(MediaStore.AUTHORITY, ops)
        } catch (e: RemoteException) {
        } catch (e: OperationApplicationException) {
        }

        if (closeCursorAfter) {
            cursor.close()
        }
    }

    private fun countPlaylist(playlistId: Long): Int {
        return contentResolver.query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                arrayOf(MediaStore.Audio.Playlists.Members.AUDIO_ID),
                null,
                null,
                MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
        )?.use {
            if (it.moveToFirst()) {
                it.count
            } else {
                0
            }
        } ?: 0
    }

    private fun makePlaylistSongCursor(playlistID: Long?): Cursor? {
        val selection = StringBuilder().apply {
            append("${MediaStore.Audio.AudioColumns.IS_MUSIC}=1")
            append(" AND ${MediaStore.Audio.AudioColumns.TITLE} != ''")
        }
        val projection = arrayOf(
                MediaStore.Audio.Playlists.Members._ID,
                MediaStore.Audio.Playlists.Members.AUDIO_ID,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ARTIST_ID,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.TRACK,
                MediaStore.Audio.Playlists.Members.PLAY_ORDER
        )
        return contentResolver.query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID!!),
                projection,
                selection.toString(),
                null,
                MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER
        )
    }

    private fun makeInsertItems(
        ids: LongArray,
        offset: Int,
        len: Int,
        base: Int
    ): Array<ContentValues> {
        var actualLen = len
        if (offset + actualLen > ids.size) {
            actualLen = ids.size - offset
        }
        val contentValuesList = mutableListOf<ContentValues>()
        for (i in 0 until actualLen) {
            val values = ContentValues().apply {
                put(PLAYLIST_PLAY_ORDER, base + offset + i)
                put(PLAYLIST_AUDIO_ID, ids[offset + i])
            }
            contentValuesList.add(values)
        }
        return contentValuesList.toTypedArray()
    }
}
