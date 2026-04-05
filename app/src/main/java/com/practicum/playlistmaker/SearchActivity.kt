package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val buttonBack = findViewById<ImageView>(R.id.back_from_search)
        buttonBack.setOnClickListener {
            finish()
        }

        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val clearButton = findViewById<ImageView>(R.id.clear_button)
        clearButton.setOnClickListener {
            searchEditText.setText("")
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchString = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        searchEditText.addTextChangedListener(searchTextWatcher)
        searchEditText.requestFocus()

    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
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

    companion object {
        const val SEARCH_STRING = "SEARCH_STRING"
        const val DEF_STRING = ""
    }
}