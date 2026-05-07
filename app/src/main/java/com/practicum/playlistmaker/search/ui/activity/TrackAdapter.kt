package com.practicum.playlistmaker.search.ui.activity

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.domain.models.Track

class TrackAdapter (val clickListener: TrackClickListener) : RecyclerView.Adapter<TrackViewHolder> () {

    var tracks: MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder.from(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {clickListener.onTrackClick(tracks[position])}
    }

    override fun getItemCount() = tracks.size



    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}