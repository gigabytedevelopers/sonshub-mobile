package com.gigabytedevelopersinc.apps.sonshub.players.music.db

import android.app.Application
import androidx.room.Room
import org.koin.dsl.bind
import org.koin.dsl.module

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
 * Time: 4:50 PM
 * Desc: RoomModule
 **/

val roomModule = module {

    single {
        Room.databaseBuilder(get<Application>(), TimberDatabase::class.java, "queue.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }

    factory { get<TimberDatabase>().queueDao() }

    factory {
        RealQueueHelper(get(), get())
    } bind QueueHelper::class
}
