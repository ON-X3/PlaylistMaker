package com.practicum.playlistmaker.search.ui.viewmodel

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.models.Track

class SearchViewModel(private val context: Context) : ViewModel() {

    private val tracksInteractor = Creator.provideTracksInteractor(context)
    private val historyInteractor = Creator.provideSearchHistoryInteractor(context)

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val showToast = SingleLiveEvent<String?>()
    fun observeShowToast(): LiveData<String?> = showToast

    private var latestSearchText = ""

    private val handler = Handler(Looper.getMainLooper())
    private var latestFoundTacks = mutableListOf<Track>()

    fun searchTracks(searchText: String) {
        latestFoundTacks.clear()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        if (searchText.isNotEmpty()) {

            renderState(SearchState.Loading)

            tracksInteractor.searchTracks(searchText) { foundTracks, message ->
                val tracks = mutableListOf<Track>()
                if (foundTracks != null) {
                    tracks.addAll(foundTracks)
                }

                when {
                    message != null -> {
                        renderState(SearchState.Error(context.getString(R.string.search_error)))
                        showToast.postValue(message)
                    }

                    tracks.isEmpty() -> {
                        renderState(SearchState.Empty(context.getString(R.string.search_nothing)))
                    }

                    else -> {
                        latestFoundTacks.addAll(tracks)
                        renderState(SearchState.Content(tracks))
                    }
                }
            }
        }
    }

    fun searchDebounce(text: String) {
        if (latestSearchText == text) {
            return
        }

        latestSearchText = text

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchTracks(text) }

        handler.postDelayed(searchRunnable, SEARCH_REQUEST_TOKEN, SEARCH_DEBOUNCE_DELAY)
    }

    fun showHistory(hasFocus: Boolean, text: String) {
        if (text.isEmpty() && hasFocus) {
            historyInteractor.loadSearchHistory { listOfLoadedTracks ->
                if (listOfLoadedTracks.isNotEmpty()) {
                    renderState(SearchState.History(listOfLoadedTracks))
                } else stateLiveData.postValue(SearchState.EmptyScreen)
            }
        }
    }

    fun clearHistory() {
        historyInteractor.clearSearchHistory()
        stateLiveData.postValue(SearchState.EmptyScreen)
    }

    fun addToHistory(track: Track) {
        historyInteractor.addToSearchHistory(track)
    }

    fun getLatestSearchText(): String {
        return latestSearchText
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SearchViewModel(app)
            }
        }
    }

}