package com.gigabytedevelopersinc.apps.sonshub.players.music.playback

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import androidx.annotation.Nullable
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_NEXT
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_PREVIOUS
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.APP_PACKAGE_NAME
import com.gigabytedevelopersinc.apps.sonshub.players.music.db.QueueEntity
import com.gigabytedevelopersinc.apps.sonshub.players.music.db.QueueHelper
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.isPermissionGranted
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.isPlayEnabled
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.isPlaying
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.toIDList
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.toRawMediaItems
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.MediaID
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.MediaID.Companion.CALLER_OTHER
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.MediaID.Companion.CALLER_SELF
import com.gigabytedevelopersinc.apps.sonshub.players.music.notifications.Notifications
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.players.SongPlayer
import com.gigabytedevelopersinc.apps.sonshub.Repository.AlbumRepository
import com.gigabytedevelopersinc.apps.sonshub.Repository.ArtistRepository
import com.gigabytedevelopersinc.apps.sonshub.Repository.GenreRepository
import com.gigabytedevelopersinc.apps.sonshub.Repository.PlaylistRepository
import com.gigabytedevelopersinc.apps.sonshub.Repository.SongsRepository
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.Utils.EMPTY_ALBUM_ART_URI
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import timber.log.Timber.d as log

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
 * Time: 11:35 AM
 * Desc: TimberMusicService
 **/

// TODO pull out media logic to separate class to make this more readable
class TimberMusicService : MediaBrowserServiceCompat(), KoinComponent, LifecycleOwner {

    companion object {
        const val MEDIA_ID_ARG = "MEDIA_ID"
        const val MEDIA_TYPE_ARG = "MEDIA_TYPE"
        const val MEDIA_CALLER = "MEDIA_CALLER"
        const val MEDIA_ID_ROOT = -1
        const val TYPE_ALL_ARTISTS = 0
        const val TYPE_ALL_ALBUMS = 1
        const val TYPE_ALL_SONGS = 2
        const val TYPE_ALL_PLAYLISTS = 3
        const val TYPE_SONG = 9
        const val TYPE_ALBUM = 10
        const val TYPE_ARTIST = 11
        const val TYPE_PLAYLIST = 12
        const val TYPE_ALL_FOLDERS = 13
        const val TYPE_ALL_GENRES = 14
        const val TYPE_GENRE = 15

        const val NOTIFICATION_ID = 888
    }

    private val notifications by inject<Notifications>()
    private val albumRepository by inject<AlbumRepository>()
    private val artistRepository by inject<ArtistRepository>()
    private val songsRepository by inject<SongsRepository>()
    private val genreRepository by inject<GenreRepository>()
    private val playlistRepository by inject<PlaylistRepository>()

    private lateinit var player: SongPlayer
    private val queueHelper by inject<QueueHelper>()

    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver
    private val lifecycle = LifecycleRegistry(this)

    override fun getLifecycle() = lifecycle

    override fun onCreate() {
        lifecycle.currentState = Lifecycle.State.RESUMED
        super.onCreate()
        log("onCreate()")

        // We get it here so we don't end up lazy-initializing it from a non-UI thread.
        player = get()

        if (isPermissionGranted(READ_EXTERNAL_STORAGE)) {
            GlobalScope.launch(IO) {
                player.setQueue()
            }
        }

        sessionToken = player.getSession().sessionToken
        becomingNoisyReceiver = BecomingNoisyReceiver(this, sessionToken!!)

        player.onPlayingState { isPlaying ->
            if (isPlaying) {
                becomingNoisyReceiver.register()
                startForeground(NOTIFICATION_ID, notifications.buildNotification(getSession()))
            } else {
                becomingNoisyReceiver.unregister()
                stopForeground(false)
                saveCurrentData()
            }
            notifications.updateNotification(player.getSession())
        }

        player.onCompletion {
            notifications.updateNotification(player.getSession())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand(): ${intent?.action}")
        if (intent == null) {
            return START_STICKY
        }

        val mediaSession = player.getSession()
        val controller = mediaSession.controller

        when (intent.action) {
            Constants.ACTION_PLAY_PAUSE -> {
                controller.playbackState?.let { playbackState ->
                    when {
                        playbackState.isPlaying -> controller.transportControls.pause()
                        playbackState.isPlayEnabled -> controller.transportControls.play()
                    }
                }
            }
            ACTION_NEXT -> {
                controller.transportControls.skipToNext()
            }
            ACTION_PREVIOUS -> {
                controller.transportControls.skipToPrevious()
            }
        }

        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return START_STICKY
    }

    override fun onDestroy() {
        lifecycle.currentState = Lifecycle.State.DESTROYED
        log("onDestroy()")
        saveCurrentData()
        player.release()
        notifications.clearNotifications()
        super.onDestroy()
    }

    //media browser
    override fun onLoadChildren(parentId: String, result: Result<List<MediaBrowserCompat.MediaItem>>) {
        result.detach()
        GlobalScope.launch(Main) {
            val mediaItems = withContext(IO) {
                loadChildren(parentId)
            }
            result.sendResult(mediaItems)
        }
    }

    @Nullable
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): MediaBrowserServiceCompat.BrowserRoot? {
        val caller = if (clientPackageName == APP_PACKAGE_NAME) {
            CALLER_SELF
        } else {
            CALLER_OTHER
        }
        return MediaBrowserServiceCompat.BrowserRoot(MediaID(MEDIA_ID_ROOT.toString(), null, caller).asString(), null)
    }

