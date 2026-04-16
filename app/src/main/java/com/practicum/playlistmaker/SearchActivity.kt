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

    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter(tracks)

    private lateinit var searchPlaceholder: LinearLayout
    private lateinit var searchPlaceholderImage: ImageView
    private lateinit var searchPlaceholderText: TextView
    private lateinit var updateButton: MaterialButton
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEditText = findViewById(R.id.searchEditText)
        searchPlaceholder = findViewById(R.id.search_placeholder)
        searchPlaceholderImage = findViewById(R.id.search_placeholder_image)
        searchPlaceholderText = findViewById(R.id.search_placeholder_text)
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
        }



        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchString = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        searchEditText.addTextChangedListener(searchTextWatcher)
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
            if (text == getString(R.string.search_nothing)) {
                searchPlaceholderImage.setImageResource(R.drawable.search_nothing)
                updateButton.visibility = View.GONE
            }
            if (text == getString(R.string.search_error)) {
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
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                            }
                            if (tracks.isEmpty()) {
                                showPlaceholder(getString(R.string.search_nothing))
                            } else {
                                showPlaceholder("")
                            }
                        } else {
                            showPlaceholder(getString(R.string.search_error))
                        }
                    }

                    override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                        showPlaceholder(getString(R.string.search_error))
                    }
                })
        }
    }

    companion object {
        const val SEARCH_STRING = "SEARCH_STRING"
        const val DEF_STRING = ""
    }
}