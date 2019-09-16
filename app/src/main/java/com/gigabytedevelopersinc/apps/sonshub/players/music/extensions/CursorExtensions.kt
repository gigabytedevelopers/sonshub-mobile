package com.gigabytedevelopersinc.apps.sonshub.players.music.extensions

import android.database.Cursor

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
 * Time: 4:19 AM
 * Desc: CursorExtensions
 **/

fun Cursor?.forEach(
    closeAfter: Boolean = false,
    each: Cursor.() -> Unit
) {
    if (this == null) return
    if (moveToFirst()) {
        do {
            each(this)
        } while (moveToNext())
    }
    if (closeAfter) {
        close()
    }
}

fun <T> Cursor?.mapList(
    closeAfter: Boolean = false,
    mapper: Cursor.() -> T
): MutableList<T> {
    val result = mutableListOf<T>()
    forEach(closeAfter = closeAfter) {
        result.add(mapper(this))
    }
    return result
}

inline fun <reified T> Cursor.value(name: String): T {
    val index = getColumnIndexOrThrow(name)
    return when (T::class) {
        Short::class -> getShort(index) as T
        Int::class -> getInt(index) as T
        Long::class -> getLong(index) as T
        Boolean::class -> (getInt(index) == 1) as T
        String::class -> getString(index) as T
        Float::class -> getFloat(index) as T
        Double::class -> getDouble(index) as T
        ByteArray::class -> getBlob(index) as T
        else -> throw IllegalStateException("What do I do with ${T::class.java.simpleName}?")
    }
}

inline fun <reified T> Cursor.valueOrDefault(name: String, defaultValue: T): T {
    val index = getColumnIndex(name)
    if (index == -1) {
        return defaultValue
    }
    return when (T::class) {
        Short::class -> getShort(index) as? T ?: defaultValue
        Int::class -> getInt(index) as? T ?: defaultValue
        Long::class -> getLong(index) as? T ?: defaultValue
        Boolean::class -> (getInt(index) == 1) as T
        String::class -> getString(index) as? T ?: defaultValue
        Float::class -> getFloat(index) as? T ?: defaultValue
        Double::class -> getDouble(index) as? T ?: defaultValue
        ByteArray::class -> getBlob(index) as? T ?: defaultValue
        else -> throw IllegalStateException("What do I do with ${T::class.java.simpleName}?")
    }
}

fun Cursor.valueOrEmpty(name: String): String = valueOrDefault(name, "")
