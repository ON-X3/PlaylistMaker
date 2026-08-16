package com.practicum.playlistmaker.search.ui.fragment

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.ui.models.TrackUi

class TrackAdapter(
    val clickListener: TrackClickListener,
    val longClickListener: TrackLongClickListener? = null
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var tracks: MutableList<TrackUi> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder.from(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(tracks[position]) }
        if (longClickListener != null) {
            holder.itemView.setOnLongClickListener {
                longClickListener.onTrackLongClick(tracks[position])
                true
            }
        }
    }

    override fun getItemCount() = tracks.size


    fun interface TrackClickListener {
        fun onTrackClick(track: TrackUi)
    }

    fun interface TrackLongClickListener {
        fun onTrackLongClick(track: TrackUi)
    }
}