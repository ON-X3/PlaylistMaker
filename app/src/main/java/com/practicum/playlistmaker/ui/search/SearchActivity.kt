package com.practicum.playlistmaker.ui.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.models.Track

class SearchActivity : AppCompatActivity() {

    private var searchString: String = DEF_STRING

    private val tracks: MutableList<Track> = mutableListOf()
    private val searchHistoryList: MutableList<Track> = mutableListOf()
    private lateinit var adapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter

    private lateinit var searchPlaceholder: LinearLayout
    private lateinit var searchPlaceholderImage: ImageView
    private lateinit var searchPlaceholderText: TextView
    private lateinit var updateButton: MaterialButton
    private lateinit var searchEditText: EditText
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var clearHistoryButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var trackRecycler: RecyclerView
    private lateinit var searchHistoryRecycler: RecyclerView

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTracks() }
    private val tracksSearcher = Creator.provideTracksInteractor()
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        SEARCH_NOTHING = getString(R.string.search_nothing)
        SEARCH_ERROR = getString(R.string.search_error)

        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)

        adapter = TrackAdapter(tracks, searchHistoryInteractor)
        searchHistoryAdapter = TrackAdapter(searchHistoryList, searchHistoryInteractor)
        searchHistoryRecycler = findViewById<RecyclerView>(R.id.search_history_list)
        searchHistoryRecycler.adapter = searchHistoryAdapter

        searchEditText = findViewById(R.id.searchEditText)
        searchPlaceholder = findViewById(R.id.search_placeholder)
        searchPlaceholderImage = findViewById(R.id.search_placeholder_image)
        searchPlaceholderText = findViewById(R.id.search_placeholder_text)
        searchHistoryLayout = findViewById(R.id.search_history_layout)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        updateButton = findViewById(R.id.update_button)
        progressBar = findViewById(R.id.progress_bar)

        trackRecycler = findViewById(R.id.tracksList)
        trackRecycler.adapter = adapter

        searchHistoryRecycler = findViewById<RecyclerView>(R.id.search_history_list)
        searchHistoryRecycler.adapter = searchHistoryAdapter

        updateButton.setOnClickListener {
            searchTracks()
        }

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        val clearButton = findViewById<ImageView>(R.id.clear_button)
        clearButton.setOnClickListener {
            searchEditText.setText("")
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            tracks.clear()
            adapter.notifyDataSetChanged()
            hidePlaceholder()
        }

        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearSearchHistory()
            hideSearchHistory()
        }


        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchString = s.toString()
                searchDebounce()
                if (s?.isEmpty() == true) {
                    showSearchHistory(searchEditText.hasFocus())
                    hidePlaceholder()
                    hideListOfTracks()
                } else hideSearchHistory()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        searchEditText.addTextChangedListener(searchTextWatcher)
        searchEditText.setOnFocusChangeListener { v, hasFocus ->
            showSearchHistory(hasFocus)
        }
        searchEditText.requestFocus()
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnable)
                searchTracks()
            }
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_STRING, searchString)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchString = savedInstanceState.getString(SEARCH_STRING, DEF_STRING)
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        searchEditText.setText(searchString)
        searchEditText.setSelection(searchEditText.text.length)
    }

    private fun searchTracks() {
        val expression = searchEditText.text.toString()
        if (expression.isNotEmpty()) {

            hidePlaceholder()
            hideListOfTracks()
            showProgressBar()

            tracksSearcher.searchTracks(expression) { resultOfRequest ->
                val (isSuccess, listOfTracks) = resultOfRequest

                runOnUiThread {
                    hideProgressBar()
                    if (isSuccess) {
                        tracks.clear()
                        if (listOfTracks.isNotEmpty()) {
                            tracks.addAll(listOfTracks)
                            adapter.notifyDataSetChanged()
                            showListOfTracks()
                        } else {
                            showPlaceholder(SEARCH_NOTHING)
                        }
                    } else {
                        showPlaceholder(SEARCH_ERROR)
                    }
                }
            }
        }
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideListOfTracks() {
        trackRecycler.visibility = View.GONE
    }

    private fun showListOfTracks() {
        trackRecycler.visibility = View.VISIBLE
    }

    private fun hidePlaceholder() {
        searchPlaceholder.visibility = View.GONE
    }

    private fun showPlaceholder(text: String) {
        searchPlaceholderText.text = text
        if (text === SEARCH_NOTHING) {
            searchPlaceholderImage.setImageResource(R.drawable.search_nothing)
            updateButton.visibility = View.GONE
        }
        if (text === SEARCH_ERROR) {
            searchPlaceholderImage.setImageResource(R.drawable.search_error)
            updateButton.visibility = View.VISIBLE
        }
        searchPlaceholder.visibility = View.VISIBLE
    }

    private fun hideSearchHistory() {
        searchHistoryLayout.visibility = View.GONE
    }

    private fun showSearchHistory(hasFocus: Boolean) {
        searchHistoryInteractor.loadSearchHistory { listOfLoadedTracks ->
            searchHistoryList.clear()
            searchHistoryList.addAll(listOfLoadedTracks)
            runOnUiThread {
                searchHistoryAdapter.notifyDataSetChanged()
                clearHistoryButton.visibility =
                    if (searchHistoryList.isNotEmpty()) View.VISIBLE else View.GONE
                searchHistoryLayout.visibility =
                    if (hasFocus && searchEditText.text.isEmpty() && searchHistoryList.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        const val SEARCH_STRING = "SEARCH_STRING"
        const val DEF_STRING = ""

        lateinit var SEARCH_NOTHING: String
        lateinit var SEARCH_ERROR: String

        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}