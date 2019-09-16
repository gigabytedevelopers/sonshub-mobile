package com.gigabytedevelopersinc.apps.sonshub.Repository

import com.gigabytedevelopersinc.apps.sonshub.players.music.PREF_ALBUM_SORT_ORDER
import com.gigabytedevelopersinc.apps.sonshub.players.music.PREF_SONG_SORT_ORDER
import org.koin.dsl.module.module

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 10 Feb, 2019
 * Time: 10:16 PM
 * Desc: RepositoriesModule
 **/

val repositoriesModule = module {

    factory {
        RealSongsRepository(get(), get(name = PREF_SONG_SORT_ORDER))
    } bind SongsRepository::class

    factory {
        RealAlbumRepository(get(), get(name = PREF_ALBUM_SORT_ORDER))
    } bind AlbumRepository::class

    factory {
        RealArtistRepository(get())
    } bind ArtistRepository::class

    factory {
        RealGenreRepository(get())
    } bind GenreRepository::class

    factory {
        RealPlaylistRepository(get())
    } bind PlaylistRepository::class

    factory {
        RealFoldersRepository()
    } bind FoldersRepository::class
}
