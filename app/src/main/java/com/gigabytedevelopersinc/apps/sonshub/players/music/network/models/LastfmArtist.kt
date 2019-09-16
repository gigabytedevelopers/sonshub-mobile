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
 * Time: 7:35 PM
 * Desc: LastfmArtist
 **/

data class LastfmArtist(@SerializedName("image") val artwork: List<Artwork>, @SerializedName("bio") val bio: ArtistBio)
