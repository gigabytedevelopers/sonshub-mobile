package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.databinding.MusicItemArtistBinding
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Artist
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
 * Time: 1:58 PM
 * Desc: ArtistAdapter
 **/

class ArtistAdapter : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {
    var artists: List<Artist> = emptyList()
        private set

    init {
        setHasStableIds(true)
    }

    fun updateData(artists: List<Artist>) {
        this.artists = artists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflateWithBinding(R.layout.music_item_artist))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.albumArt.setImageDrawable(null)
        holder.bind(artists[position])
    }

    override fun getItemCount() = artists.size

    override fun getItemId(position: Int) = artists[position].id

    class ViewHolder constructor(var binding: MusicItemArtistBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(artist: Artist) {
            binding.artist = artist
            binding.executePendingBindings()
        }
    }
}
