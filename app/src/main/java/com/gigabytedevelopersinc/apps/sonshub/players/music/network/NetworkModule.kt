package com.gigabytedevelopersinc.apps.sonshub.players.music.network

import android.app.Application
import com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.dsl.module.module

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
 * Time: 8:27 PM
 * Desc: NetworkModule
 **/

private const val CACHE_MAX_AGE = 60 * 60 * 24 * 7
private const val CACHE_MAX_STALE = 31536000
private const val CACHE_SIZE = 1024L * 1024
private const val CACHE_CONTROL = "Cache-Control"

val networkModule = module {

    // OkHttp
    single {
        val cacheHeader = "max-age=$CACHE_MAX_AGE,max-stale=$CACHE_MAX_STALE"
        val cache = Cache(get<Application>().cacheDir, CACHE_SIZE)
        OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor {
                    val newRequest = it.request()
                            .newBuilder()
                            .addHeader(CACHE_CONTROL, cacheHeader)
                            .build()
                    it.proceed(newRequest)
                }
                .build()
    }

    // Gson
    single {
        GsonBuilder()
                .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
                .create()
    }
}
