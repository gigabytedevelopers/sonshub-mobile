package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Album
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Artist
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Song
import com.gigabytedevelopersinc.apps.sonshub.Repository.AlbumRepository
import com.gigabytedevelopersinc.apps.sonshub.Repository.ArtistRepository
import com.gigabytedevelopersinc.apps.sonshub.Repository.SongsRepository
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val songsRepository: SongsRepository,
    private val albumsRepository: AlbumRepository,
    private val artistsRepository: ArtistRepository
) : CoroutineViewModel(Main) {

    private val searchData = SearchData()
    private val _searchLiveData = MutableLiveData<SearchData>()

    val searchLiveData = _searchLiveData

    fun search(query: String) {
        if (query.length >= 3) {
            launch {
                val songs = withContext(IO) {
                    songsRepository.searchSongs(query, 10)
                }
                if (songs.isNotEmpty()) {
                    searchData.songs = songs.toMutableList()
                }
                _searchLiveData.postValue(searchData)
            }

            launch {
                val albums = withContext(IO) {
                    albumsRepository.getAlbums(query, 7)
                }
                if (albums.isNotEmpty()) {
                    searchData.albums = albums.toMutableList()
                }
                _searchLiveData.postValue(searchData)
            }

            launch {
                val artists = withContext(IO) {
                    artistsRepository.getArtists(query, 7)
                }
                if (artists.isNotEmpty()) {
                    searchData.artists = artists.toMutableList()
                }
                _searchLiveData.postValue(searchData)
            }
        } else {
            _searchLiveData.postValue(searchData.clear())
        }
    }

    data class SearchData(
        var songs: MutableList<Song> = mutableListOf(),
        var albums: MutableList<Album> = mutableListOf(),
        var artists: MutableList<Artist> = mutableListOf()
    ) {

        fun clear(): SearchData {
            songs.clear()
            albums.clear()
            artists.clear()
            return this
        }
    }
}
