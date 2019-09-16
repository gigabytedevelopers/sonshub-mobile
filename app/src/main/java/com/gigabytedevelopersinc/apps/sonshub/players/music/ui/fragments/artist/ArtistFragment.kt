package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.addOnItemClick
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.filter
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.inflateTo
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.observe
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.safeActivity
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Artist
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.adapters.ArtistAdapter
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.base.MediaItemFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.SpacesItemDecoration
import kotlinx.android.synthetic.main.music_layout_recyclerview_padding.recyclerView

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
 * Time: 8:57 AM
 * Desc: ArtistFragment
 **/

class ArtistFragment : MediaItemFragment() {
    private lateinit var artistAdapter: ArtistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflateTo(R.layout.music_layout_recyclerview_padding, container)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        artistAdapter = ArtistAdapter()
        recyclerView.apply {
            val gridSpan = resources.getInteger(R.integer.grid_span)
            layoutManager = GridLayoutManager(safeActivity, gridSpan)
            adapter = artistAdapter
            addOnItemClick { position: Int, _: View ->
                mainViewModel.mediaItemClicked(artistAdapter.artists[position], null)
            }

            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.album_art_spacing)
            addItemDecoration(SpacesItemDecoration(spacingInPixels))
        }

        mediaItemFragmentViewModel.mediaItems
                .filter { it.isNotEmpty() }
                .observe(this) { list ->
                    @Suppress("UNCHECKED_CAST")
                    artistAdapter.updateData(list as List<Artist>)
                }
    }
}
