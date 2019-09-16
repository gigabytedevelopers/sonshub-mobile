package com.gigabytedevelopersinc.apps.sonshub.players.music.extensions

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
 * Time: 4:17 AM
 * Desc: CollectionsExtensions
 **/

fun <T> List<T>?.moveElement(fromIndex: Int, toIndex: Int): List<T> {
    if (this == null) {
        return emptyList()
    }
    return toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
}

fun <T> List<T>.equalsBy(other: List<T>, by: (left: T, right: T) -> Boolean): Boolean {
    if (this.size != other.size) {
        return false
    }
    for ((index, item) in withIndex()) {
        val otherItem = other[index]
        val itemsEqual = by(item, otherItem)
        if (!itemsEqual) {
            return false
        }
    }
    return true
}

fun LongArray.friendlyString(): String {
    return joinToString(separator = ", ", prefix = "[", postfix = "]")
}
