package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.list.listItems
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.MediaID.Companion.CALLER_SELF
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Song
import com.gigabytedevelopersinc.apps.sonshub.Repository.PlaylistRepository
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.SONGS
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.toast
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
 * Date: 13 Feb, 2019
 * Time: 3:15 AM
 * Desc: AddToPlaylistDialog
 **/

class AddToPlaylistDialog : DialogFragment(), CreatePlaylistDialog.PlaylistCreatedCallback {

    companion object {
        private const val TAG = "AddToPlaylistDialog"

        fun show(activity: FragmentActivity, song: Song? = null) {
            val songs: LongArray
            if (song == null) {
                songs = LongArray(0)
            } else {
                songs = LongArray(1)
                songs[0] = song.id
            }
            show(activity, songs)
        }

        fun show(activity: FragmentActivity, songList: LongArray) {
            val dialog = AddToPlaylistDialog().apply {
                arguments = Bundle().apply { putLongArray(SONGS, songList) }
            }
            dialog.show(activity.supportFragmentManager, TAG)
        }
    }

    var callback: () -> Unit? = {
        null
    }
    private val playlistRepository by inject<PlaylistRepository>()

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = activity ?: throw IllegalStateException("Not attached")
        val playlists = playlistRepository.getPlaylists(CALLER_SELF)
        val itemList = mutableListOf<String>().apply {
            add(getString(R.string.create_new_playlist))
            addAll(playlists.map { it.name })
        }

        return MaterialDialog(context).show {
            title(R.string.add_to_playlist)
            listItems(items = itemList) { _, index, _ ->
                val songs = arguments?.getLongArray(SONGS) ?: return@listItems
                if (index == 0) {
                    CreatePlaylistDialog.show(this@AddToPlaylistDialog, songs)
                } else {
                    val inserted = playlistRepository.addToPlaylist(playlists[index - 1].id, songs)
                    val message = context.resources.getQuantityString(
                            R.plurals.NNNtrackstoplaylist, inserted, inserted)
                    context.toast(message)
                }
            }
            onDismiss {
                // Make sure the DialogFragment dismisses as well
                this@AddToPlaylistDialog.dismiss()
            }
        }
    }

    override fun onPlaylistCreated() = dismiss()
}
