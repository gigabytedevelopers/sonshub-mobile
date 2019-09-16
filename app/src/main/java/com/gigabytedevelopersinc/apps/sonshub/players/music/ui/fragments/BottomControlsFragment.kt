package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ONE
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_CAST_CONNECTED
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_CAST_DISCONNECTED
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ACTION_RESTORE_MEDIA_SESSION
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.NOW_PLAYING
import com.gigabytedevelopersinc.apps.sonshub.databinding.MusicLayoutBottomsheetControlsBinding
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.addFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.hide
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.inflateWithBinding
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.map
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.observe
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.show
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.CastStatus
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.CastStatus.Companion.STATUS_PLAYING
import com.gigabytedevelopersinc.apps.sonshub.players.music.network.models.ArtworkSize
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.activities.MusicMainActivity
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.bindings.setLastFmAlbumImage
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.bindings.setPlayState
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.base.BaseNowPlayingFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.widgets.BottomSheetListener
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.AutoClearedValue
import kotlinx.android.synthetic.main.music_layout_bottomsheet_controls.*

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
 * Time: 9:51 AM
 * Desc: BottomControlsFragment
 **/

class BottomControlsFragment : BaseNowPlayingFragment(), BottomSheetListener {
    var binding by AutoClearedValue<MusicLayoutBottomsheetControlsBinding>(this)
    private var isCasting = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.music_layout_bottomsheet_controls, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.rootView.setOnClickListener {
            if (!isCasting) {
                activity.addFragment(
                        fragment = NowPlayingFragment(),
                        tag = NOW_PLAYING
                )
            }
        }

        binding.viewModel = nowPlayingViewModel
        binding.lifecycleOwner = this

        setupUI()
        setupCast()
    }

    private fun setupUI() {
        val layoutParams = progressBar.layoutParams as LinearLayout.LayoutParams
        progressBar.measure(0, 0)
        layoutParams.setMargins(0, -(progressBar.measuredHeight / 2), 0, 0)
        progressBar.layoutParams = layoutParams
        songTitle.isSelected = true

        btnTogglePlayPause.setOnClickListener {
            nowPlayingViewModel.currentData.value?.let { mediaData ->
                mainViewModel.mediaItemClicked(mediaData.toDummySong(), null)
            }
        }

        btnPlayPause.setOnClickListener {
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

        (activity as? MusicMainActivity)?.let { mainActivity ->
            btnCollapse.setOnClickListener { mainActivity.collapseBottomSheet() }
            mainActivity.setBottomSheetListener(this)
        }

        buildUIControls()
    }

    private fun buildUIControls() {
        btnLyrics.setOnClickListener {
            val currentSong = nowPlayingViewModel.currentData.value
            val artist = currentSong?.artist
            val title = currentSong?.title
            val mainActivity = activity as? MusicMainActivity
            if (artist != null && title != null && mainActivity != null) {
                mainActivity.collapseBottomSheet()
                Handler().postDelayed({
                    mainActivity.addFragment(fragment = LyricsFragment.newInstance(artist, title))
                }, 200)
            }
        }
    }

    private fun setupCast() {
        //display cast data directly if casting instead of databinding
        val castProgressObserver = Observer<Pair<Long, Long>> {
            binding.progressBar.progress = it.first.toInt()
            if (binding.progressBar.max != it.second.toInt())
                binding.progressBar.max = it.second.toInt()

            binding.seekBar.progress = it.first.toInt()
            if (binding.seekBar.max != it.second.toInt())
                binding.seekBar.max = it.second.toInt()
        }

        val castStatusObserver = Observer<CastStatus> {
            it ?: return@Observer
            if (it.isCasting) {
                isCasting = true

                mainViewModel.castProgressLiveData.observe(this, castProgressObserver)
                bottomContolsAlbumart.setBackgroundResource(0)
                setLastFmAlbumImage(binding.bottomContolsAlbumart, it.castSongArtist, it.castSongAlbum, ArtworkSize.SMALL, it.castAlbumId.toLong())

                binding.songArtist.text = getString(R.string.casting_to_x, it.castDeviceName)
                if (it.castSongId == -1) {
                    binding.songTitle.text = getString(R.string.nothing_playing)
                } else {
                    binding.songTitle.text =
                            getString(R.string.now_playing_format, it.castSongTitle, it.castSongArtist)
                }

                if (it.state == STATUS_PLAYING) {
                    setPlayState(binding.btnTogglePlayPause, STATE_PLAYING)
                    setPlayState(binding.btnPlayPause, STATE_PLAYING)
                } else {
                    setPlayState(binding.btnTogglePlayPause, STATE_PAUSED)
                    setPlayState(binding.btnPlayPause, STATE_PAUSED)
                }
            } else {
                isCasting = false
                mainViewModel.castProgressLiveData.removeObserver(castProgressObserver)
            }
        }

        mainViewModel.customAction
                .map { it.peekContent() }
                .observe(this) {
                    when (it) {
                        ACTION_CAST_CONNECTED -> {
                            mainViewModel.castLiveData.observe(this, castStatusObserver)
                        }
                        ACTION_CAST_DISCONNECTED -> {
                            isCasting = false
                            mainViewModel.castLiveData.removeObserver(castStatusObserver)
                            mainViewModel.transportControls().sendCustomAction(ACTION_RESTORE_MEDIA_SESSION, null)
                        }
                    }
                }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        if (slideOffset > 0) {
            btnPlayPause.hide()
            progressBar.hide()
            btnCollapse.show()
        } else {
            progressBar.show()
        }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        if (newState == STATE_DRAGGING || newState == STATE_EXPANDED) {
            btnPlayPause.hide()
            btnCollapse.show()
            //disable expanded controls when casting as we don't support next/previous yet
            if (isCasting) {
                (activity as MusicMainActivity).collapseBottomSheet()
            }
        } else if (newState == STATE_COLLAPSED) {
            btnPlayPause.show()
            btnCollapse.hide()
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.mediaController.observe(this) { mediaController ->
            progressBar.setMediaController(mediaController)
            progressText.setMediaController(mediaController)
            seekBar.setMediaController(mediaController)
        }
    }

    override fun onStop() {
        progressBar.disconnectController()
        progressText.disconnectController()
        seekBar.disconnectController()
        super.onStop()
    }
}
