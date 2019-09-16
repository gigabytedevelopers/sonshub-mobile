package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.base

import android.os.Bundle
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.MEDIA_CALLER
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.MEDIA_ID_ARG
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.MEDIA_TYPE_ARG
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ALBUM
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ALL_ALBUMS
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ALL_ARTISTS
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ALL_FOLDERS
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ALL_GENRES
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ALL_PLAYLISTS
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ALL_SONGS
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ARTIST
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_GENRE
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_PLAYLIST
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_REMOVED_FROM_PLAYLIST
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_SONG_DELETED
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ALBUM
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ARTIST
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.CATEGORY_SONG_DATA
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.argumentOrEmpty
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.map
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.observe
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.CategorySongData
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Genre
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.MediaID
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Playlist
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.FolderFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.GenreFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.PlaylistFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.album.AlbumDetailFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.album.AlbumsFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.artist.ArtistDetailFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.artist.ArtistFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.songs.CategorySongsFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.songs.SongsFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.viewmodels.MediaItemFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

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
 * Time: 9:37 AM
 * Desc: MediaItemFragment
 **/

open class MediaItemFragment : BaseNowPlayingFragment() {

    protected lateinit var mediaItemFragmentViewModel: MediaItemFragmentViewModel

    private lateinit var mediaType: String
    private var mediaId: String? = null
    private var caller: String? = null

    companion object {
        fun newInstance(mediaId: MediaID): MediaItemFragment {
            val args = Bundle().apply {
                putString(MEDIA_TYPE_ARG, mediaId.type)
                putString(MEDIA_ID_ARG, mediaId.mediaId)
                putString(MEDIA_CALLER, mediaId.caller)
            }
            return when (mediaId.type?.toInt()) {
                TYPE_ALL_SONGS -> SongsFragment().apply { arguments = args }
                TYPE_ALL_ALBUMS -> AlbumsFragment().apply { arguments = args }
                TYPE_ALL_PLAYLISTS -> PlaylistFragment().apply { arguments = args }
                TYPE_ALL_ARTISTS -> ArtistFragment().apply { arguments = args }
                TYPE_ALL_FOLDERS -> FolderFragment().apply { arguments = args }
                TYPE_ALL_GENRES -> GenreFragment().apply { arguments = args }
                TYPE_ALBUM -> AlbumDetailFragment().apply {
                    arguments = args.apply { putParcelable(ALBUM, mediaId.mediaItem) }
                }
                TYPE_ARTIST -> ArtistDetailFragment().apply {
                    arguments = args.apply { putParcelable(ARTIST, mediaId.mediaItem) }
                }
                TYPE_PLAYLIST -> CategorySongsFragment().apply {
                    arguments = args.apply {
                        (mediaId.mediaItem as Playlist).apply {
                            val data = CategorySongData(name, songCount, TYPE_PLAYLIST, id)
                            putParcelable(CATEGORY_SONG_DATA, data)
                        }
                    }
                }
                TYPE_GENRE -> CategorySongsFragment().apply {
                    arguments = args.apply {
                        (mediaId.mediaItem as Genre).apply {
                            val data = CategorySongData(name, songCount, TYPE_GENRE, id)
                            putParcelable(CATEGORY_SONG_DATA, data)
                        }
                    }
                }
                else -> SongsFragment().apply {
                    arguments = args
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mediaType = argumentOrEmpty(MEDIA_TYPE_ARG)
        mediaId = argumentOrEmpty(MEDIA_ID_ARG)
        caller = argumentOrEmpty(MEDIA_CALLER)

        val mediaId = MediaID(mediaType, mediaId, caller)
        mediaItemFragmentViewModel = getViewModel { parametersOf(mediaId) }

        mainViewModel.customAction
                .map { it.getContentIfNotHandled() }
                .observe(this) {
                    when (it) {
                        ACTION_SONG_DELETED -> mediaItemFragmentViewModel.reloadMediaItems()
                        ACTION_REMOVED_FROM_PLAYLIST -> mediaItemFragmentViewModel.reloadMediaItems()
                    }
                }
    }
}
