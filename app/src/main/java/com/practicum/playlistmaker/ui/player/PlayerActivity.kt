package com.practicum.playlistmaker.ui.player

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
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
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var mediaPlayer = MediaPlayer()
    private lateinit var playButton: ImageButton
    private lateinit var playProgress: TextView
    private var playerState = STATE_DEFAULT
    private var handler = Handler(Looper.getMainLooper())
    private val playProgressRunnable = Runnable { startUpdatingPlayProgress() }

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

        playProgress = findViewById(R.id.play_progress)
        playButton = findViewById(R.id.play_button)
        playButton.isEnabled = false
        playButton.setOnClickListener {
            playbackControl()
        }

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
        findViewById<TextView>(R.id.track_time_value).text = track.trackTime
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

        preparePlayer(track)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(playProgressRunnable)
        mediaPlayer.release()

    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setBackgroundResource(R.drawable.button_play_100)
            handler.removeCallbacks(playProgressRunnable)
            playProgress.text = getString(R.string.demo_time)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setBackgroundResource(R.drawable.button_pause_100)
        playerState = STATE_PLAYING
        startUpdatingPlayProgress()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setBackgroundResource(R.drawable.button_play_100)
        playerState = STATE_PAUSED
        handler.removeCallbacks(playProgressRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startUpdatingPlayProgress() {
        if (playerState == STATE_PLAYING) {
            playProgress.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            handler.postDelayed(playProgressRunnable, PLAY_PROGRESS_UPDATE_DELAY)
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val PLAY_PROGRESS_UPDATE_DELAY = 500L
    }
}
