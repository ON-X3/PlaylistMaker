package com.practicum.playlistmaker.search.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackItemBinding
import com.practicum.playlistmaker.search.ui.models.TrackUi
import com.practicum.playlistmaker.util.dpToPx

class TrackViewHolder(private val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: TrackUi) {
        binding.apply {
            trackName.text = model.trackName.trim()
            artistName.text = model.artistName.trim()
            trackTime.text = model.trackTime
            artwork.imageTintList = null
            artistName.requestLayout()
        }

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder_45)
            .error(R.drawable.placeholder_45)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(binding.artwork)
    }



    companion object {
        fun from(parent: ViewGroup): TrackViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TrackItemBinding.inflate(inflater, parent, false)
            return TrackViewHolder(binding)
        }
    }
}