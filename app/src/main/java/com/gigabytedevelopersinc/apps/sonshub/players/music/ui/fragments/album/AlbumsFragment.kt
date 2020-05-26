package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.rxkprefs.Pref
import com.gigabytedevelopersinc.apps.sonshub.players.music.PREF_ALBUM_SORT_ORDER
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.AlbumSortOrder
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.AlbumSortOrder.ALBUM_A_Z
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.AlbumSortOrder.ALBUM_Z_A
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.AlbumSortOrder.ALBUM_YEAR
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.AlbumSortOrder.ALBUM_NUMBER_OF_SONGS
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.addOnItemClick
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.filter
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.inflateTo
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.observe
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.disposeOnDetach
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.ioToMain
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.safeActivity
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Album
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.adapters.AlbumAdapter
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.base.MediaItemFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.listeners.SortMenuListener
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.SpacesItemDecoration
import kotlinx.android.synthetic.main.music_layout_recyclerview_padding.recyclerView
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

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
 * Time: 7:40 AM
 * Desc: AlbumsFragment
 **/

class AlbumsFragment : MediaItemFragment() {
    private lateinit var albumAdapter: AlbumAdapter
    private val sortOrderPref by inject<Pref<AlbumSortOrder>>(named(PREF_ALBUM_SORT_ORDER))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflateTo(R.layout.music_layout_recyclerview_padding, container)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        albumAdapter = AlbumAdapter().apply {
            showHeader = true
            sortMenuListener = sortListener
        }

        recyclerView.apply {
            val gridSpan = resources.getInteger(R.integer.grid_span)
            layoutManager = GridLayoutManager(safeActivity, gridSpan).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == 0) 2 else 1
                    }
                }
            }
            adapter = albumAdapter
            addOnItemClick { position: Int, _: View ->
                albumAdapter.getAlbumForPosition(position)?.let {
                    mainViewModel.mediaItemClicked(it, null)
                }
            }

            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.album_art_spacing)
            addItemDecoration(SpacesItemDecoration(spacingInPixels))
        }

        mediaItemFragmentViewModel.mediaItems
                .filter { it.isNotEmpty() }
                .observe(this) { list ->
                    @Suppress("UNCHECKED_CAST")
                    albumAdapter.updateData(list as List<Album>)
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
        override fun shuffleAll() {}

        override fun sortAZ() {
            sortOrderPref.set(ALBUM_A_Z)
        }

        override fun sortDuration() {}

        override fun sortYear() {
            sortOrderPref.set(ALBUM_YEAR)
        }

        override fun sortZA() {
            sortOrderPref.set(ALBUM_Z_A)
        }

        override fun numOfSongs() {
            sortOrderPref.set(ALBUM_NUMBER_OF_SONGS)
        }
    }
}
