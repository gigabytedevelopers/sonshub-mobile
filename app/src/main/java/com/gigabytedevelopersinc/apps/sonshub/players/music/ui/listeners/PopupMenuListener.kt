package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.listeners

import android.content.Context
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Song

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 13 Feb, 2019
 * Time: 4:59 AM
 * Desc: PopupMenuListener
 **/

interface PopupMenuListener {

    fun play(song: Song)

    fun goToAlbum(song: Song)

    fun goToArtist(song: Song)

    fun addToPlaylist(context: Context, song: Song)

    fun deleteSong(song: Song)

    fun removeFromPlaylist(song: Song, playlistId: Long)

    fun playNext(song: Song)
}
