@file:JvmName("OkHttpUtils")

package com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2okhttp

import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2core.getDefaultCookieManager
import okhttp3.CookieJar
import okhttp3.JavaNetCookieJar

fun getDefaultCookieJar(): CookieJar {
    val cookieManager = getDefaultCookieManager()
    return JavaNetCookieJar(cookieManager)
}