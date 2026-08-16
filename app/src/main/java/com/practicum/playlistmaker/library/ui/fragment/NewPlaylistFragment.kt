package com.practicum.playlistmaker.library.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.library.ui.viewmodel.NewPlaylistViewModel
import com.practicum.playlistmaker.util.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel

open class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    protected val binding get() = _binding!!
    open val viewModel: NewPlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Dialog)
            .setTitle(getString(R.string.close_new_playlist_dialog_title))
            .setMessage(getString(R.string.close_new_playlist_dialog_text))
            .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.finish)) { _, _ ->
                findNavController().navigateUp()
            }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val keyboardPadding = (ime.bottom - navigationBars.bottom)
                .coerceAtLeast(0)

            binding.root.setPadding(
                0,
                0,
                0,
                keyboardPadding
            )

            insets
        }

        binding.albumName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.root.postDelayed({ scrollToView(binding.albumName) }, 200)
            }
        }

        binding.albumDescription.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.root.postDelayed({ scrollToView(binding.albumDescription) }, 200)
            }
        }

        setupScrollOnTouch(binding.albumName)
        setupScrollOnTouch(binding.albumDescription)

        binding.albumName.doOnTextChanged { text, _, _, _ ->
            viewModel.onNameEdited(text.toString())
        }
        binding.albumDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.onDescriptionEdited(text.toString())
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackClick(dialog)
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.onImagePicked(uri)
                }
            }

        binding.album.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackClick(dialog)
        }

        binding.createPlaylistButton.setOnClickListener {
            Log.d("PlaylistDB", "Button is clicked")
            viewModel.createPlaylist()
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist) + " ${binding.albumName.text.trim()} " + getString(R.string.created),
                Toast.LENGTH_LONG
            ).show()
            findNavController().navigateUp()
        }

        viewModel.observeNewPlaylistState().observe(viewLifecycleOwner) {
            if (binding.album.getTag(R.id.tag_uri) != it.imageUri) {
                binding.album.setTag(R.id.tag_uri, it.imageUri)
                Glide.with(this)
                    .load(it.imageUri)
                    .transform(CenterCrop(), RoundedCorners(dpToPx(8f, requireContext())))
                    .into(binding.album)
            }

            binding.albumNameStrokeText.isVisible = it.name.isNotEmpty()

            binding.albumDescriptionStrokeText.isVisible = it.description.isNotEmpty()


            binding.createPlaylistButton.isEnabled = it.isButtonEnabled
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun scrollToView(view: View) {
        binding.root.post {
            val rect = Rect()
            view.getDrawingRect(rect)
            binding.root.offsetDescendantRectToMyCoords(view, rect)

            binding.root.smoothScrollTo(
                0,
                rect.bottom
            )
        }
    }

    open fun onBackClick(dialog: MaterialAlertDialogBuilder) {
        if (binding.album.getTag(R.id.tag_uri) != null || !binding.albumName.text.isNullOrEmpty() || !binding.albumDescription.text.isNullOrEmpty()) {
            dialog.show()
        } else {
            findNavController().navigateUp()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupScrollOnTouch(editText: EditText) {
        editText.setOnTouchListener { _, _ ->
            editText.postDelayed({
                scrollToView(editText)
            }, 200)

            false
        }
    }
}