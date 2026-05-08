package com.practicum.playlistmaker.search.ui.viewmodel

import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.TrackUi
import java.util.Locale

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : ViewModel() {

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
                        renderState(SearchState.Error(R.string.search_error))
                        showToast.postValue(message)
                    }

                    tracks.isEmpty() -> {
                        renderState(SearchState.Empty(R.string.search_nothing))
                    }

                    else -> {
                        latestFoundTacks.addAll(tracks)
                        renderState(SearchState.Content(toListOfTrackUi(tracks)))
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
                    renderState(SearchState.History(toListOfTrackUi(listOfLoadedTracks)))
                } else stateLiveData.postValue(SearchState.EmptyScreen)
            }
        }
    }

    fun clearHistory() {
        historyInteractor.clearSearchHistory()
        stateLiveData.postValue(SearchState.EmptyScreen)
    }

    fun addToHistory(track: TrackUi) {
        historyInteractor.addToSearchHistory(fromTrackUi(track))
    }

    fun getLatestSearchText(): String {
        return latestSearchText
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    private fun toListOfTrackUi(list: List<Track>): List<TrackUi> {
        return list.map {
            TrackUi(
                it.trackId,
                it.trackName,
                it.artistName,
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTime),
                it.artworkUrl100,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
    }

    private fun fromTrackUi(track: TrackUi): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            stringToMs(track.trackTime),
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }

    private fun stringToMs(string: String): Long {
        val (min, sec) = string.split(":")
        val ms = (min.toLong() * 60 + sec.toLong()) * 1000
        return ms
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getFactory(
            tracksInteractor: TracksInteractor,
            historyInteractor: SearchHistoryInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(tracksInteractor, historyInteractor)
            }
        }
    }

}