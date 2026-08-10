package com.practicum.playlistmaker.player.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.library.domain.models.Playlist

class PlaylistInPlayerAdapter(private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<PlaylistInPlayerViewHolder>() {

    var playlists: MutableList<Playlist> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistInPlayerViewHolder =
        PlaylistInPlayerViewHolder.from(parent)

    override fun onBindViewHolder(holder: PlaylistInPlayerViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { itemClickListener.onItemClick(playlists[position]) }
    }

    override fun getItemCount() = playlists.size


    fun interface ItemClickListener {
        fun onItemClick(playlist: Playlist)
    }

}