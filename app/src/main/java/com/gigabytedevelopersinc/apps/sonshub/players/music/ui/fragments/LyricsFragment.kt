package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.ARTIST
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.SONG
import com.gigabytedevelopersinc.apps.sonshub.databinding.MusicFragmentLyricsBinding
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.argument
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.disposeOnDetach
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.inflateWithBinding
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.ioToMain
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.subscribeForOutcome
import com.gigabytedevelopersinc.apps.sonshub.players.music.network.Outcome
import com.gigabytedevelopersinc.apps.sonshub.players.music.network.api.LyricsRestService
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.base.BaseNowPlayingFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.AutoClearedValue
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
 * Time: 1:48 AM
 * Desc: LyricsFragment
 **/

class LyricsFragment : BaseNowPlayingFragment() {
    companion object {
        fun newInstance(artist: String, title: String): LyricsFragment {
            return LyricsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARTIST, artist)
                    putString(SONG, title)
                }
            }
        }
    }

    private lateinit var artistName: String
    lateinit var songTitle: String
    var binding by AutoClearedValue<MusicFragmentLyricsBinding>(this)

    private val lyricsService by inject<LyricsRestService>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.music_fragment_lyrics, container)
        artistName = argument(ARTIST)
        songTitle = argument(SONG)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.songTitle = songTitle

        // TODO make the lyrics handler/repo injectable
        lyricsService.getLyrics(artistName, songTitle)
                .ioToMain()
                .subscribeForOutcome { outcome ->
                    when (outcome) {
                        is Outcome.Success -> binding.lyrics = outcome.data
                    }
                }
                .disposeOnDetach(view)
    }
}
