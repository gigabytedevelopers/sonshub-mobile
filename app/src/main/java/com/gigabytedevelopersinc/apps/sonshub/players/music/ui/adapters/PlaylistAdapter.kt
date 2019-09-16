package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.databinding.MusicItemPlaylistBinding
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Playlist
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.inflateWithBinding

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Wednesday, 06
 * Month: February
 * Year: 2019
 * Date: 14 Feb, 2019
 * Time: 2:42 PM
 * Desc: PlaylistAdapter
 **/

class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {
    var playlists: List<Playlist> = emptyList()
        private set

    fun updateData(playlists: List<Playlist>) {
        this.playlists = playlists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflateWithBinding(R.layout.music_item_playlist))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount() = playlists.size

    class ViewHolder constructor(var binding: MusicItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.playlist = playlist
            binding.executePendingBindings()
        }
    }
}
