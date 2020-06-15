package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments

import android.animation.AnimatorInflater.loadStateListAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.afollestad.rxkprefs.Pref
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.PREF_START_PAGE
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.StartPage
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.addFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.inflateTo
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.safeActivity
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.MediaID
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ALL_ALBUMS
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ALL_ARTISTS
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ALL_GENRES
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.TimberMusicService.Companion.TYPE_ALL_SONGS
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.activities.MusicMainActivity
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.base.MediaItemFragment
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.music_main_fragment.*
import kotlinx.android.synthetic.main.music_toolbar.*
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
 * Date: 12 Feb, 2019
 * Time: 3:21 AM
 * Desc: MainFragment
 **/

class MainFragment : Fragment() {
    private val startPagePref by inject<Pref<StartPage>>(named(PREF_START_PAGE))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflateTo(R.layout.music_main_fragment, container)

    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setHasOptionsMenu(true)

        setupViewPager(viewpager)
        tabLayout.setupWithViewPager(viewpager)

        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val animatorRes = if (verticalOffset == 0) {
                R.anim.appbar_elevation_disable
            } else {
                R.anim.appbar_elevation_enable
            }
            appBar.stateListAnimator = loadStateListAnimator(context, animatorRes)
        })

//        toolbar.overflowIcon = safeActivity.drawable(R.drawable.music_ic_more_vert_black_24dp)

        val mainActivity = safeActivity as MusicMainActivity
        mainActivity.setSupportActionBar(toolbar)
        mainActivity.supportActionBar?.run {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(false)
        }

        btnSearch.setOnClickListener { safeActivity.addFragment(fragment = SearchFragment()) }
    }

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (safeActivity as MusicMainActivity).setupCastButton(mediaRouteButton)
    }*/

    private fun setupViewPager(viewPager: ViewPager) {
        val res = context?.resources ?: return
        val adapter = Adapter(childFragmentManager).apply {
            addFragment(
                    fragment = MediaItemFragment.newInstance(MediaID(TYPE_ALL_SONGS.toString(), null)),
                    title = res.getString(R.string.songs)
            )
            addFragment(
                    fragment = MediaItemFragment.newInstance(MediaID(TYPE_ALL_ALBUMS.toString(), null)),
                    title = res.getString(R.string.albums)
            )
            /*addFragment(
                    fragment = MediaItemFragment.newInstance(MediaID(TYPE_ALL_PLAYLISTS.toString(), null)),
                    title = res.getString(R.string.playlists)
            )*/
            addFragment(
                    fragment = MediaItemFragment.newInstance(MediaID(TYPE_ALL_ARTISTS.toString(), null)),
                    title = res.getString(R.string.artists)
            )
            /*addFragment(
                    fragment = MediaItemFragment.newInstance(MediaID(TYPE_ALL_FOLDERS.toString(), null)),
                    title = res.getString(R.string.folders)
            )*/
            addFragment(
                    fragment = MediaItemFragment.newInstance(MediaID(TYPE_ALL_GENRES.toString(), null)),
                    title = res.getString(R.string.genres)
            )
        }
        viewPager.adapter = adapter
        viewpager.offscreenPageLimit = 1
        viewPager.setCurrentItem(startPagePref.get().index, false)
    }

    internal class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val fragments = ArrayList<Fragment>()
        private val titles = ArrayList<String>()

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getItem(position: Int) = fragments[position]

        override fun getCount() = fragments.size

        override fun getPageTitle(position: Int) = titles[position]
    }
}
