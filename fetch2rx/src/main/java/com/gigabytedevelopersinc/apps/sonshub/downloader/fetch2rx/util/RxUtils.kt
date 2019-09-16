package com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2rx.util

import com.gigabytedevelopersinc.apps.sonshub.downloader.fetch2rx.Convertible
import io.reactivex.Flowable

fun <T> Flowable<T>.toConvertible(): Convertible<T> {
    return Convertible(this)
}