    private fun addMediaRoots(mMediaRoot: MutableList<MediaBrowserCompat.MediaItem>, caller: String) {
        mMediaRoot.add(MediaBrowserCompat.MediaItem(
                MediaDescriptionCompat.Builder().apply {
                    setMediaId(MediaID(TYPE_ALL_ARTISTS.toString(), null, caller).asString())
                    setTitle(getString(R.string.artists))
                    setIconUri(EMPTY_ALBUM_ART_URI.toUri())
                    setSubtitle(getString(R.string.artists))
                }.build(), FLAG_BROWSABLE
        ))

        mMediaRoot.add(MediaBrowserCompat.MediaItem(
                MediaDescriptionCompat.Builder().apply {
                    setMediaId(MediaID(TYPE_ALL_ALBUMS.toString(), null, caller).asString())
                    setTitle(getString(R.string.albums))
                    setIconUri(EMPTY_ALBUM_ART_URI.toUri())
                    setSubtitle(getString(R.string.albums))
                }.build(), FLAG_BROWSABLE
        ))

        mMediaRoot.add(MediaBrowserCompat.MediaItem(
                MediaDescriptionCompat.Builder().apply {
                    setMediaId(MediaID(TYPE_ALL_SONGS.toString(), null, caller).asString())
                    setTitle(getString(R.string.songs))
                    setIconUri(EMPTY_ALBUM_ART_URI.toUri())
                    setSubtitle(getString(R.string.songs))
                }.build(), FLAG_BROWSABLE
        ))

        mMediaRoot.add(MediaBrowserCompat.MediaItem(
                MediaDescriptionCompat.Builder().apply {
                    setMediaId(MediaID(TYPE_ALL_PLAYLISTS.toString(), null, caller).asString())
                    setTitle(getString(R.string.playlists))
                    setIconUri(EMPTY_ALBUM_ART_URI.toUri())
                    setSubtitle(getString(R.string.playlists))
                }.build(), FLAG_BROWSABLE
        ))

        mMediaRoot.add(MediaBrowserCompat.MediaItem(
                MediaDescriptionCompat.Builder().apply {
                    setMediaId(MediaID(TYPE_ALL_GENRES.toString(), null, caller).asString())
                    setTitle(getString(R.string.genres))
                    setIconUri(EMPTY_ALBUM_ART_URI.toUri())
                    setSubtitle(getString(R.string.genres))
                }.build(), FLAG_BROWSABLE
        ))
    }

    private fun loadChildren(parentId: String): ArrayList<MediaBrowserCompat.MediaItem> {
        val mediaItems = ArrayList<MediaBrowserCompat.MediaItem>()
        val mediaIdParent = MediaID().fromString(parentId)

        val mediaType = mediaIdParent.type
        val mediaId = mediaIdParent.mediaId
        val caller = mediaIdParent.caller

        if (mediaType == MEDIA_ID_ROOT.toString()) {
            addMediaRoots(mediaItems, caller!!)
        } else {
            when (mediaType?.toInt() ?: 0) {
                TYPE_ALL_ARTISTS -> {
                    mediaItems.addAll(artistRepository.getAllArtists(caller))
                }
                TYPE_ALL_ALBUMS -> {
                    mediaItems.addAll(albumRepository.getAllAlbums(caller))
                }
                TYPE_ALL_SONGS -> {
                    mediaItems.addAll(songsRepository.loadSongs(caller))
                }
                TYPE_ALL_GENRES -> {
                    mediaItems.addAll(genreRepository.getAllGenres(caller))
                }
                TYPE_ALL_PLAYLISTS -> {
                    mediaItems.addAll(playlistRepository.getPlaylists(caller))
                }
                TYPE_ALBUM -> {
                    mediaId?.let {
                        mediaItems.addAll(albumRepository.getSongsForAlbum(it.toLong(), caller))
                    }
                }
                TYPE_ARTIST -> {
                    mediaId?.let {
                        mediaItems.addAll(artistRepository.getSongsForArtist(it.toLong(), caller))
                    }
                }
                TYPE_PLAYLIST -> {
                    mediaId?.let {
                        mediaItems.addAll(playlistRepository.getSongsInPlaylist(it.toLong(), caller))
                    }
                }
                TYPE_GENRE -> {
                    mediaId?.let {
                        mediaItems.addAll(genreRepository.getSongsForGenre(it.toLong(), caller))
                    }
                }
            }
        }

        return if (caller == CALLER_SELF) {
            mediaItems
        } else {
            mediaItems.toRawMediaItems()
        }
    }

    private fun saveCurrentData() {
        GlobalScope.launch(IO) {
            val mediaSession = player.getSession()
            val controller = mediaSession.controller
            if (controller == null ||
                    controller.playbackState == null ||
                    controller.playbackState.state == STATE_NONE) {
                return@launch
            }

            val queue = controller.queue
            val currentId = controller.metadata?.getString(METADATA_KEY_MEDIA_ID)
            queueHelper.updateQueueSongs(queue?.toIDList(), currentId?.toLong())

            val queueEntity = QueueEntity().apply {
                this.currentId = currentId?.toLong()
                currentSeekPos = controller.playbackState?.position
                repeatMode = controller.repeatMode
                shuffleMode = controller.shuffleMode
                playState = controller.playbackState?.state
                queueTitle = controller.queueTitle?.toString() ?: getString(R.string.all_songs)
            }
            queueHelper.updateQueueData(queueEntity)
        }
    }
}
