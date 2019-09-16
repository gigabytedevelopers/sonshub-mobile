package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_QUEUE_REORDER
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.QUEUE_FROM
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.QUEUE_TO
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.addOnItemClick
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.getExtraBundle
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.inflateTo
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.keepInOrder
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.observe
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.toSongIds
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.QueueData
import com.gigabytedevelopersinc.apps.sonshub.Repository.SongsRepository
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.adapters.SongsAdapter
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.base.BaseNowPlayingFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.widgets.DragSortRecycler
import kotlinx.android.synthetic.main.music_fragment_queue.recyclerView
import kotlinx.android.synthetic.main.music_fragment_queue.tvQueueTitle
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
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
 * Date: 17 Feb, 2019
 * Time: 5:00 PM
 * Desc: QueueFragment
 **/

class QueueFragment : BaseNowPlayingFragment() {
    lateinit var adapter: SongsAdapter
    private lateinit var queueData: QueueData
    private var isReorderFromUser = false

    private val songsRepository by inject<SongsRepository>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflateTo(R.layout.music_fragment_queue, container)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = SongsAdapter().apply {
            isQueue = true
            popupMenuListener = mainViewModel.popupMenuListener
        }
        recyclerView.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = this@QueueFragment.adapter
        }

        nowPlayingViewModel.queueData.observe(this) { data ->
            this.queueData = data
            tvQueueTitle.text = data.queueTitle
            if (data.queue.isNotEmpty()) {
                fetchQueueSongs(data.queue)
            }
        }

        recyclerView.addOnItemClick { position, _ ->
            adapter.getSongForPosition(position)?.let { song ->
                val extras = getExtraBundle(adapter.songs.toSongIds(), queueData.queueTitle)
                mainViewModel.mediaItemClicked(song, extras)
            }
        }
    }

    private fun fetchQueueSongs(queue: LongArray) {
        //to avoid lag when reordering queue, we don't re-fetch queue if we know the reorder was from user
        if (isReorderFromUser) {
            isReorderFromUser = false
            return
        }

        // TODO should this logic be in a view model?
        launch {
            val songs = withContext(IO) {
                songsRepository.getSongsForIds(queue).keepInOrder(queue)
            } ?: return@launch
            adapter.updateData(songs)

            val dragSortRecycler = DragSortRecycler().apply {
                setViewHandleId(R.id.ivReorder)
                setOnItemMovedListener { from, to ->
                    isReorderFromUser = true
                    adapter.reorderSong(from, to)

                    val extras = Bundle().apply {
                        putInt(QUEUE_FROM, from)
                        putInt(QUEUE_TO, to)
                    }
                    mainViewModel.transportControls().sendCustomAction(ACTION_QUEUE_REORDER, extras)
                }
            }

            recyclerView.run {
                addItemDecoration(dragSortRecycler)
                addOnItemTouchListener(dragSortRecycler)
                addOnScrollListener(dragSortRecycler.scrollListener)
            }
        }
    }
}
