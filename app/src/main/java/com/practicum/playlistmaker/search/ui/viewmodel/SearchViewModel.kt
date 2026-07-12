package com.practicum.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.core.common.mapper.toDomain
import com.practicum.playlistmaker.core.common.mapper.toUi
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.models.TrackUi
import com.practicum.playlistmaker.util.Debouncer
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val showToast = SingleLiveEvent<String?>()
    fun observeShowToast(): LiveData<String?> = showToast

    private var latestSearchText = ""

    private var latestFoundTacks = mutableListOf<Track>()

    private val searchTrackDebouncer = Debouncer<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { text ->
        searchTracks(text)
    }

    private var searchJob: Job? = null
    private var historyJob: Job? = null

    private fun searchTracks(searchText: String) {
        latestFoundTacks.clear()
        searchJob?.cancel()

        if (searchText.isNotEmpty()) {

            renderState(SearchState.Loading)
            searchJob = viewModelScope.launch {
                tracksInteractor.searchTracks(searchText).collect { pair ->
                    processResult(pair.first, pair.second)
                }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, message: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }

        when {
            message != null -> {
                renderState(SearchState.Error(R.string.search_error))
                showToast.value = message
            }

            tracks.isEmpty() -> {
                renderState(SearchState.Empty(R.string.search_nothing))
            }

            else -> {
                latestFoundTacks.addAll(tracks)
                renderState(SearchState.Content(tracks.map { it.toUi() }))
            }
        }
    }

    fun searchDebounce(text: String) {
        if (latestSearchText == text) {
            return
        }

        latestSearchText = text

        searchTrackDebouncer.invoke(text)
    }

    fun searchWithoutDebounce(text: String) {
        searchTrackDebouncer.cancel()
        searchTracks(text)
    }

    fun showHistory(hasFocus: Boolean, text: String) {
        historyJob?.cancel()
        if (text.isEmpty() && hasFocus) {
            historyJob = viewModelScope.launch {
                historyInteractor.loadSearchHistory().collect { listOfLoadedTracks ->
                    if (listOfLoadedTracks.isNotEmpty()) {
                        renderState(SearchState.History(listOfLoadedTracks.map { it.toUi() }))
                    } else stateLiveData.value = SearchState.EmptyScreen
                }
            }
        }
    }

    fun clearHistory() {
        historyInteractor.clearSearchHistory()
        stateLiveData.value = SearchState.EmptyScreen
    }

    fun addToHistory(track: TrackUi) {
        viewModelScope.launch() { historyInteractor.addToSearchHistory(track.toDomain()) }

    }

    fun getLatestSearchText(): String {
        return latestSearchText
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}