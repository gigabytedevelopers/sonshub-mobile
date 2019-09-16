package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.databinding.MusicItemGenreBinding
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Genre
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
 * Time: 2:35 PM
 * Desc: GenreAdapter
 **/

class GenreAdapter : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {
    var genres: List<Genre> = emptyList()
        private set

    fun updateData(genres: List<Genre>) {
        this.genres = genres
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflateWithBinding(R.layout.music_item_genre))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(genres[position])
    }

    override fun getItemCount() = genres.size

    class ViewHolder constructor(var binding: MusicItemGenreBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(genre: Genre) {
            binding.genre = genre
            binding.executePendingBindings()
        }
    }
}
