package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.viewmodel.SearchState
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private var isClickAllowed = true
    private lateinit var adapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private lateinit var binding: ActivitySearchBinding

    private lateinit var viewModel: SearchViewModel
    private lateinit var searchTextWatcher: TextWatcher

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TrackAdapter { track ->
            onTrackClick(track)
        }
        searchHistoryAdapter = TrackAdapter { track ->
            onTrackClick(track)
        }

        binding.searchHistoryList.adapter = searchHistoryAdapter

        binding.tracksList.adapter = adapter

        binding.updateButton.setOnClickListener {
            viewModel.searchTracks(binding.searchEditText.text.toString())
        }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        viewModel =
            ViewModelProvider(this, SearchViewModel.getFactory()).get(SearchViewModel::class.java)
        binding.searchEditText.setText(viewModel.getLatestSearchText())

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeShowToast().observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        val clearButton = findViewById<ImageView>(R.id.clearButton)
        clearButton.setOnClickListener {
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
                clearButton.isVisible = !s.isNullOrEmpty()
                if (!s.isNullOrEmpty()) hideSearchHistory()
                viewModel.showHistory(binding.searchEditText.hasFocus(), s?.toString() ?: "")
                viewModel.searchDebounce(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.searchEditText.apply {
            addTextChangedListener(searchTextWatcher)
            setOnFocusChangeListener { v, hasFocus ->
                viewModel.showHistory(hasFocus, this.text.toString())
            }
            requestFocus()
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.searchTracks(this.text.toString())
                }
                false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.searchEditText.removeTextChangedListener(searchTextWatcher)
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoadingState()
            is SearchState.Error -> showErrorState(state.errorMessage)
            is SearchState.Empty -> showEmptyState(state.message)
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

    private fun showErrorState(placeholderText: String) {
        hideProgressBar()
        hideListOfTracks()
        hideSearchHistory()
        showPlaceholder(placeholderText)
    }

    private fun showEmptyState(placeholderText: String) {
        hideProgressBar()
        hideListOfTracks()
        hideSearchHistory()
        showPlaceholder(placeholderText)
    }

    private fun showContentState(foundTracks: List<Track>) {
        hideProgressBar()
        hidePlaceholder()
        hideSearchHistory()
        showListOfTracks(foundTracks)
    }

    private fun showHistoryState(tracks: List<Track>) {
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

    private fun showListOfTracks(foundTracks: List<Track>) {
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

    private fun showSearchHistory(listOfTracks: List<Track>) {
        searchHistoryAdapter.tracks.clear()
        searchHistoryAdapter.tracks.addAll(listOfTracks)
        searchHistoryAdapter.notifyDataSetChanged()
        binding.searchHistoryLayout.visibility = View.VISIBLE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun onTrackClick(track: Track) {
        if (clickDebounce()) {

            viewModel.addToHistory(track)

            val playerIntent = Intent(this, PlayerActivity::class.java)
            playerIntent.putExtra("track", Gson().toJson(track))
            startActivity(playerIntent)
        }
    }

    companion object {

        const val CLICK_DEBOUNCE_DELAY = 1000L

    }
}