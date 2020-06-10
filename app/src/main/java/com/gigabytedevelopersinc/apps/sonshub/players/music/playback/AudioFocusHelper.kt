package com.gigabytedevelopersinc.apps.sonshub.players.music.playback

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import com.gigabytedevelopersinc.apps.sonshub.players.music.alias.OnAudioFocusGain
import com.gigabytedevelopersinc.apps.sonshub.players.music.alias.OnAudioFocusLoss
import com.gigabytedevelopersinc.apps.sonshub.players.music.alias.OnAudioFocusLossTransient
import com.gigabytedevelopersinc.apps.sonshub.players.music.alias.OnAudioFocusLossTransientCanDuck
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.Utils.isOreo

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Tuesday, 09
 * Month: June
 * Year: 2020
 * Date: 09 Jun, 2020
 * Time: 12:15 PM
 * Desc: AudioFocusHelper
 **/
interface AudioFocusHelper {
    var isAudioFocusGranted: Boolean

    fun requestPlayback(): Boolean
    fun abandonPlayback()
    fun onAudioFocusGain(audioFocusGain: OnAudioFocusGain)
    fun onAudioFocusLoss(audioFocusLoss: OnAudioFocusLoss)
    fun onAudioFocusLossTransient(audioFocusLossTransient: OnAudioFocusLossTransient)
    fun onAudioFocusLossTransientCanDuck(audioFocusLossTransientCanDuck: OnAudioFocusLossTransientCanDuck)
    fun setVolume(volume: Int)
}

class AudioFocusHelperImplementation(
    context: Context
) : AudioFocusHelper, AudioManager.OnAudioFocusChangeListener {

    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var audioFocusGainCallback: OnAudioFocusGain = {}
    private var audioFocusLossCallback: OnAudioFocusLoss = {}
    private var audioFocusLossTransientCallback: OnAudioFocusLossTransient = {}
    private var audioFocusLossTransientCanDuckCallback: OnAudioFocusLossTransientCanDuck = {}

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> audioFocusGainCallback()
            AudioManager.AUDIOFOCUS_LOSS -> audioFocusLossCallback()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> audioFocusLossTransientCallback()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> audioFocusLossTransientCanDuckCallback()
        }
    }

    override var isAudioFocusGranted: Boolean = false

    override fun requestPlayback(): Boolean {
        val state = if (isOreo()) {
            val attr = AudioAttributes.Builder().apply {
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                setUsage(AudioAttributes.USAGE_MEDIA)
            }.build()
            audioManager.requestAudioFocus(
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(attr)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(this)
                    .build()
            )
        } else audioManager.requestAudioFocus(this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return state == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    override fun abandonPlayback() {
        if (isOreo()) {
            val attr = AudioAttributes.Builder().apply {
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                setUsage(AudioAttributes.USAGE_MEDIA)
            }.build()
            audioManager.abandonAudioFocusRequest(
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener(this)
                    .setAudioAttributes(attr)
                    .build()
            )
        } else audioManager.abandonAudioFocus(this)
    }

    override fun onAudioFocusGain(audioFocusGain: OnAudioFocusGain) {
        audioFocusGainCallback = audioFocusGain
    }

    override fun onAudioFocusLoss(audioFocusLoss: OnAudioFocusLoss) {
        audioFocusLossCallback = audioFocusLoss
    }

    override fun onAudioFocusLossTransient(audioFocusLossTransient: OnAudioFocusLossTransient) {
        audioFocusLossTransientCallback = audioFocusLossTransient
    }

    override fun onAudioFocusLossTransientCanDuck(audioFocusLossTransientCanDuck: OnAudioFocusLossTransientCanDuck) {
        audioFocusLossTransientCanDuckCallback = audioFocusLossTransientCanDuck
    }

    override fun setVolume(volume: Int) {
        audioManager.adjustVolume(volume, AudioManager.FLAG_PLAY_SOUND)
    }
}