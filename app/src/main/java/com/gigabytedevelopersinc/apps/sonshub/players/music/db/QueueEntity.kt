package com.gigabytedevelopersinc.apps.sonshub.players.music.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 06 Feb, 2019
 * Time: 4:25 PM
 * Desc: QueueEntity
 **/

@Entity(tableName = "queue_meta_data")
data class QueueEntity @Ignore constructor(
    @PrimaryKey(autoGenerate = false) var id: Long = 0,
    @ColumnInfo(name = "current_id") var currentId: Long? = 0,
    @ColumnInfo(name = "current_seek_pos") var currentSeekPos: Long? = 0,
    @ColumnInfo(name = "repeat_mode") var repeatMode: Int? = 0,
    @ColumnInfo(name = "shuffle_mode") var shuffleMode: Int? = 0,
    @ColumnInfo(name = "play_state") var playState: Int? = 0,
    @ColumnInfo(name = "queue_title") var queueTitle: String = "All songs"
) {
    constructor() : this(0, 0, 0, 0, 0, 0, "")
}
