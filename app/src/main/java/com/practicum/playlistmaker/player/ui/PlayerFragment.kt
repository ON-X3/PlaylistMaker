package com.practicum.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.ui.models.TrackUi
import com.practicum.playlistmaker.util.dpToPx
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlayerViewModel

    private lateinit var behavior: BottomSheetBehavior<View>
    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null

    private lateinit var adapter: PlaylistInPlayerAdapter

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

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = requireArguments().getParcelable<TrackUi>(ARGS_TRACK)!!

        viewModel = getViewModel { parametersOf(track) }

        behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        behavior.peekHeight = (resources.displayMetrics.heightPixels*0.6).toInt()

        bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.overlay.visibility = View.GONE
                } else {
                    binding.overlay.visibility = View.VISIBLE
                }
            }

            override fun onSlide(p0: View, p1: Float) {
                binding.overlay.alpha = if (p1 < 0) {
                    p1+1
                } else 1f
            }

        }

        behavior.addBottomSheetCallback(bottomSheetCallback!!)

        adapter = PlaylistInPlayerAdapter { playlist ->
            onPlaylistClick(playlist)
        }

        binding.playlistsList.adapter = adapter


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

        viewModel.observePlaylists().observe(viewLifecycleOwner) {
            if (it is PlayerPlaylistsState.Content) {
                adapter.playlists.apply {
                    clear()
                    addAll(it.playlists)
                }
                adapter.notifyDataSetChanged()
                Log.d("PlayerPlaylistsDB", "In adapter: ${adapter.itemCount} items")
                binding.playlistsList.visibility = View.VISIBLE
            } else {
                binding.playlistsList.visibility = View.GONE
            }
        }

        viewModel.observeIsTrackAdded().observe(viewLifecycleOwner) {
            if (it.first) {
                Toast.makeText(requireContext(), getString(R.string.successfully_added_to_playlist)+" ${it.second}",
                    Toast.LENGTH_LONG).show()
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                Toast.makeText(requireContext(), getString(R.string.already_added_to_playlist)+" ${it.second}",
                    Toast.LENGTH_LONG).show()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.addToPlaylistButton.setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }

        bindTrackInfo(track)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        behavior.removeBottomSheetCallback(bottomSheetCallback!!)
        _binding = null
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

    private fun onPlaylistClick(playlist: Playlist) {
        viewModel.addToPlaylist(playlist)
    }
}