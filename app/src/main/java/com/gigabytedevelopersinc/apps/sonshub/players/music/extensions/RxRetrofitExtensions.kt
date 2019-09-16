package com.gigabytedevelopersinc.apps.sonshub.players.music.extensions

import com.gigabytedevelopersinc.apps.sonshub.players.music.network.Outcome
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 07 Feb, 2019
 * Time: 3:57 AM
 * Desc: RxRetrofitExtensions
 **/

fun <T> Observable<T>.subscribeForOutcome(onOutcome: (Outcome<T>) -> Unit): Disposable {
    return subscribe({ onOutcome(Outcome.success(it)) }, { onOutcome(processError(it)) })
}

private fun <T> processError(error: Throwable): Outcome<T> {
    return when (error) {
        is HttpException -> {
            val response = error.response()
            val body = response!!.errorBody()!!
            Outcome.apiError(getError(body, error))
        }
        is SocketTimeoutException, is IOException -> Outcome.failure(error)
        else -> Outcome.failure(error)
    }
}

private fun getError(
    responseBody: ResponseBody,
    throwable: Throwable
): Throwable {
    return try {
        val jsonObject = JSONObject(responseBody.string())
        Exception(jsonObject.getString("message"), throwable)
    } catch (e: Exception) {
        Exception(e.message ?: "$e")
    }
}
