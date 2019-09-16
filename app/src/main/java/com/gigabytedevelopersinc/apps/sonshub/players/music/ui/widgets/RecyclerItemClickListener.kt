package com.gigabytedevelopersinc.apps.sonshub.players.music.ui.widgets

import android.view.View
import androidx.recyclerview.widget.RecyclerView

typealias RecyclerViewItemClickListener = (position: Int, view: View) -> Unit

class RecyclerItemClickListener(
    private val mRecycler: RecyclerView,
    private val clickListener: RecyclerViewItemClickListener? = null,
    private val longClickListener: RecyclerViewItemClickListener? = null
) : RecyclerView.OnChildAttachStateChangeListener {

    override fun onChildViewDetachedFromWindow(view: View) {
        view.setOnClickListener(null)
        view.setOnLongClickListener(null)
    }

    override fun onChildViewAttachedToWindow(view: View) {
        view.setOnClickListener { v -> setOnChildAttachedToWindow(v) }
    }

    private fun setOnChildAttachedToWindow(v: View?) {
        if (v != null) {
            val position = mRecycler.getChildLayoutPosition(v)
            if (position >= 0) {
                clickListener?.invoke(position, v)
                longClickListener?.invoke(position, v)
            }
        }
    }
}
