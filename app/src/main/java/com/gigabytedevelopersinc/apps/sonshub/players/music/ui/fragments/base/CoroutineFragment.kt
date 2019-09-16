@file:Suppress("MemberVisibilityCanBePrivate")

package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.fragments.base

import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

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
 * Time: 9:59 AM
 * Desc: CoroutineFragment
 *
 * A base Fragment that allows you to launch coroutines that cancel if they are active when the Fragment is destroyed.
 **/

abstract class CoroutineFragment : Fragment() {

    protected val mainDispatcher: CoroutineDispatcher get() = Main

    private val job = Job()
    protected val scope = CoroutineScope(job + mainDispatcher)

    protected fun launch(
        context: CoroutineContext = mainDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)

    override fun onDestroyView() {
        job.cancel()
        super.onDestroyView()
    }
}
