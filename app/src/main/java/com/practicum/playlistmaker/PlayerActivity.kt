package com.practicum.playlistmaker

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        val json = intent.getStringExtra("track")
        val track = Gson().fromJson(json, Track::class.java)
        val albumView = findViewById<TextView>(R.id.album_value)
        val releaseDateView = findViewById<TextView>(R.id.release_date_value)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_45)
            .error(R.drawable.placeholder_45)
            .transform(RoundedCorners(dpToPx(8f, this)))
            .into(findViewById(R.id.artwork))

        findViewById<TextView>(R.id.trackName).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        findViewById<TextView>(R.id.track_time_value).text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        findViewById<TextView>(R.id.genre_value).text = track.primaryGenreName
        findViewById<TextView>(R.id.country_value).text = track.country

        if (track.collectionName != null) {
            albumView.text = track.collectionName
        } else {
            albumView.visibility = View.GONE
            findViewById<TextView>(R.id.album).visibility = View.GONE
        }

        if (track.releaseDate != null) {
            releaseDateView.text = track.releaseDate.substring(0, 4)
        } else {
            releaseDateView.visibility = View.GONE
            findViewById<TextView>(R.id.release_date).visibility = View.GONE
        }
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}
