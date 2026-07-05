package com.practicum.playlistmaker.search.ui.fragment

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.ui.models.TrackUi
import com.practicum.playlistmaker.search.ui.viewmodel.SearchState
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private var isClickAllowed = true
    private lateinit var adapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var searchTextWatcher: TextWatcher

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter { track ->
            onTrackClick(track)
        }
        searchHistoryAdapter = TrackAdapter { track ->
            onTrackClick(track)
        }

        binding.searchHistoryList.adapter = searchHistoryAdapter

        binding.tracksList.adapter = adapter

        binding.updateButton.setOnClickListener {
            viewModel.searchWithoutDebounce(binding.searchEditText.text.toString())
        }

        binding.searchEditText.setText(viewModel.getLatestSearchText())

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        binding.clearButton.setOnClickListener {
            binding.searchEditText.setText("")
            inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
            viewModel.showHistory(binding.searchEditText.hasFocus(), "")
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }


        searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearButton.isVisible = !s.isNullOrEmpty()
                if (!s.isNullOrEmpty()) hideSearchHistory()
                viewModel.showHistory(binding.searchEditText.hasFocus(), s?.toString() ?: "")
                viewModel.searchDebounce(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.searchEditText.apply {
            addTextChangedListener(searchTextWatcher)
            setOnFocusChangeListener { _, hasFocus ->
                viewModel.showHistory(hasFocus, this.text.toString())
            }
            requestFocus()
            inputMethodManager?.showSoftInput(
                binding.searchEditText,
                InputMethodManager.SHOW_IMPLICIT
            )
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.searchWithoutDebounce(this.text.toString())
                }
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.searchEditText.removeTextChangedListener(searchTextWatcher)
        _binding = null
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoadingState()
            is SearchState.Error -> showErrorState(state.errorMessageId)
            is SearchState.Empty -> showEmptyState(state.messageId)
            is SearchState.Content -> showContentState(state.tracks)
            is SearchState.History -> showHistoryState(state.tracks)
            is SearchState.EmptyScreen -> showEmptyScreenState()
        }
    }

    private fun showLoadingState() {
        hidePlaceholder()
        hideListOfTracks()
        hideSearchHistory()
        showProgressBar()
    }

    private fun showErrorState(placeholderTextId: Int) {
        hideProgressBar()
        hideListOfTracks()
        hideSearchHistory()
        showPlaceholder(getString(placeholderTextId))
    }

    private fun showEmptyState(placeholderTextId: Int) {
        hideProgressBar()
        hideListOfTracks()
        hideSearchHistory()
        showPlaceholder(getString(placeholderTextId))
    }

    private fun showContentState(foundTracks: List<TrackUi>) {
        hideProgressBar()
        hidePlaceholder()
        hideSearchHistory()
        showListOfTracks(foundTracks)
    }

    private fun showHistoryState(tracks: List<TrackUi>) {
        hideProgressBar()
        hidePlaceholder()
        hideListOfTracks()
        showSearchHistory(tracks)
    }

    private fun showEmptyScreenState() {
        hideProgressBar()
        hidePlaceholder()
        hideListOfTracks()
        hideSearchHistory()
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideListOfTracks() {
        binding.tracksList.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showListOfTracks(foundTracks: List<TrackUi>) {
        adapter.tracks.clear()
        adapter.tracks.addAll(foundTracks)
        adapter.notifyDataSetChanged()
        binding.tracksList.visibility = View.VISIBLE
    }

    private fun hidePlaceholder() {
        binding.searchPlaceholder.visibility = View.GONE
    }

    private fun showPlaceholder(text: String) {
        binding.searchPlaceholderText.text = text
        if (text === getString(R.string.search_nothing)) {
            binding.searchPlaceholderImage.setImageResource(R.drawable.search_nothing)
            binding.updateButton.visibility = View.GONE
        }
        if (text === getString(R.string.search_error)) {
            binding.searchPlaceholderImage.setImageResource(R.drawable.search_error)
            binding.updateButton.visibility = View.VISIBLE
        }
        binding.searchPlaceholder.visibility = View.VISIBLE
    }

    private fun hideSearchHistory() {
        binding.searchHistoryLayout.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showSearchHistory(listOfTracks: List<TrackUi>) {
        searchHistoryAdapter.tracks.clear()
        searchHistoryAdapter.tracks.addAll(listOfTracks)
        searchHistoryAdapter.notifyDataSetChanged()
        binding.searchHistoryLayout.visibility = View.VISIBLE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            /*lifecycleScope вместо viewLifecycleOwner.lifecycleScope, рекомендуемого в курсе,
            использован осознанно, т.к. корутина обращается только к свойству самого фрагмента и
            не взаимодействует с представлением фрагмента, а использование рекомендованного
            viewLifecycleOwner.lifecycleScope приводит к тому, что корутина отменяется при
            навигации на фрагмент плеера, и свойство isClickAllowed остается false, что приводит
            к невозможности повторного нажатия на какой-либо трек после возвращения от плеера
            обратно к списку треков*/
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun onTrackClick(track: TrackUi) {
        if (clickDebounce()) {
            viewModel.addToHistory(track)
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }
    }

    companion object {

        const val CLICK_DEBOUNCE_DELAY = 1000L

    }
}