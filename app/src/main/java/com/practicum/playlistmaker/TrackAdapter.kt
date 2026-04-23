package com.practicum.playlistmaker

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.SearchActivity.Companion.CLICK_DEBOUNCE_DELAY

class TrackAdapter (val tracks: List<Track>, val searchHistory: SearchHistory) : RecyclerView.Adapter<TrackViewHolder> () {

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                searchHistory.addToSearchHistory(tracks[position])
                val playerIntent = Intent(it.context, PlayerActivity::class.java)
                playerIntent.putExtra("track", Gson().toJson(tracks[position]))
                it.context.startActivity(playerIntent)
            }
        }
    }

    override fun getItemCount() = tracks.size

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}