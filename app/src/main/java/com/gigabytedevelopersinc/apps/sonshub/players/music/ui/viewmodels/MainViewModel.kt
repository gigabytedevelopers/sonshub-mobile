package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.viewmodels

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.mediarouter.app.MediaRouteButton
import androidx.mediarouter.media.MediaControlIntent.CATEGORY_LIVE_AUDIO
import androidx.mediarouter.media.MediaControlIntent.CATEGORY_REMOTE_PLAYBACK
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import androidx.mediarouter.media.MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY
import com.gigabytedevelopersinc.apps.sonshub.R
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.Session
import com.google.android.gms.cast.framework.SessionManager
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.MediaSessionConnection
import com.gigabytedevelopersinc.apps.sonshub.players.music.cast.CastHelper
import com.gigabytedevelopersinc.apps.sonshub.players.music.cast.CastServer
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_CAST_CONNECTED
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_CAST_DISCONNECTED
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_PLAY_NEXT
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_REMOVED_FROM_PLAYLIST
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_SONG_DELETED
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.SONG
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.id
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.isPlayEnabled
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.isPlaying
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.isPrepared
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.map
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.show
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.CastStatus
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.CastStatus.Companion.STATUS_NONE
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.MediaID
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Song
import com.gigabytedevelopersinc.apps.sonshub.Repository.AlbumRepository
import com.gigabytedevelopersinc.apps.sonshub.Repository.ArtistRepository
import com.gigabytedevelopersinc.apps.sonshub.Repository.PlaylistRepository
import com.gigabytedevelopersinc.apps.sonshub.Repository.SongsRepository
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.activities.MusicMainActivity
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.dialogs.AddToPlaylistDialog
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.dialogs.DeleteSongDialog
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.listeners.PopupMenuListener
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.Event
import java.io.IOException
import timber.log.Timber.d as log
import timber.log.Timber.e as loge
import timber.log.Timber.w as warn

