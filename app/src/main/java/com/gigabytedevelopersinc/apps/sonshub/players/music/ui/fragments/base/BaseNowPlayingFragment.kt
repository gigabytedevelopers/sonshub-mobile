package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.base

import android.os.Bundle
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.observe
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.safeActivity
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.activities.MusicMainActivity
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.NowPlayingFragment
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.viewmodels.MainViewModel
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.viewmodels.NowPlayingViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

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
 * Time: 9:11 AM
 * Desc: BaseNowPlayingFragment
 **/

open class BaseNowPlayingFragment : CoroutineFragment() {

    protected val mainViewModel by sharedViewModel<MainViewModel>()
    protected val nowPlayingViewModel by sharedViewModel<NowPlayingViewModel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        nowPlayingViewModel.currentData.observe(this) { showHideBottomSheet() }
    }

    override fun onPause() {
        showHideBottomSheet()
        super.onPause()
    }

    private fun showHideBottomSheet() {
        val activity = safeActivity as MusicMainActivity
        nowPlayingViewModel.currentData.value?.let {
            if (!it.title.isNullOrEmpty()) {
                if (activity.supportFragmentManager.findFragmentById(R.id.container) is NowPlayingFragment) {
                    activity.hideBottomSheet()
                } else {
                    activity.showBottomSheet()
                }
            } else {
                activity.hideBottomSheet()
            }
        }
    }
}
