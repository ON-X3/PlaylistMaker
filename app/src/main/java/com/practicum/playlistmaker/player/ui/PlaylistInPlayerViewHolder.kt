package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistInPlayerItemBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.util.dpToPx

class PlaylistInPlayerViewHolder(private val binding: PlaylistInPlayerItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        binding.albumName.text = playlist.playlistName
        binding.trackCount.text =
            itemView.resources.getQuantityString(R.plurals.tracks, playlist.count, playlist.count)
        Glide.with(itemView)
            .load(playlist.imagePath)
            .placeholder(R.drawable.placeholder_45)
            .transform(CenterCrop(), RoundedCorners(dpToPx(2f, itemView.context)))
            .into(binding.album)
    }

    companion object {
        fun from(parent: ViewGroup): PlaylistInPlayerViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PlaylistInPlayerItemBinding.inflate(inflater, parent, false)
            return PlaylistInPlayerViewHolder(binding)
        }
    }

}