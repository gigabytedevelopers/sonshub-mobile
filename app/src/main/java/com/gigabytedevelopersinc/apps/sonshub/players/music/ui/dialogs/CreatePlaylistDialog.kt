package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.input.input
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.SONGS
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.toast
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Song
import com.gigabytedevelopersinc.apps.sonshub.Repository.PlaylistRepository
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
 * Time: 4:35 AM
 * Desc: CreatePlaylistDialog
 **/

class CreatePlaylistDialog : DialogFragment() {
    interface PlaylistCreatedCallback {
        fun onPlaylistCreated()
    }

    companion object {
        private const val TAG = "CreatePlaylistDialog"

        fun <T> show(parent: T, song: Song? = null) where T : Fragment, T : PlaylistCreatedCallback {
            val songs: LongArray
            if (song == null) {
                songs = LongArray(0)
            } else {
                songs = LongArray(1)
                songs[0] = song.id
            }
            show(parent, songs)
        }

        fun <T> show(parent: T, songList: LongArray) where T : Fragment, T : PlaylistCreatedCallback {
            val dialog = CreatePlaylistDialog().apply {
                arguments = Bundle().apply { putLongArray(SONGS, songList) }
                setTargetFragment(parent, 69)
            }
            dialog.show(parent.fragmentManager!!, TAG)
        }
    }

    private val playlistsRepository by inject<PlaylistRepository>()

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = activity ?: throw IllegalStateException("Not attached")
        val songs = arguments?.getLongArray(SONGS)

        return MaterialDialog(context).show {
            title(R.string.create_new_playlist)
            positiveButton(R.string.create)
            negativeButton(android.R.string.cancel)

            input(hintRes = R.string.enter_playlist_name, callback = { _, text ->
                val playlistId = playlistsRepository.createPlaylist(text.toString())
                if (playlistId != -1L) {
                    if (songs != null && songs.isNotEmpty()) {
                        val inserted = playlistsRepository.addToPlaylist(playlistId, songs)
                        val message = context.resources.getQuantityString(
                                R.plurals.NNNtrackstoplaylist, inserted, inserted)
                        context.toast(message)
                    } else {
                        context.toast(R.string.playlist_created)
                    }
                    (targetFragment as? PlaylistCreatedCallback)?.onPlaylistCreated()
                } else {
                    context.toast(R.string.unable_create_playlist)
                }
            })

            onDismiss {
                // Make sure the DialogFragment dismisses as well
                this@CreatePlaylistDialog.dismiss()
            }
        }
    }
}
