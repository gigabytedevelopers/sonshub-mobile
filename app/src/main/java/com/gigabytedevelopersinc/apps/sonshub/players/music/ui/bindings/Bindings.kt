package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.bindings

import android.graphics.Bitmap
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ONE
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.gigabytedevelopersinc.apps.sonshub.R
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.Utils
import com.gigabytedevelopersinc.apps.sonshub.players.music.util.Utils.getAlbumArtUri

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
 * Time: 2:57 AM
 * Desc: Bindings
 **/

val IMAGE_ROUND_CORNERS_TRANSFORMER: Transformation<Bitmap>
    get() = RoundedCorners(2)

val LARGE_IMAGE_ROUND_CORNERS_TRANSFORMER: Transformation<Bitmap>
    get() = RoundedCorners(5)

val EXTRA_LARGE_IMAGE_ROUND_CORNERS_TRANSFORMER: Transformation<Bitmap>
    get() = RoundedCorners(8)

@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView, albumId: Long) {
    val size = view.resources.getDimensionPixelSize(R.dimen.album_art)
    val options = RequestOptions()
            .centerCrop()
            .override(size, size)
            .transform(IMAGE_ROUND_CORNERS_TRANSFORMER)
            .placeholder(R.drawable.music_ic_music_note)
    Glide.with(view)
            .load(getAlbumArtUri(albumId))
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(options)
            .into(view)
}

@BindingAdapter("playState")
fun setPlayState(view: ImageView, state: Int) {
    if (state == STATE_PLAYING) {
        view.setImageResource(R.drawable.music_ic_pause_outline)
    } else {
        view.setImageResource(R.drawable.music_ic_play_outline)
    }
}

@BindingAdapter("repeatMode")
fun setRepeatMode(view: ImageView, mode: Int) {
    when (mode) {
        REPEAT_MODE_NONE -> view.setImageResource(R.drawable.music_ic_repeat_none)
        REPEAT_MODE_ONE -> view.setImageResource(R.drawable.music_ic_repeat_one)
        REPEAT_MODE_ALL -> view.setImageResource(R.drawable.music_ic_repeat_all)
        else -> view.setImageResource(R.drawable.music_ic_repeat_none)
    }
}

@BindingAdapter("shuffleMode")
fun setShuffleMode(view: ImageView, mode: Int) {
    when (mode) {
        SHUFFLE_MODE_NONE -> view.setImageResource(R.drawable.music_ic_shuffle_none)
        SHUFFLE_MODE_ALL -> view.setImageResource(R.drawable.music_ic_shuffle_all)
        else -> view.setImageResource(R.drawable.music_ic_shuffle_none)
    }
}

@BindingAdapter("duration")
fun setDuration(view: TextView, duration: Int) {
    view.text = Utils.makeShortTimeString(view.context, duration.toLong() / 1000)
}
