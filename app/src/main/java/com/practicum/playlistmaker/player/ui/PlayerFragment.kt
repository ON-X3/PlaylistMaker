package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.search.ui.models.TrackUi
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlayerViewModel

    companion object {
        private const val ARGS_TRACK = "track"

        fun createArgs(track: TrackUi): Bundle = Bundle().apply {
            putParcelable(ARGS_TRACK, track)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = requireArguments().getParcelable<TrackUi>(ARGS_TRACK)!!

        viewModel = getViewModel { parametersOf(track) }


        binding.playButton.isEnabled = false
        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.favoritesButton.setOnClickListener {
            viewModel.onFavoritesClick()
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            changeButtonImage(it is PlayerState.StatePlaying)
            enableButton(it.isPlayButtonAvailable)
            binding.playProgress.text = it.playProgress

            if (it.isFavorite) {
                binding.favoritesButton.setBackgroundResource(R.drawable.button_in_favorites)
            } else {
                binding.favoritesButton.setBackgroundResource(R.drawable.button_not_in_favorites)
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        bindTrackInfo(track)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            .transform(RoundedCorners(dpToPx(8f, requireContext())))
            .into(binding.artwork)

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