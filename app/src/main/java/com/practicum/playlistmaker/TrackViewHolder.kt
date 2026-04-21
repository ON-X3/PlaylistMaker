package com.practicum.playlistmaker

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val artwokrView: ImageView = itemView.findViewById(R.id.artwork)
    private val trackNameView: TextView = itemView.findViewById(R.id.trackName)
    private val trackTimeView: TextView = itemView.findViewById(R.id.trackTime)
    private val artistNameView: TextView = itemView.findViewById(R.id.artistName)

    fun bind(model: Track) {
        trackNameView.text = model.trackName.trim()
        artistNameView.text = model.artistName.trim()
        trackTimeView.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
        artwokrView.imageTintList = null
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder_45)
            .error(R.drawable.placeholder_45)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(artwokrView)
        artistNameView.requestLayout()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}