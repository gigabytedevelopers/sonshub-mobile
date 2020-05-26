package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.rxkprefs.Pref
import com.gigabytedevelopersinc.apps.sonshub.players.music.PREF_LAST_FOLDER
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.getExtraBundle
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.inflateTo
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.safeActivity
import com.gigabytedevelopersinc.apps.sonshub.Repository.FoldersRepository
import com.gigabytedevelopersinc.apps.sonshub.Repository.SongsRepository
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.adapters.FolderAdapter
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.base.MediaItemFragment
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
 * Date: 17 Feb, 2019
 * Time: 12:13 AM
 * Desc: FolderFragment
 **/

class FolderFragment : MediaItemFragment() {
    private lateinit var folderAdapter: FolderAdapter

    private val songsRepository by inject<SongsRepository>()
    private val foldersRepository by inject<FoldersRepository>()
    private val lastFolderPref by inject<Pref<String>>(named(PREF_LAST_FOLDER))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflateTo(R.layout.music_layout_recyclerview_padding, container)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        folderAdapter = FolderAdapter(safeActivity, songsRepository, foldersRepository, lastFolderPref)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = folderAdapter
        }
        folderAdapter.init(callback = { song, queueIds, title ->
            val extras = getExtraBundle(queueIds, title)
            mainViewModel.mediaItemClicked(song, extras)
        })
    }
}
