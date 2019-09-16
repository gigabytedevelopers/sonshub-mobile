package com.gigabytedevelopersinc.apps.sonshub.players.music.network.api

import com.gigabytedevelopersinc.apps.sonshub.players.music.network.models.AlbumInfo
import com.gigabytedevelopersinc.apps.sonshub.players.music.network.models.ArtistInfo
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
 * Time: 2:15 PM
 * Desc: LastFmRestService
 **/

private const val API_KEY = "fdb3a51437d4281d4d64964d333531d4"
private const val FORMAT = "json"

private const val BASE_PARAMETERS_ALBUM = "?method=album.getinfo&api_key=$API_KEY&format=$FORMAT"
private const val BASE_PARAMETERS_ARTIST = "?method=artist.getinfo&api_key=$API_KEY&format=$FORMAT"

interface LastFmRestService {

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ALBUM)
    fun getAlbumInfo(@Query("artist") artist: String, @Query("album") album: String): Observable<AlbumInfo>

    @Headers("Cache-Control: public")
    @GET(BASE_PARAMETERS_ARTIST)
    fun getArtistInfo(@Query("artist") artist: String): Observable<ArtistInfo>
}
