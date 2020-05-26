package com.gigabytedevelopersinc.apps.sonshub.players.music.playback

import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.players.MusicPlayer
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.players.Queue
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.players.RealMusicPlayer
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.players.RealQueue
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.players.RealSongPlayer
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.players.SongPlayer
import org.koin.dsl.bind
import org.koin.dsl.module

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
 * Time: 9:10 AM
 * Desc: MediaModule
 **/

val mediaModule = module {

    factory {
        RealMusicPlayer(get())
    } bind MusicPlayer::class

    factory {
        RealQueue(get(), get(), get())
    } bind Queue::class

    factory {
        RealSongPlayer(get(), get(), get(), get(), get())
    } bind SongPlayer::class
}
