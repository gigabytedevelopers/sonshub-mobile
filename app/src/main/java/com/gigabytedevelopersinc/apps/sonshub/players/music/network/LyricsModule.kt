package com.gigabytedevelopersinc.apps.sonshub.players.music.network

import com.gigabytedevelopersinc.apps.sonshub.players.music.network.api.LyricsRestService
import com.gigabytedevelopersinc.apps.sonshub.players.music.network.conversion.LyricsConverterFactory
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

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
 * Time: 7:57 PM
 * Desc: LyricsModule
 **/

private const val LYRICS_API_HOST = "https://makeitpersonal.co"

val lyricsModule = module {

    single<LyricsRestService> {
        val client = get<OkHttpClient>()
        val retrofit = Retrofit.Builder()
                .baseUrl(LYRICS_API_HOST)
                .client(client)
                .addConverterFactory(LyricsConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        retrofit.create(LyricsRestService::class.java)
    }
}
