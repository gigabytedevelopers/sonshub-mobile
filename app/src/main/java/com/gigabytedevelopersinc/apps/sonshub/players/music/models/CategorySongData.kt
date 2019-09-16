package com.gigabytedevelopersinc.apps.sonshub.players.music.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

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
 * Time: 1:39 PM
 * Desc: CategorySongData
 **/

@Parcelize
data class CategorySongData(
    val title: String,
    val songCount: Int,
    val type: Int,
    val id: Long
) : Parcelable
