package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.viewmodels.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * A base view model that allows you to launch coroutines that cancel if they are active when the
 * view model is destroyed.
 *
 * @author Aidan Follestad (@afollestad)
 */
abstract class CoroutineViewModel(
    private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val job = Job()
    protected val scope = CoroutineScope(job + mainDispatcher)

    protected fun launch(
        context: CoroutineContext = mainDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
