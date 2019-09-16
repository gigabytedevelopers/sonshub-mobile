package com.gigabytedevelopersinc.apps.sonshub.players.music.network

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
 * Time: 10:00 PM
 * Desc: Outcome
 **/

sealed class Outcome<T> {
    data class Success<T>(var data: T) : Outcome<T>()
    data class Failure<T>(val e: Throwable) : Outcome<T>()
    data class ApiError<T>(val e: Throwable) : Outcome<T>()

    companion object {
        fun <T> success(data: T): Outcome<T> = Success(data)

        fun <T> failure(e: Throwable): Outcome<T> = Failure(e)

        fun <T> apiError(e: Throwable): Outcome<T> = ApiError(e)
    }
}
