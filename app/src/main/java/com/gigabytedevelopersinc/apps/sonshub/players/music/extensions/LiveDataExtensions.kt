package com.gigabytedevelopersinc.apps.sonshub.players.music.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

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
 * Time: 6:40 AM
 * Desc: LiveDataExtensions
 **/

fun <T> LiveData<T>.observe(owner: LifecycleOwner, onEmission: (T) -> Unit) {
    return observe(owner, Observer<T> {
        if (it != null) {
            onEmission(it)
        }
    })
}

fun <X, Y> LiveData<X>.map(mapper: (X) -> Y) =
        Transformations.map(this, mapper)

typealias LiveDataFilter<T> = (T) -> Boolean

/** @author Aidan Follestad (@afollestad) */
class FilterLiveData<T>(
    source1: LiveData<T>,
    private val filter: LiveDataFilter<T>
) : MediatorLiveData<T>() {

    init {
        super.addSource(source1) {
            if (filter(it)) {
                value = it
            }
        }
    }

    override fun <S : Any?> addSource(
        source: LiveData<S>,
        onChanged: Observer<in S>
    ) {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> removeSource(toRemote: LiveData<T>) {
        throw UnsupportedOperationException()
    }
}

fun <T> LiveData<T>.filter(filter: LiveDataFilter<T>): MediatorLiveData<T> = FilterLiveData(this, filter)

fun <T> LiveData<T>.observeOnce(onEmission: (T) -> Unit) {
    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            onEmission(value)
            removeObserver(this)
        }
    }
    observeForever(observer)
}
