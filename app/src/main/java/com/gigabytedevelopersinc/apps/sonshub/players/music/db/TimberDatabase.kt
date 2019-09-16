package com.gigabytedevelopersinc.apps.sonshub.players.music.db

import androidx.room.Database
import androidx.room.RoomDatabase

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
 * Time: 4:55 PM
 * Desc: TimberDatabase
 **/

@Database(entities = [QueueEntity::class, SongEntity::class], version = 2, exportSchema = false)
abstract class TimberDatabase : RoomDatabase() {

    abstract fun queueDao(): QueueDao
}
