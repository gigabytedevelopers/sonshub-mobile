package com.gigabytedevelopersinc.apps.sonshub.players.music.network.models

import com.google.gson.annotations.SerializedName

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 09 Feb, 2019
 * Time: 7:29 PM
 * Desc: ArtworkSize
 **/

enum class ArtworkSize(val apiValue: String) {
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large"),
    EXTRA_LARGE("extralarge"),
    MEGA("mega")
}

data class LastfmAlbum(@SerializedName("image") val artwork: List<Artwork>)

fun List<Artwork>.ofSize(size: ArtworkSize): Artwork {
    val result = firstOrNull { it.size == size.apiValue } ?: last()
    return if (size == ArtworkSize.MEGA) {
        result.copy(url = result.url.replace("300x300", "700x700"))
    } else {
        result
    }
}
