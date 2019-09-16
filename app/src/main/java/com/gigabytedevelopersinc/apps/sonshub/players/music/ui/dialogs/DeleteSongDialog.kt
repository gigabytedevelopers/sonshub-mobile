package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Song
import com.gigabytedevelopersinc.apps.sonshub.players.music.constants.Constants.SONGS
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.toast
import com.gigabytedevelopersinc.apps.sonshub.Repository.SongsRepository
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.Utils
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
 * Time: 4:55 AM
 * Desc: DeleteSongDialog
 **/

class DeleteSongDialog : DialogFragment() {
    interface OnSongDeleted {
        fun onSongDeleted(songId: Long)
    }

    private val songsRepository by inject<SongsRepository>()

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialDialog(activity!!).show {
            title(R.string.delete_song_prompt)
            positiveButton(R.string.delete) {
                val songs = arguments?.getLongArray(SONGS) ?: return@positiveButton
                val deleted = songsRepository.deleteTracks(songs)
                val message = Utils.makeLabel(context, R.plurals.NNNtracksdeleted, deleted)
                context.toast(message)
                (activity as? OnSongDeleted)?.onSongDeleted(songs.single())
            }
            negativeButton(android.R.string.cancel)
            onDismiss {
                // Make sure the DialogFragment dismisses as well
                this@DeleteSongDialog.dismiss()
            }
        }
    }

    companion object {
        private const val TAG = "DeleteSongDialog"

        fun <T> show(activity: T, song: Song? = null) where T : FragmentActivity, T : OnSongDeleted {
            val songs: LongArray
            if (song == null) {
                songs = LongArray(0)
            } else {
                songs = LongArray(1)
                songs[0] = song.id
            }
            show(activity, songs)
        }

        fun <T> show(activity: T, songList: LongArray) where T : FragmentActivity, T : OnSongDeleted {
            val dialog = DeleteSongDialog().apply {
                arguments = Bundle().apply { putLongArray(SONGS, songList) }
            }
            dialog.show(activity.supportFragmentManager, TAG)
        }
    }
}
