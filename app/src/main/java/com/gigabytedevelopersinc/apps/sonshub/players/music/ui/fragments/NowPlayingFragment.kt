package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat.*
import android.view.*
import androidx.core.content.ContextCompat
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.Repository.SongsRepository
import com.gigabytedevelopersinc.apps.sonshub.databinding.MusicFragmentNowPlayingBinding
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.addFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.inflateWithBinding
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.observe
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.safeActivity
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.QueueData
import com.gigabytedevelopersinc.apps.sonshub.players.music.network.models.ArtworkSize
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.bindings.setLastFmAlbumImage
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.base.BaseNowPlayingFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.AutoClearedValue
import kotlinx.android.synthetic.main.music_fragment_now_playing.*
import org.koin.android.ext.android.inject
import kotlin.math.absoluteValue

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
 * Time: 3:11 PM
 * Desc: NowPlayingFragment
 **/

class NowPlayingFragment : BaseNowPlayingFragment(), GestureDetector.OnGestureListener {
    var binding by AutoClearedValue<MusicFragmentNowPlayingBinding>(this)
    private var queueData: QueueData? = null

    private val songsRepository by inject<SongsRepository>()

    private lateinit var gestureDetector: GestureDetector
    private val minFlingVelocity = 800

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.music_fragment_now_playing, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        binding.let {
            it.viewModel = nowPlayingViewModel
            it.lifecycleOwner = this

            nowPlayingViewModel.currentData.observe(this) { setNextData() }
            nowPlayingViewModel.queueData.observe(this) { queueData ->
                this.queueData = queueData
                setNextData()
            }
        }
        setupUI()
    }

    //TODO this should not here, move it to BindingAdapter or create a separate queue view model
    private fun setNextData() {
        //upNextAlbumArt.setImageResource(R.drawable.sonshub_icon)
        val queue = queueData?.queue ?: return
        if (queue.isNotEmpty() && nowPlayingViewModel.currentData.value != null) {

            val currentIndex = queue.indexOf(nowPlayingViewModel.currentData.value!!.mediaId!!.toLong())
            if (currentIndex + 1 < queue.size) {
                val nextSong = songsRepository.getSongForId(queue[currentIndex + 1])
                setLastFmAlbumImage(upNextAlbumArt, nextSong.artist, nextSong.album, ArtworkSize.MEDIUM, nextSong.albumId)
                upNextTitle.text = nextSong.title
                upNextArtist.text = nextSong.artist
            } else {
                //nothing up next, show same
                upNextAlbumArt.setBackgroundResource(R.drawable.music_default_album_art_small_queue)
                upNextAlbumArt.setImageResource(R.drawable.music_ic_music_note)
                upNextAlbumArt.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                upNextTitle.text = getString(R.string.queue_ended)
                upNextArtist.text = getString(R.string.no_song_next)
            }
        }
    }

    private fun setupUI() {
        songTitle.isSelected = true
        btnTogglePlayPause.setOnClickListener {
            nowPlayingViewModel.currentData.value?.let { mediaData ->
                mainViewModel.mediaItemClicked(mediaData.toDummySong(), null)
            }
        }
        btnNext.setOnClickListener {
            mainViewModel.transportControls().skipToNext()
        }
        btnPrevious.setOnClickListener {
            mainViewModel.transportControls().skipToPrevious()
        }

        btnRepeat.setOnClickListener {
            when (nowPlayingViewModel.currentData.value?.repeatMode) {
                REPEAT_MODE_NONE -> mainViewModel.transportControls().setRepeatMode(REPEAT_MODE_ONE)
                REPEAT_MODE_ONE -> mainViewModel.transportControls().setRepeatMode(REPEAT_MODE_ALL)
                REPEAT_MODE_ALL -> mainViewModel.transportControls().setRepeatMode(REPEAT_MODE_NONE)
            }
        }
        btnShuffle.setOnClickListener {
            when (nowPlayingViewModel.currentData.value?.shuffleMode) {
                SHUFFLE_MODE_NONE -> mainViewModel.transportControls().setShuffleMode(SHUFFLE_MODE_ALL)
                SHUFFLE_MODE_ALL -> mainViewModel.transportControls().setShuffleMode(SHUFFLE_MODE_NONE)
            }
        }

        btnQueue.setOnClickListener { safeActivity.addFragment(fragment = QueueFragment()) }
        btnBack.setOnClickListener { safeActivity.onBackPressed() }

        buildUIControls()
        setupSwipeGestures()
    }

    private fun buildUIControls() {
        btnLyrics.setOnClickListener {
            val currentSong = nowPlayingViewModel.currentData.value
            val artist = currentSong?.artist
            val title = currentSong?.title
            if (artist != null && title != null) {
                safeActivity.addFragment(fragment = LyricsFragment.newInstance(artist, title))
            }
        }
    }

    private fun setupSwipeGestures() {
        gestureDetector = GestureDetector(activity, this)
        frag_now_playing_rl.setOnTouchListener(touchListener)
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_about -> AboutDialog.show(safeActivity)
        }
        return super.onOptionsItemSelected(item)
    }*/

    override fun onResume() {
        super.onResume()
        mainViewModel.mediaController.observe(this) { mediaController ->
            progressText.setMediaController(mediaController)
            seekBar.setMediaController(mediaController)
        }
    }

    override fun onStop() {
        progressText.disconnectController()
        seekBar.disconnectController()
        super.onStop()
    }

    var touchListener: View.OnTouchListener = View.OnTouchListener {
            _: View, motionEvent: MotionEvent -> gestureDetector.onTouchEvent(motionEvent)
    }

    override fun onDown(event: MotionEvent): Boolean {
        return true
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (velocityX.absoluteValue > minFlingVelocity) {
            if (velocityX < 0) {
                mainViewModel.transportControls().skipToNext()
            } else {
                mainViewModel.transportControls().skipToPrevious()
            }
        }
        return true
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent) {
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return true
    }
}
