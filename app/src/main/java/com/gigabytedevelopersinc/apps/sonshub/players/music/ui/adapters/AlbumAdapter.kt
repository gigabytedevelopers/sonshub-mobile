package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.databinding.MusicItemAlbumBinding
import com.gigabytedevelopersinc.apps.sonshub.databinding.MusicItemAlbumsHeaderBinding
import com.gigabytedevelopersinc.apps.sonshub.databinding.MusicItemArtistAlbumBinding
import com.gigabytedevelopersinc.apps.sonshub.players.music.models.Album
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.dpToPixels
import com.gigabytedevelopersinc.apps.sonshub.players.music.extensions.inflateWithBinding
import com.gigabytedevelopersinc.apps.sonshub.players.music.ui.listeners.SortMenuListener

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
 * Time: 1:48 PM
 * Desc: AlbumAdapter
 **/

private const val TYPE_ALBUM_HEADER = 0
private const val TYPE_ALBUM_ITEM = 1

class AlbumAdapter constructor(private val isArtistAlbum: Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var showHeader = false
    var sortMenuListener: SortMenuListener? = null

    var albums: List<Album> = emptyList()
        private set

    fun updateData(albums: List<Album>) {
        this.albums = albums
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isArtistAlbum) {
            ArtistAlbumViewHolder(parent.inflateWithBinding(R.layout.music_item_artist_album))
        } else {
            return when (viewType) {
                TYPE_ALBUM_HEADER -> {
                    val viewBinding = parent.inflateWithBinding<MusicItemAlbumsHeaderBinding>(R.layout.music_item_albums_header)
                    HeaderViewHolder(viewBinding, sortMenuListener)
                }
                TYPE_ALBUM_ITEM -> {
                    val viewBinding = parent.inflateWithBinding<MusicItemAlbumBinding>(R.layout.music_item_album)
                    ViewHolder(viewBinding)
                }
                else -> {
                    val viewBinding = parent.inflateWithBinding<MusicItemAlbumBinding>(R.layout.music_item_album)
                    ViewHolder(viewBinding)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isArtistAlbum) {
            val albumHolder = holder as ArtistAlbumViewHolder
            albumHolder.run {
                artistAlbumBinding.albumTitle.setSingleLine()
                (artistAlbumBinding.rootView.layoutParams as RecyclerView.LayoutParams).rightMargin =
                        24f.dpToPixels(artistAlbumBinding.root.context)

                bind(albums[position])
            }
        } else {
            when (getItemViewType(position)) {
                TYPE_ALBUM_HEADER -> {
                    (holder as HeaderViewHolder).bind(albums.size)
                }
                TYPE_ALBUM_ITEM -> {
                    val album = albums[position + if (showHeader) -1 else 0]
                    (holder as ViewHolder).bind(album)
                }
            }
        }
    }

    override fun getItemCount() = if (showHeader) {
        albums.size + 1
    } else {
        albums.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (showHeader && position == 0) {
            TYPE_ALBUM_HEADER
        } else {
            TYPE_ALBUM_ITEM
        }
    }

    class HeaderViewHolder constructor(var binding: MusicItemAlbumsHeaderBinding, private val sortMenuListener: SortMenuListener?) : RecyclerView.ViewHolder(binding.root) {

        fun bind(count: Int) {
            binding.albumCount = count
            binding.executePendingBindings()

            binding.btnShuffle.setOnClickListener { sortMenuListener?.shuffleAll() }
            binding.sortMenu.setupMenu(sortMenuListener)
        }
    }

    class ViewHolder constructor(private val binding: MusicItemAlbumBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.albumArt.clipToOutline = true
            binding.album = album
            binding.executePendingBindings()
        }
    }

    class ArtistAlbumViewHolder constructor(val artistAlbumBinding: MusicItemArtistAlbumBinding) : RecyclerView.ViewHolder(artistAlbumBinding.root) {

        fun bind(album: Album) {
            artistAlbumBinding.albumArt.clipToOutline = true
            artistAlbumBinding.album = album
            artistAlbumBinding.executePendingBindings()
        }
    }

    fun getAlbumForPosition(position: Int): Album? {
        return if (showHeader) {
            if (position == 0) {
                null
            } else {
                albums[position - 1]
            }
        } else {
            albums[position]
        }
    }
}
