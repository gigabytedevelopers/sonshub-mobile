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
 * Time: 7:29 PM
 * Desc: Artwork
 **/

data class Artwork(
    @SerializedName("#text") val url: String,
    @SerializedName("size") val size: String
)
