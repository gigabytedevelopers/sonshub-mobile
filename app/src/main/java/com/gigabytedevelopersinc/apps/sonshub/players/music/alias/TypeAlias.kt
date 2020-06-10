package com.gigabytedevelopersinc.apps.sonshub.players.music.alias

import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.AudioFocusHelper

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
 * Time: 12:17 PM
 * Desc: TypeAlias
 **/

typealias OnAudioFocusGain = AudioFocusHelper.() -> Unit
typealias OnAudioFocusLoss = AudioFocusHelper.() -> Unit
typealias OnAudioFocusLossTransient = AudioFocusHelper.() -> Unit
typealias OnAudioFocusLossTransientCanDuck = AudioFocusHelper.() -> Unit