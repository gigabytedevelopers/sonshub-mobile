package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.rxkprefs.Pref
import com.google.android.material.snackbar.Snackbar
import com.gigabytedevelopersinc.apps.sonshub.players.music.PREF_SONG_SORT_ORDER
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.SongSortOrder
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.SongSortOrder.SONG_A_Z
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.SongSortOrder.SONG_DURATION
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.SongSortOrder.SONG_YEAR
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.SongSortOrder.SONG_Z_A
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.addOnItemClick
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.filter
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.getExtraBundle
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.inflateTo
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.observe
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.disposeOnDetach
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.ioToMain
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.safeActivity
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.toSongIds
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Song
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.adapters.SongsAdapter
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.base.MediaItemFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.listeners.SortMenuListener
import kotlinx.android.synthetic.main.music_layout_recyclerview.recyclerView
import org.koin.android.ext.android.inject

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 15 Feb, 2019
 * Time: 9:24 AM
 * Desc: SongsFragment
 **/

class SongsFragment : MediaItemFragment() {
    private lateinit var songsAdapter: SongsAdapter
    private val sortOrderPref by inject<Pref<SongSortOrder>>(name = PREF_SONG_SORT_ORDER)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflateTo(R.layout.music_layout_recyclerview, container)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        songsAdapter = SongsAdapter().apply {
            showHeader = true
            popupMenuListener = mainViewModel.popupMenuListener
            sortMenuListener = sortListener
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(safeActivity)
            adapter = songsAdapter
            addOnItemClick { position: Int, _: View ->
                songsAdapter.getSongForPosition(position)?.let { song ->
                    val extras = getExtraBundle(songsAdapter.songs.toSongIds(), getString(R.string.all_songs))
                    mainViewModel.mediaItemClicked(song, extras)
                }
            }
        }

        mediaItemFragmentViewModel.mediaItems
                .filter { it.isNotEmpty() }
                .observe(this) { list ->
                    @Suppress("UNCHECKED_CAST")
                    songsAdapter.updateData(list as List<Song>)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Auto trigger a reload when the sort order pref changes
        sortOrderPref.observe()
                .ioToMain()
                .subscribe { mediaItemFragmentViewModel.reloadMediaItems() }
                .disposeOnDetach(view)
    }

    private val sortListener = object : SortMenuListener {
        override fun shuffleAll() {
            songsAdapter.songs.shuffled().apply {
                val extras = getExtraBundle(toSongIds(), getString(R.string.all_songs))
                if (this.isEmpty()) {
                    Snackbar.make(recyclerView, R.string.shuffle_no_songs_error, Snackbar.LENGTH_SHORT).show()
                } else {
                    mainViewModel.mediaItemClicked(this[0], extras)
                }
            }
        }

        override fun sortAZ() = sortOrderPref.set(SONG_A_Z)

        override fun sortDuration() = sortOrderPref.set(SONG_DURATION)

        override fun sortYear() = sortOrderPref.set(SONG_YEAR)

        override fun numOfSongs() {}

        override fun sortZA() = sortOrderPref.set(SONG_Z_A)
    }
}
