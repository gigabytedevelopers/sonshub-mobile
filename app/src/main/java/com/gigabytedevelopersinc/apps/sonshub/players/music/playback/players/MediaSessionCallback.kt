package com.gigabytedevelopersinc.apps.sonshub.players.music.playback.players

import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_PLAY_NEXT
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_QUEUE_REORDER
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_REPEAT_QUEUE
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_REPEAT_SONG
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_RESTORE_MEDIA_SESSION
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_SET_MEDIA_STATE
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_SONG_DELETED
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.QUEUE_FROM
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.QUEUE_TITLE
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.QUEUE_TO
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.REPEAT_MODE
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.SEEK_TO_POS
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.SHUFFLE_MODE
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.SONG
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.SONGS_LIST
import com.gigabytedevelopersinc.apps.sonshub.players.music.db.QueueDao
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.MediaID
import com.gigabytedevelopersinc.apps.sonshub.Repository.SongsRepository
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.AudioFocusHelper
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
 * Time: 7:20 AM
 * Desc: MediaSessionCallback
 **/

class MediaSessionCallback(
    private val mediaSession: MediaSessionCompat,
    private val songPlayer: SongPlayer,
    private val audioFocusHelper: AudioFocusHelper,
    private val songsRepository: SongsRepository,
    private val queueDao: QueueDao
) : MediaSessionCompat.Callback() {

    init {
        audioFocusHelper.onAudioFocusGain {
            Timber.d("GAIN")
            val isPlaying = songPlayer.getSession().controller.playbackState.state == STATE_PLAYING
            if (isAudioFocusGranted && !isPlaying) {
                songPlayer.playSong()
            } else audioFocusHelper.setVolume(AudioManager.ADJUST_RAISE)
            isAudioFocusGranted = false
        }
        audioFocusHelper.onAudioFocusLoss {
            Timber.d("LOSS")
            abandonPlayback()
            isAudioFocusGranted = false
            onPause()
        }

        audioFocusHelper.onAudioFocusLossTransient {
            Timber.d("TRANSIENT")
            val isPlaying = songPlayer.getSession().controller.playbackState.state == STATE_PLAYING
            if (isPlaying) {
                isAudioFocusGranted = true
                onPause()
            }
        }

        audioFocusHelper.onAudioFocusLossTransientCanDuck {
            Timber.d("TRANSIENT_CAN_DUCK")
            audioFocusHelper.setVolume(AudioManager.ADJUST_LOWER)
        }
    }

    override fun onPause() {
        Timber.d("onPause()")
        songPlayer.pause()
    }

    override fun onPlay() {
        Timber.d("onPlay()")
        if (audioFocusHelper.requestPlayback())
            songPlayer.playSong()
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        Timber.d("onPlayFromSearch()")
        query?.let {
            val song = songsRepository.searchSongs(query, 1)
            if (song.isNotEmpty()) {
                songPlayer.playSong(song.first())
            }
        } ?: onPlay()
    }

    override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
        Timber.d("onPlayFromMediaId()")
        val songId = MediaID().fromString(mediaId).mediaId!!.toLong()
        if (audioFocusHelper.requestPlayback())
            songPlayer.playSong(songId)

        if (extras == null) return
        val queue = extras.getLongArray(SONGS_LIST)
        val seekTo = extras.getInt(SEEK_TO_POS)
        val queueTitle = extras.getString(QUEUE_TITLE) ?: ""

        if (queue != null) {
            songPlayer.setQueue(queue, queueTitle)
        }
        if (seekTo > 0) {
            songPlayer.seekTo(seekTo)
        }
    }

    override fun onSeekTo(pos: Long) {
        Timber.d("onSeekTo()")
        songPlayer.seekTo(pos.toInt())
    }

    override fun onSkipToNext() {
        Timber.d("onSkipToNext()")
        songPlayer.nextSong()
    }

    override fun onSkipToPrevious() {
        Timber.d("onSkipToPrevious()")
        songPlayer.previousSong()
    }

    override fun onStop() {
        Timber.d("onStop()")
        songPlayer.stop()
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        super.onSetRepeatMode(repeatMode)
        val bundle = mediaSession.controller.playbackState.extras ?: Bundle()
        songPlayer.setPlaybackState(
                Builder(mediaSession.controller.playbackState)
                        .setExtras(bundle.apply {
                            putInt(REPEAT_MODE, repeatMode)
                        }
                        ).build()
        )
    }

    override fun onSetShuffleMode(shuffleMode: Int) {
        super.onSetShuffleMode(shuffleMode)
        val bundle = mediaSession.controller.playbackState.extras ?: Bundle()
        songPlayer.setPlaybackState(
                Builder(mediaSession.controller.playbackState)
                        .setExtras(bundle.apply {
                            putInt(SHUFFLE_MODE, shuffleMode)
                        }).build()
        )
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        when (action) {
            ACTION_SET_MEDIA_STATE -> setSavedMediaSessionState()
            ACTION_REPEAT_SONG -> songPlayer.repeatSong()
            ACTION_REPEAT_QUEUE -> songPlayer.repeatQueue()

            ACTION_PLAY_NEXT -> {
                val nextSongId = extras!!.getLong(SONG)
                songPlayer.playNext(nextSongId)
            }

            ACTION_QUEUE_REORDER -> {
                val from = extras!!.getInt(QUEUE_FROM)
                val to = extras.getInt(QUEUE_TO)
                songPlayer.swapQueueSongs(from, to)
            }

            ACTION_SONG_DELETED -> {
                val id = extras!!.getLong(SONG)
                songPlayer.removeFromQueue(id)
            }

            ACTION_RESTORE_MEDIA_SESSION -> restoreMediaSession()
        }
    }

    private fun setSavedMediaSessionState() {
        // Only set saved session from db if we know there is not any active media session
        val controller = mediaSession.controller ?: return
        if (controller.playbackState == null || controller.playbackState.state == STATE_NONE) {
            val queueData = queueDao.getQueueDataSync() ?: return
            songPlayer.restoreFromQueueData(queueData)
        } else {
            // Force update the playback state and metadata from the media session so that the
            // attached Observer in NowPlayingViewModel gets the current state.
            restoreMediaSession()
        }
    }

    private fun restoreMediaSession() {
        songPlayer.setPlaybackState(mediaSession.controller.playbackState)
        mediaSession.setMetadata(mediaSession.controller.metadata)
    }
}
