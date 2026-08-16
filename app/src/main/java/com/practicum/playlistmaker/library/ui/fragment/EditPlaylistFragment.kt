package com.practicum.playlistmaker.library.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.ui.state.NewPlaylistState
import com.practicum.playlistmaker.library.ui.viewmodel.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : NewPlaylistFragment() {

    companion object {
        private const val ARGS_PLAYLIST_ID = "id"
        private const val ARGS_PLAYLIST_NAME = "name"
        private const val ARGS_PLAYLIST_DESCRIPTION = "description"
        private const val ARGS_PLAYLIST_IMAGE = "image"

        fun createArgs(playlistId: Int, playlistName: String, playlistDescription: String, imageUri: Uri?): Bundle =
            Bundle().apply {
                putInt(ARGS_PLAYLIST_ID, playlistId)
                putString(ARGS_PLAYLIST_NAME, playlistName)
                putString(ARGS_PLAYLIST_DESCRIPTION, playlistDescription)
                putParcelable(ARGS_PLAYLIST_IMAGE, imageUri)
            }
    }

    override lateinit var viewModel: EditPlaylistViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = getViewModel {
            parametersOf(
                NewPlaylistState(
                    requireArguments().getParcelable(ARGS_PLAYLIST_IMAGE),
                    requireArguments().getString(ARGS_PLAYLIST_NAME).toString(),
                    requireArguments().getString(ARGS_PLAYLIST_DESCRIPTION).toString(),
                    true
                ), requireArguments().getInt(ARGS_PLAYLIST_ID)
            )
        }
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setTitle(R.string.edit)
        binding.albumName.setText(requireArguments().getString(ARGS_PLAYLIST_NAME))
        binding.albumDescription.setText(requireArguments().getString(ARGS_PLAYLIST_DESCRIPTION))
        binding.createPlaylistButton.apply {
            setText(R.string.save)
            setOnClickListener {
                viewModel.savePlaylist()
                findNavController().navigateUp()
            }
        }

    }

    override fun onBackClick(dialog: MaterialAlertDialogBuilder) {
        findNavController().navigateUp()
    }

}