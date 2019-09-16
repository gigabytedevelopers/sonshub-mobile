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
 * Date: 08 Feb, 2019
 * Time: 7:18 PM
 * Desc: ArtistBio
 **/

data class ArtistBio(
    @SerializedName("summary") val summary: String,
    @SerializedName("content") val content: String
)
