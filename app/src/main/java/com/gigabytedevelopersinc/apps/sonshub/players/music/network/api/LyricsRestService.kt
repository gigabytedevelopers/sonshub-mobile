package com.gigabytedevelopersinc.apps.sonshub.players.music.network.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

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
 * Time: 4:19 AM
 * Desc: LyricsRestService
 **/

interface LyricsRestService {

    @Headers("Cache-Control: public")
    @GET("/lyrics")
    fun getLyrics(@Query("artist") artist: String, @Query("title") title: String): Observable<String>
}
