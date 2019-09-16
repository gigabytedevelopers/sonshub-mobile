package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaBrowserCompat.SubscriptionCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gigabytedevelopersinc.apps.sonshub.players.music.playback.MediaSessionConnection
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.MediaID

class MediaItemFragmentViewModel(
    private val mediaId: MediaID,
    mediaSessionConnection: MediaSessionConnection
) : ViewModel() {

    private val _mediaItems = MutableLiveData<List<MediaBrowserCompat.MediaItem>>()
            .apply { postValue(emptyList()) }

    val mediaItems: LiveData<List<MediaItem>> = _mediaItems

    private val subscriptionCallback = object : SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: List<MediaItem>) {
            _mediaItems.postValue(children)
        }
    }

    private val mediaSessionConnection = mediaSessionConnection.also {
        it.subscribe(mediaId.asString(), subscriptionCallback)
    }

    //hacky way to force reload items (e.g. song sort order changed)
    fun reloadMediaItems() {
        mediaSessionConnection.unsubscribe(mediaId.asString(), subscriptionCallback)
        mediaSessionConnection.subscribe(mediaId.asString(), subscriptionCallback)
    }

    override fun onCleared() {
        super.onCleared()
        // And then, finally, unsubscribe the media ID that was being watched.
        mediaSessionConnection.unsubscribe(mediaId.asString(), subscriptionCallback)
    }
}
