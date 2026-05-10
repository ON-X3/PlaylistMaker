package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.search.ui.models.TrackUi
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        val track = intent.getParcelableExtra<TrackUi>("track")!!

        viewModel = getViewModel { parametersOf(track.previewUrl) }


        binding.playButton.isEnabled = false
        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        viewModel.observePlayerState().observe(this) {
            changeButtonImage(it == PlayerViewModel.STATE_PLAYING)
            enableButton(it != PlayerViewModel.STATE_DEFAULT)
        }

        viewModel.observePlayProgress().observe(this) {
            binding.playProgress.text = it
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        bindTrackInfo(track)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun changeButtonImage(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playButton.setBackgroundResource(R.drawable.button_pause_100)
        } else {
            binding.playButton.setBackgroundResource(R.drawable.button_play_100)
        }
    }

    private fun enableButton(isEnabled: Boolean) {
        binding.playButton.isEnabled = isEnabled
    }

    private fun bindTrackInfo(track: TrackUi) {
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_45)
            .error(R.drawable.placeholder_45)
            .transform(RoundedCorners(dpToPx(8f, this)))
            .into(findViewById(R.id.artwork))

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTimeValue.text = track.trackTime
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country

        if (track.collectionName != null) {
            binding.albumValue.text = track.collectionName
        } else {
            binding.albumValue.visibility = View.GONE
            binding.album.visibility = View.GONE
        }

        if (track.releaseDate != null) {
            binding.releaseDateValue.text = track.releaseDate.substring(0, 4)
        } else {
            binding.releaseDateValue.visibility = View.GONE
            binding.releaseDate.visibility = View.GONE
        }
    }
}
