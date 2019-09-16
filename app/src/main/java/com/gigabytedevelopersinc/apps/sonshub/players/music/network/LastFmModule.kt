package com.gigabytedevelopersinc.apps.sonshub.players.music.network

import com.google.gson.Gson
import com.gigabytedevelopersinc.apps.sonshub.players.music.network.api.LastFmRestService
import okhttp3.OkHttpClient
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

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
 * Time: 7:41 PM
 * Desc: LastfmModule
 **/

private const val LAST_FM_API_HOST = "http://ws.audioscrobbler.com/2.0/"

val lastFmModule = module {

    single<LastFmRestService> {
        val client = get<OkHttpClient>()
        val gson = get<Gson>()
        val retrofit = Retrofit.Builder()
                .baseUrl(LAST_FM_API_HOST)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        retrofit.create(LastFmRestService::class.java)
    }
}
