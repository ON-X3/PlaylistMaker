package com.practicum.playlistmaker.search.ui.viewmodel

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface SearchState {

    object Loading: SearchState

    data class Content(val tracks: List<Track>): SearchState

    data class Error(val errorMessage: String): SearchState

    data class Empty(val message: String): SearchState

    data class History(val tracks: List<Track>): SearchState

    object EmptyScreen: SearchState
}