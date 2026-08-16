package com.practicum.playlistmaker.library.ui.fragment

import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.library.ui.state.PlaylistState
import com.practicum.playlistmaker.library.ui.viewmodel.PlaylistViewModel
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.ui.fragment.TrackAdapter
import com.practicum.playlistmaker.search.ui.models.TrackUi
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.util.dpToPx
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.util.Locale

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PlaylistViewModel

    private lateinit var adapter: TrackAdapter

    private lateinit var moreBehavior: BottomSheetBehavior<View>
    private lateinit var moreBottomSheetCallback: BottomSheetBehavior.BottomSheetCallback

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist"

        fun createArgs(playlistId: Int): Bundle =
            Bundle().apply { putInt(ARGS_PLAYLIST_ID, playlistId) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = getViewModel { parametersOf(requireArguments().getInt(ARGS_PLAYLIST_ID)) }


        adapter = TrackAdapter(
            { track ->
                onClick(track)
            },
            { track ->
                onLongClick(track)
            })

        binding.tracksList.adapter = adapter
        moreBehavior = BottomSheetBehavior.from(binding.moreBottomSheet)
        moreBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        moreBehavior.peekHeight = (resources.displayMetrics.heightPixels * 0.5).toInt()

        viewModel.observePlaylist().observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistState.EmptyPlaylist -> bindEmptyPlaylist(it)
                is PlaylistState.PlaylistWithTracks -> bindPlaylistWithTracks(it)
            }
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            if (it) {
                moreBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(
                    requireContext(),
                    R.string.share_empty_playlist_toast,
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.shareButton.setOnClickListener {
            viewModel.sharePlaylist(getFormatter())
        }

        moreBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(p0: View, state: Int) {
                binding.overlay.isVisible = state != BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onSlide(p0: View, p1: Float) {
                binding.overlay.alpha = if (p1 < 0) {
                    p1 + 1
                } else 1f
            }

        }

        moreBehavior.addBottomSheetCallback(moreBottomSheetCallback)

        binding.moreButton.setOnClickListener {
            moreBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.moreShareButton.setOnClickListener {
            viewModel.sharePlaylist(getFormatter())
        }

        binding.moreEditButton.setOnClickListener {
            Log.d(
                "EditDB", "${
                    Uri.fromFile(
                        File(viewModel.getImagePath())
                    )
                }"
            )
            findNavController().navigate(
                R.id.action_playlistFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(
                    requireArguments().getInt(ARGS_PLAYLIST_ID),
                    binding.albumName.text.toString(),
                    binding.albumDescription.text.toString(),
                    if (viewModel.getImagePath().isNotEmpty()) {
                        Uri.fromFile(
                            File(viewModel.getImagePath())
                        )
                    } else {
                        null
                    }
                )
            )
        }

        binding.moreDeleteButton.setOnClickListener {
            moreBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Dialog)
                .setTitle(R.string.delete_playlist)
                .setMessage(R.string.delete_playlist_dialog_text)
                .setNeutralButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.delete) { _, _ ->
                    viewModel.deletePlaylist()
                    findNavController().navigateUp()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        moreBehavior.removeBottomSheetCallback(moreBottomSheetCallback)
    }

    private fun bindEmptyPlaylist(playlist: PlaylistState.EmptyPlaylist) {
        bindPlaylistInfo(playlist)
        adapter.tracks.clear()

        binding.apply {
            placeholder.isVisible = true
            tracksList.isVisible = false
        }
    }

    private fun bindPlaylistWithTracks(playlist: PlaylistState.PlaylistWithTracks) {
        bindPlaylistInfo(playlist)

        adapter.tracks.apply {
            clear()
            addAll(playlist.trackList)
        }
        adapter.notifyDataSetChanged()

        binding.apply {
            placeholder.isVisible = false
            tracksList.isVisible = true
        }
    }

    private fun bindPlaylistInfo(playlist: PlaylistState) {
        binding.apply {
            albumName.text = playlist.name
            moreAlbumName.text = albumName.text
            albumDescription.text = playlist.description
            duration.text =
                resources.getQuantityString(R.plurals.minutes, playlist.duration, playlist.duration)
            trackCount.text = resources.getQuantityString(
                R.plurals.tracks,
                playlist.trackCount,
                playlist.trackCount
            )
            moreTrackCount.text = trackCount.text
        }
        binding.albumDescription.isVisible = playlist.description.isNotEmpty()
        Glide.with(this)
            .load(playlist.path)
            .placeholder(R.drawable.placeholder_45)
            .centerCrop()
            .into(binding.album)
        Glide.with(this)
            .load(playlist.path)
            .placeholder(R.drawable.placeholder_45)
            .transform(CenterCrop(), RoundedCorners(dpToPx(2f, requireContext())))
            .into(binding.moreAlbum)

        binding.root.doOnLayout {
            val behavior = BottomSheetBehavior.from(binding.bottomSheet)

            val availableHeight =
                binding.root.height - (binding.content.height - binding.content.paddingBottom + resources.getDimensionPixelSize(
                    R.dimen.large_padding
                ))

            behavior.peekHeight = availableHeight.coerceAtLeast(dpToPx(85f, requireContext()))

            binding.content.updatePadding(
                bottom = resources.getDimensionPixelSize(R.dimen.large_padding)
                        + behavior.peekHeight
            )
        }
    }

    private fun onClick(track: TrackUi) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_playerFragment,
            PlayerFragment.createArgs(track)
        )
    }

    private fun onLongClick(track: TrackUi) {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Dialog)
            .setMessage(getString(R.string.delete_track_from_playlist_dialog_title))
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteTrack(track.trackId)
            }
            .show()
    }

    private fun getFormatter(): SharingInteractor.PlaylistInfoFormatter {
        return object : SharingInteractor.PlaylistInfoFormatter {
            override fun formatTrackCount(count: Int): String =
                resources.getQuantityString(R.plurals.tracks, count, count)


            override fun formatTrackTime(duration: Long): String =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(duration)
        }
    }
}