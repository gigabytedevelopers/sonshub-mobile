package com.gigabytedevelopersinc.apps.sonshub.players.music.playback.players

import android.app.Application
import android.app.PendingIntent
import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import androidx.core.net.toUri
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.Repository.SongsRepository
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_REPEAT_QUEUE
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_REPEAT_SONG
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.REPEAT_MODE
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.SHUFFLE_MODE
import com.gigabytedevelopersinc.apps.sonshub.players.music.db.QueueDao
import com.gigabytedevelopersinc.apps.sonshub.players.music.db.QueueEntity
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.friendlyString
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.isPlaying
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.position
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.toSongIDs
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Song
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.AudioFocusHelper
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.MusicUtils
import timber.log.Timber

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
 * Time: 7:40 AM
 * Desc: SongPlayer
 * A wrapper around [MusicPlayer] that specifically manages playing [Song]s and links up with [Queue].
 **/

typealias OnIsPlaying = SongPlayer.(playing: Boolean) -> Unit

interface SongPlayer {

    fun setQueue(
        data: LongArray = LongArray(0),
        title: String = ""
    )

    fun getSession(): MediaSessionCompat

    fun playSong()

    fun playSong(id: Long)

    fun playSong(song: Song)

    fun seekTo(position: Int)

    fun pause()

    fun nextSong()

    fun repeatSong()

    fun repeatQueue()

    fun previousSong()

    fun playNext(id: Long)

    fun swapQueueSongs(from: Int, to: Int)

    fun removeFromQueue(id: Long)

    fun stop()

    fun release()

    fun onPlayingState(playing: OnIsPlaying)

    fun onPrepared(prepared: OnPrepared<SongPlayer>)

    fun onError(error: OnError<SongPlayer>)

    fun onCompletion(completion: OnCompletion<SongPlayer>)

    fun updatePlaybackState(applier: PlaybackStateCompat.Builder.() -> Unit)

    fun setPlaybackState(state: PlaybackStateCompat)

    fun restoreFromQueueData(queueData: QueueEntity)
}

