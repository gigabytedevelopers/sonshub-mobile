package com.gigabytedevelopersinc.apps.sonshub.players.music.models

import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.MediaControllerCompat
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.toIDList

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 08 Feb, 2019
 * Time: 4:35 PM
 * Desc: QueueData
 **/

data class QueueData(
    var queueTitle: String = "All songs",
    var queue: LongArray = LongArray(0),
    var currentId: Long = 0
) {
    fun fromMediaController(mediaControllerCompat: MediaControllerCompat?): QueueData {
        mediaControllerCompat?.let {
            return QueueData(
                    queueTitle = mediaControllerCompat.queueTitle?.toString().orEmpty().let {
                        if (it.isEmpty()) "All songs" else it
                    },
                    queue = mediaControllerCompat.queue?.toIDList() ?: LongArray(0),
                    currentId = mediaControllerCompat.metadata?.getString(METADATA_KEY_MEDIA_ID)?.toLong() ?: 0
            )
        }
        return QueueData()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QueueData

        if (queueTitle != other.queueTitle) return false
        if (!queue.contentEquals(other.queue)) return false
        if (currentId != other.currentId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = queueTitle.hashCode()
        result = 31 * result + queue.contentHashCode()
        result = 31 * result + currentId.hashCode()
        return result
    }
}
