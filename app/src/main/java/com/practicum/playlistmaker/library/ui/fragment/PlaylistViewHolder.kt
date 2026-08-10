package com.practicum.playlistmaker.library.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistItemBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.util.dpToPx

class PlaylistViewHolder(private val binding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) {
        binding.apply {
            albumName.text = playlist.playlistName
            trackCount.text = itemView.resources.getQuantityString(R.plurals.tracks, playlist.count, playlist.count)
        }


        Glide.with(itemView)
            .load(playlist.imagePath)
            .placeholder(R.drawable.placeholder_45)
            .transform(CenterCrop(), RoundedCorners(dpToPx(8f, itemView.context)))
            .into(binding.album)

    }

    companion object {
        fun from(parent: ViewGroup): PlaylistViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PlaylistItemBinding.inflate(inflater, parent, false)
            return PlaylistViewHolder(binding)
        }
    }

}