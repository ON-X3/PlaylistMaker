package com.practicum.playlistmaker.library.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.library.domain.models.Playlist

class PlaylistAdapter (private val clickListener: PlaylistClickListener) : RecyclerView.Adapter<PlaylistViewHolder>() {

    var playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistViewHolder = PlaylistViewHolder.from(parent)


    override fun onBindViewHolder(
        holder: PlaylistViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            clickListener.onPlaylistClick(playlists[position])
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}