package com.practicum.playlistmaker

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class TrackAdapter (val tracks: List<Track>, val searchHistory: SearchHistory) : RecyclerView.Adapter<TrackViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            searchHistory.addToSearchHistory(tracks[position])
            val playerIntent = Intent(it.context, PlayerActivity::class.java)
            playerIntent.putExtra("track", Gson().toJson(tracks[position]))
            it.context.startActivity(playerIntent)
        }
    }

    override fun getItemCount() = tracks.size
}