class RealSongPlayer(
    private val context: Application,
    private val musicPlayer: MusicPlayer,
    private val songsRepository: SongsRepository,
    private val queueDao: QueueDao,
    private val queue: Queue,
    private val audioFocusHelper: AudioFocusHelper
) : SongPlayer {

    private var isInitialized: Boolean = false

    private var isPlayingCallback: OnIsPlaying = {}
    private var preparedCallback: OnPrepared<SongPlayer> = {}
    private var errorCallback: OnError<SongPlayer> = {}
    private var completionCallback: OnCompletion<SongPlayer> = {}

    private var metadataBuilder = MediaMetadataCompat.Builder()
    private var stateBuilder = createDefaultPlaybackState()

    private var mediaSession = MediaSessionCompat(context, context.getString(R.string.app_name)).apply {
        setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        setCallback(
            MediaSessionCallback(
                this,
                this@RealSongPlayer,
                songsRepository,
                queueDao
            )
        )
        setPlaybackState(stateBuilder.build())

        val sessionIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val sessionActivityPendingIntent = PendingIntent.getActivity(context, 0, sessionIntent, 0)
        setSessionActivity(sessionActivityPendingIntent)
        isActive = true
    }

    init {
        queue.setMediaSession(mediaSession)

        audioFocusHelper.onAudioFocusGain {
            Timber.d("GAIN")
            if (isAudioFocusGranted && !getSession().isPlaying()) {
                playSong()
            } else audioFocusHelper.setVolume(AudioManager.ADJUST_RAISE)
            isAudioFocusGranted = false
        }
        audioFocusHelper.onAudioFocusLoss {
            Timber.d("LOSS")
            abandonPlayback()
            isAudioFocusGranted = false
            pause()
        }

        audioFocusHelper.onAudioFocusLossTransient {
            Timber.d("TRANSIENT")
            if (getSession().isPlaying()) {
                isAudioFocusGranted = true
                pause()
            }
        }

        audioFocusHelper.onAudioFocusLossTransientCanDuck {
            Timber.d("TRANSIENT_CAN_DUCK")
            audioFocusHelper.setVolume(AudioManager.ADJUST_LOWER)
        }

        musicPlayer.onPrepared {
            preparedCallback(this@RealSongPlayer)
            playSong()
            seekTo(getSession().position().toInt())
        }

        musicPlayer.onCompletion {
            completionCallback(this@RealSongPlayer)
            val controller = getSession().controller
            when (controller.repeatMode) {
                REPEAT_MODE_ONE -> {
                    controller.transportControls.sendCustomAction(ACTION_REPEAT_SONG, null)
                }
                REPEAT_MODE_ALL -> {
                    controller.transportControls.sendCustomAction(ACTION_REPEAT_QUEUE, null)
                }
                else -> controller.transportControls.skipToNext()
            }
        }
    }

    override fun setQueue(
        data: LongArray,
        title: String
    ) {
        Timber.d("""setQueue: ${data.friendlyString()} ("$title"))""")
        this.queue.ids = data
        this.queue.title = title
    }

    override fun getSession(): MediaSessionCompat = mediaSession

    override fun playSong() {

        Timber.d("playSong()")
        queue.ensureCurrentId()

        if (isInitialized) {
            updatePlaybackState {
                setState(STATE_PLAYING, mediaSession.position(), 1F)
            }
            if (audioFocusHelper.requestPlayback())
                musicPlayer.play()
            return
        }
        musicPlayer.reset()

        val path = MusicUtils.getSongUri(queue.currentSongId).toString()
        val isSourceSet = if (path.startsWith("content://")) {
            musicPlayer.setSource(path.toUri())
        } else {
            musicPlayer.setSource(path)
        }
        if (isSourceSet) {
            isInitialized = true
            musicPlayer.prepare()
        }
    }

    override fun playSong(id: Long) {
        Timber.d("playSong(): $id")
        val song = songsRepository.getSongForId(id)
        playSong(song)
    }

    override fun playSong(song: Song) {
        Timber.d("playSong(): ${song.title}")
        if (queue.currentSongId != song.id) {
            queue.currentSongId = song.id
            isInitialized = false
            updatePlaybackState {
                setState(STATE_STOPPED, 0, 1F)
            }
        }
        setMetaData(song)
        playSong()
    }

    override fun seekTo(position: Int) {
        Timber.d("seekTo(): $position")
        if (isInitialized) {
            musicPlayer.seekTo(position)
            updatePlaybackState {
                setState(
                        mediaSession.controller.playbackState.state,
                        position.toLong(),
                        1F
                )
            }
        }
    }

    override fun pause() {
        Timber.d("pause()")
        if (musicPlayer.isPlaying() && isInitialized) {
            musicPlayer.pause()
            updatePlaybackState {
                setState(STATE_PAUSED, mediaSession.position(), 1F)
            }
        }
    }

    override fun nextSong() {
        Timber.d("nextSong()")
        queue.nextSongId?.let {
            playSong(it)
        } ?: pause()
    }

    override fun repeatSong() {
        Timber.d("repeatSong()")
        updatePlaybackState {
            setState(STATE_STOPPED, 0, 1F)
        }
        playSong(queue.currentSong())
    }

    override fun repeatQueue() {
        Timber.d("repeatQueue()")
        if (queue.currentSongId == queue.lastId())
            playSong(queue.firstId())
        else {
            nextSong()
        }
    }

    override fun previousSong() {
        Timber.d("previousSong()")
        queue.previousSongId?.let(::playSong)
    }

    override fun playNext(id: Long) {
        Timber.d("playNext(): $id")
        queue.moveToNext(id)
    }

    override fun swapQueueSongs(from: Int, to: Int) {
        Timber.d("swapQueueSongs(): $from -> $to")
        queue.swap(from, to)
    }

    override fun removeFromQueue(id: Long) {
        Timber.d("removeFromQueue(): $id")
        queue.remove(id)
    }

    override fun stop() {
        Timber.d("stop()")
        musicPlayer.stop()
        updatePlaybackState {
            setState(STATE_NONE, 0, 1F)
        }
    }

    override fun release() {
        Timber.d("release()")
        mediaSession.apply {
            isActive = false
            release()
        }
        musicPlayer.release()
        queue.reset()
    }

    override fun onPlayingState(playing: OnIsPlaying) {
        this.isPlayingCallback = playing
    }

    override fun onPrepared(prepared: OnPrepared<SongPlayer>) {
        this.preparedCallback = prepared
    }

    override fun onError(error: OnError<SongPlayer>) {
        this.errorCallback = error
        musicPlayer.onError { throwable ->
            errorCallback(this@RealSongPlayer, throwable)
        }
    }

    override fun onCompletion(completion: OnCompletion<SongPlayer>) {
        this.completionCallback = completion
    }

    override fun updatePlaybackState(applier: PlaybackStateCompat.Builder.() -> Unit) {
        applier(stateBuilder)
        setPlaybackState(stateBuilder.build())
    }

    override fun setPlaybackState(state: PlaybackStateCompat) {
        mediaSession.setPlaybackState(state)
        state.extras?.let { bundle ->
            mediaSession.setRepeatMode(bundle.getInt(REPEAT_MODE))
            mediaSession.setShuffleMode(bundle.getInt(SHUFFLE_MODE))
        }
        if (state.isPlaying) {
            isPlayingCallback(this, true)
        } else {
            isPlayingCallback(this, false)
        }
    }

    override fun restoreFromQueueData(queueData: QueueEntity) {
        queue.currentSongId = queueData.currentId ?: -1
        val playbackState = queueData.playState ?: STATE_NONE
        val currentPos = queueData.currentSeekPos ?: 0
        val repeatMode = queueData.repeatMode ?: REPEAT_MODE_NONE
        val shuffleMode = queueData.shuffleMode ?: SHUFFLE_MODE_NONE

        val queueIds = queueDao.getQueueSongsSync().toSongIDs()
        setQueue(queueIds, queueData.queueTitle)
        setMetaData(queue.currentSong())

        val extras = Bundle().apply {
            putInt(REPEAT_MODE, repeatMode)
            putInt(SHUFFLE_MODE, shuffleMode)
        }
        updatePlaybackState {
            setState(playbackState, currentPos, 1F)
            setExtras(extras)
        }
    }

    private fun setMetaData(song: Song) {
        // TODO make music utils injectable
        val artwork = MusicUtils.getAlbumArtBitmap(context, song.albumId)
        val mediaMetadata = metadataBuilder.apply {
            putString(METADATA_KEY_ALBUM, song.album)
            putString(METADATA_KEY_ARTIST, song.artist)
            putString(METADATA_KEY_TITLE, song.title)
            putString(METADATA_KEY_ALBUM_ART_URI, song.albumId.toString())
            putBitmap(METADATA_KEY_ALBUM_ART, artwork)
            putString(METADATA_KEY_MEDIA_ID, song.id.toString())
            putLong(METADATA_KEY_DURATION, song.duration.toLong())
        }.build()
        mediaSession.setMetadata(mediaMetadata)
    }
}

private fun createDefaultPlaybackState(): PlaybackStateCompat.Builder {
    return PlaybackStateCompat.Builder().setActions(
            ACTION_PLAY
                    or ACTION_PAUSE
                    or ACTION_PLAY_FROM_SEARCH
                    or ACTION_PLAY_FROM_MEDIA_ID
                    or ACTION_PLAY_PAUSE
                    or ACTION_SKIP_TO_NEXT
                    or ACTION_SKIP_TO_PREVIOUS
                    or ACTION_SET_SHUFFLE_MODE
                    or ACTION_SET_REPEAT_MODE)
            .setState(STATE_NONE, 0, 1f)
}
