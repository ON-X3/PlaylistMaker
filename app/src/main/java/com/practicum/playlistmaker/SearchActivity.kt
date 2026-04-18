package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class SearchActivity : AppCompatActivity() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val tunesApiService = retrofit.create<TunesApiService>()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchHistoryPrefs = getSharedPreferences(SEARCH_HISTORY_PREFERENCES, MODE_PRIVATE)
        val searchHistory = SearchHistory(searchHistoryPrefs)
        adapter = TrackAdapter(tracks, searchHistory)
        searchHistoryAdapter = TrackAdapter(searchHistoryList, searchHistory)

        searchEditText = findViewById(R.id.searchEditText)
        searchPlaceholder = findViewById(R.id.search_placeholder)
        searchPlaceholderImage = findViewById(R.id.search_placeholder_image)
        searchPlaceholderText = findViewById(R.id.search_placeholder_text)
        searchHistoryLayout = findViewById(R.id.search_history_layout)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        updateButton = findViewById(R.id.update_button)

        updateButton.setOnClickListener {
            sendQuery()
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
            showPlaceholder("")
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            showSearchHistory(searchHistory, searchEditText.hasFocus())
        }



        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchString = s.toString()
                if (s?.isEmpty() == true) {
                    showSearchHistory(searchHistory, searchEditText.hasFocus())
                    showPlaceholder("")
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                } else searchHistoryLayout.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchEditText.addTextChangedListener(searchTextWatcher)
        searchEditText.setOnFocusChangeListener { v, hasFocus ->
            showSearchHistory(searchHistory, hasFocus)
        }
        searchEditText.requestFocus()

        val trackRecycler = findViewById<RecyclerView>(R.id.tracksList)
        trackRecycler.adapter = adapter

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendQuery()
                true
            }
            false
        }

        val searchHistoryRecycler = findViewById<RecyclerView>(R.id.search_history_list)
        searchHistoryRecycler.adapter = searchHistoryAdapter

    }

    private var searchString: String = DEF_STRING

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

    private fun showPlaceholder(text: String) {
        if (text.isNotEmpty()) {
            tracks.clear()
            adapter.notifyDataSetChanged()
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
        } else {
            searchPlaceholder.visibility = View.GONE
        }
    }

    private fun sendQuery() {
        if (searchEditText.text.isNotEmpty()) {
            tunesApiService.search(searchEditText.text.toString())
                .enqueue(object : Callback<TracksResponse> {
                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        if (response.isSuccessful) {
                            tracks.clear()
                            val results : List<Track>? = response.body()?.results
                            if (results?.isNotEmpty() == true) {
                                tracks.addAll(results)
                                adapter.notifyDataSetChanged()
                            }
                            if (tracks.isEmpty()) {
                                showPlaceholder(SEARCH_NOTHING)
                            } else {
                                showPlaceholder("")
                            }
                        } else {
                            showPlaceholder(SEARCH_ERROR)
                        }
                    }

                    override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                        showPlaceholder(SEARCH_ERROR)
                    }
                })
        }
    }

    private fun showSearchHistory(searchHistory: SearchHistory, hasFocus: Boolean) {
        searchHistoryList.clear()
        if (searchHistory.getSearchHistory() != null) {
            searchHistoryList.addAll(searchHistory.getSearchHistory()!!)
        }
        searchHistoryAdapter.notifyDataSetChanged()
        clearHistoryButton.visibility = if (searchHistoryList.isNotEmpty()) View.VISIBLE else View.GONE
        searchHistoryLayout.visibility = if (hasFocus && searchEditText.text.isEmpty() && searchHistoryList.isNotEmpty()) View.VISIBLE else View.GONE
    }

    companion object {
        const val SEARCH_HISTORY_PREFERENCES = "search_history_preferences"
        const val SEARCH_STRING = "SEARCH_STRING"
        const val DEF_STRING = ""
        const val SEARCH_NOTHING = "Ничего не нашлось"
        const val SEARCH_ERROR = "Проблемы со связью\n\nЗагрузка не удалась. Проверьте подключение к интернету"
    }
}