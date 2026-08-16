package com.practicum.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.practicum.playlistmaker.library.ui.state.FavoritesState
import com.practicum.playlistmaker.library.ui.viewmodel.FavoriteTracksViewModel
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.ui.fragment.TrackAdapter
import com.practicum.playlistmaker.search.ui.models.TrackUi
import com.practicum.playlistmaker.util.Debouncer
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {


    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteTracksViewModel by viewModel()
    private var isClickAllowed = true
    private val clickDebouncer = Debouncer<Any>(CLICK_DEBOUNCE_DELAY, lifecycleScope, false) { _ ->
        isClickAllowed = true
    }
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TrackAdapter({ track -> onTrackClick(track) })
        binding.favoritesList.adapter = adapter

        viewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is FavoritesState.Content -> showContent(it.favorites)
                is FavoritesState.Empty -> showPlaceholder()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onTrackClick(track: TrackUi) {
        if (isClickAllowed) {
            isClickAllowed = false
            clickDebouncer.invoke(Any())
            findNavController().navigate(
                R.id.action_libraryFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }
    }

    private fun showPlaceholder() {
        binding.apply {
            favoritesList.visibility = View.GONE
            favoritesPlaceholder.visibility = View.VISIBLE
        }
    }

    private fun showContent(favorites: List<TrackUi>) {
        adapter.tracks.apply {
            clear()
            addAll(favorites)
        }
        binding.apply {
            favoritesList.visibility = View.VISIBLE
            favoritesPlaceholder.visibility = View.GONE
        }

    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}