class MainViewModel(
    private val context: Context,
    private val mediaSessionConnection: MediaSessionConnection,
    private val songsRepository: SongsRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val playlistsRepository: PlaylistRepository
) : ViewModel() {

    val rootMediaId: LiveData<MediaID> =
            mediaSessionConnection.isConnected.map { isConnected ->
                if (isConnected) {
                    MediaID().fromString(mediaSessionConnection.rootMediaId)
                } else {
                    null
                }
            }

    val mediaController: LiveData<MediaControllerCompat> =
            mediaSessionConnection.isConnected.map { isConnected ->
                if (isConnected) {
                    mediaSessionConnection.mediaController
                } else {
                    null
                }
            }

    val navigateToMediaItem: LiveData<Event<MediaID>> get() = _navigateToMediaItem
    private val _navigateToMediaItem = MutableLiveData<Event<MediaID>>()

    val customAction: LiveData<Event<String>> get() = _customAction
    private val _customAction = MutableLiveData<Event<String>>()

    fun mediaItemClicked(clickedItem: MediaBrowserCompat.MediaItem, extras: Bundle?) {
        log("mediaItemClicked(): $clickedItem")
        if (clickedItem.isBrowsable) {
            browseToItem(clickedItem)
        } else {
            playMedia(clickedItem, extras)
        }
    }

    private fun browseToItem(mediaItem: MediaBrowserCompat.MediaItem) {
        log("browseToItem(): $mediaItem")
        _navigateToMediaItem.value = Event(MediaID().fromString(mediaItem.mediaId!!).apply {
            this.mediaItem = mediaItem
        })
    }

    fun transportControls() = mediaSessionConnection.transportControls

    private fun playMedia(mediaItem: MediaBrowserCompat.MediaItem, extras: Bundle?) {
        log("playMedia(): $mediaItem")

        //check if casting
        castSession?.let { castSession ->
            val songID = MediaID().fromString(mediaItem.mediaId!!).mediaId!!.toLong()
            castLiveData.value?.let {
                if (it.state != STATUS_NONE && it.castSongId != -1 && it.castSongId.toLong() == songID) {
                    castSession.remoteMediaClient.togglePlayback()
                    return
                }
            }
            val song = songsRepository.getSongForId(songID)
            val songsList = extras?.getLongArray(Constants.SONGS_LIST)
            if (songsList != null) {
                val currentIndex = songsList.indexOf(songID)
                val extraSize = songsList.size - currentIndex - 1
                //only send 5 items in queue
                val filteredQueue = songsList.copyOfRange(
                        fromIndex = currentIndex,
                        toIndex = currentIndex + if (extraSize >= 5) 5 else extraSize
                )
                CastHelper.castSongQueue(castSession, songsRepository.getSongsForIds(filteredQueue), 0)
                return
            }
            CastHelper.castSong(castSession, song)
            return
        }

        val nowPlaying = mediaSessionConnection.nowPlaying.value
        val transportControls = mediaSessionConnection.transportControls

        val isPrepared = mediaSessionConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && MediaID().fromString(mediaItem.mediaId!!).mediaId == nowPlaying?.id) {
            mediaSessionConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        warn("Playable item clicked but neither play nor pause are enabled! (mediaId=${mediaItem.mediaId})")
                    }
                }
            }
        } else {
            transportControls.playFromMediaId(mediaItem.mediaId, extras)
        }
    }

    val popupMenuListener = object : PopupMenuListener {

        override fun play(song: Song) {
            playMedia(song, null)
        }

        override fun goToAlbum(song: Song) {
            browseToItem(albumRepository.getAlbum(song.albumId))
        }

        override fun goToArtist(song: Song) {
            browseToItem(artistRepository.getArtist(song.artistId))
        }

        override fun addToPlaylist(context: Context, song: Song) {
            AddToPlaylistDialog.show(context as AppCompatActivity, song)
        }

        override fun removeFromPlaylist(song: Song, playlistId: Long) {
            playlistsRepository.removeFromPlaylist(playlistId, song.id)
            _customAction.postValue(Event(ACTION_REMOVED_FROM_PLAYLIST))
        }

        override fun deleteSong(song: Song) = DeleteSongDialog.show(context as MusicMainActivity, song)

        override fun playNext(song: Song) {
            mediaSessionConnection.transportControls.sendCustomAction(ACTION_PLAY_NEXT,
                    Bundle().apply { putLong(SONG, song.id) }
            )
        }
    }

    fun onSongDeleted(id: Long) {
        _customAction.postValue(Event(ACTION_SONG_DELETED))
        // also need to remove the deleted song from the current playing queue
        mediaSessionConnection.transportControls.sendCustomAction(ACTION_SONG_DELETED,
                Bundle().apply {
                    // sending parceleable data through media session custom action bundle is not working currently
                    putLong(SONG, id)
                })
    }

    //cast helpers
    private var castSession: CastSession? = null
    private var sessionManager: SessionManager? = null
    private var isPlayServiceAvailable = false
    private var castServer: CastServer? = null
    private var mediaRouteButton: MediaRouteButton? = null

    val castLiveData: LiveData<CastStatus> get() = _castLiveData
    private val _castLiveData = MutableLiveData<CastStatus>()

    val castProgressLiveData: LiveData<Pair<Long, Long>> get() = _castProgressLiveData
    private val _castProgressLiveData = MutableLiveData<Pair<Long, Long>>()

    private val castCallback = object : RemoteMediaClient.Callback() {
        override fun onStatusUpdated() {
            super.onStatusUpdated()
            castSession?.let {
                _castLiveData.postValue(CastStatus().fromRemoteMediaClient(it.castDevice.friendlyName,
                        it.remoteMediaClient))
            }
        }
    }

    private val castProgressListener =
            RemoteMediaClient.ProgressListener { progress, duration ->
                log("Cast progress: $progress/$duration")
                _castProgressLiveData.postValue(Pair(progress, duration))
            }

    fun setupCastButton(mediaRouteButton: MediaRouteButton) {
        val castContext = ContextThemeWrapper(context, R.style.Theme_MediaRouter)
        val drawable: Drawable?
        val a = castContext.obtainStyledAttributes(null, R.styleable.MediaRouteButton, R.attr.mediaRouteButtonStyle, 0)
        drawable = a.getDrawable(R.styleable.MediaRouteButton_externalRouteEnabledDrawable)
        a.recycle()
        DrawableCompat.setTint(drawable!!, ContextCompat.getColor(context, R.color.white))
        if (isPlayServiceAvailable) {
            log("setupCastButton()")
            this.mediaRouteButton = mediaRouteButton
            val selector = MediaRouteSelector.fromBundle(MediaRouteSelector.Builder().apply {
                addControlCategory(CATEGORY_REMOTE_PLAYBACK)
                addControlCategory(CATEGORY_LIVE_AUDIO)
            }.build().asBundle())

            MediaRouter.getInstance(context).apply {
                addCallback(selector, object : MediaRouter.Callback() {
                    override fun onRouteChanged(router: MediaRouter?, route: MediaRouter.RouteInfo?) {
                        super.onRouteChanged(router, route)
                        mediaRouteButton.show()
                        mediaRouteButton.routeSelector = selector
                    }
                }, CALLBACK_FLAG_REQUEST_DISCOVERY)
            }

            CastButtonFactory.setUpMediaRouteButton(context.applicationContext, mediaRouteButton)
            mediaRouteButton.setRemoteIndicatorDrawable(drawable)
        } else {
            log("setupCastButton() - Play services not available")
        }
    }

    fun setupCastSession() {
        try {
            isPlayServiceAvailable = GoogleApiAvailability
                    .getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

            if (isPlayServiceAvailable) {
                log("setupCastSession()")
                val castContext = CastContext.getSharedInstance(context.applicationContext)
                sessionManager = castContext.sessionManager
                if (castSession == null) {
                    castSession = sessionManager?.currentCastSession.also {
                        it?.remoteMediaClient?.registerCallback(castCallback)
                        it?.remoteMediaClient?.addProgressListener(castProgressListener, 100)
                    }
                    sessionManager?.addSessionManagerListener(sessionManagerListener)
                } else {
                    sessionManager?.currentCastSession?.let { castSession = it }
                }
            } else {
                log("setupCastSession() - Play services not available")
            }
        } catch (e: Exception) {
            loge(e)
        }
    }

    fun pauseCastSession() {
        log("pauseCastSession()")
        sessionManager?.removeSessionManagerListener(sessionManagerListener)
        castSession?.remoteMediaClient?.run {
            unregisterCallback(castCallback)
            removeProgressListener(castProgressListener)
        }
        castSession = null
    }

    private val sessionManagerListener = object : SessionManagerListener<Session> {
        override fun onSessionEnded(p0: Session?, p1: Int) {
            log("onSessionEnded()")
            _customAction.postValue(Event(ACTION_CAST_DISCONNECTED))
            pauseCastSession()
            stopCastServer()
        }

        override fun onSessionEnding(p0: Session?) = Unit

        override fun onSessionResumeFailed(p0: Session?, p1: Int) = Unit

        override fun onSessionResumed(p0: Session?, p1: Boolean) {
            log("onSessionResumed()")
            _customAction.postValue(Event(ACTION_CAST_CONNECTED))
            setupCastSession()
            mediaRouteButton?.show()
        }

        override fun onSessionResuming(p0: Session?, p1: String?) {
            log("onSessionResuming()")
            startCastServer()
        }

        override fun onSessionStartFailed(p0: Session?, p1: Int) {
            warn("onSessionStartFailed()")
        }

        override fun onSessionStarted(p0: Session?, p1: String?) {
            log("onSessionStarted()")
            _customAction.postValue(Event(ACTION_CAST_CONNECTED))
            setupCastSession()
            mediaRouteButton?.show()
        }

        override fun onSessionStarting(p0: Session?) {
            log("onSessionStarting()")
            startCastServer()
        }

        override fun onSessionSuspended(p0: Session?, p1: Int) {
            log("onSessionSuspended()")
            stopCastServer()
        }
    }

    private fun startCastServer() {
        log("startCastServer()")
        castServer = CastServer(context)
        try {
            castServer?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopCastServer() {
        log("stopCastServer()")
        castServer?.stop()
    }
}
