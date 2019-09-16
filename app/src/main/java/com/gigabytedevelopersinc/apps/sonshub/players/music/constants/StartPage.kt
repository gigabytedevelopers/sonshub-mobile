package com.gigabytedevelopersinc.apps.sonshub.players.music.constants

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 06 Feb, 2019
 * Time: 3:50 PM
 * Desc: StartPage
 **/

enum class StartPage(val index: Int) {
    SONGS(0),
    ALBUMS(1),
    PLAYLISTS(2),
    ARTISTS(3),
    FOLDERS(4),
    GENRES(5);

    companion object {
        fun fromString(raw: String): StartPage {
            return StartPage.values().single { it.name.toLowerCase() == raw }
        }

        fun fromIndex(index: Int): StartPage {
            return StartPage.values().single { it.index == index }
        }

        fun toString(value: StartPage): String = value.name.toLowerCase()
    }
}
