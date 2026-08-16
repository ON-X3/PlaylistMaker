package com.practicum.playlistmaker.library.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.ui.adapter.PlaylistAdapter
import com.practicum.playlistmaker.library.ui.state.PlaylistsState
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
        }

        binding.playlistsList.adapter = PlaylistAdapter { playlist ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_playlistFragment,
                PlaylistFragment.createArgs(playlist.playlistId)
            )
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistsState.Empty -> showEmpty()
                is PlaylistsState.Content -> showContent(it.playlists)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showEmpty() {
        binding.apply {
            playlistsList.visibility = View.GONE
            playlistsPlaceholder.visibility = View.VISIBLE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(playlists: List<Playlist>) {
        (binding.playlistsList.adapter as PlaylistAdapter).playlists.apply {
            clear()
            addAll(playlists)
        }
        binding.apply {
            playlistsList.adapter?.notifyDataSetChanged()
            playlistsList.visibility = View.VISIBLE
            playlistsPlaceholder.visibility = View.GONE
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

}