package com.practicum.playlistmaker.search.ui.viewmodel

import com.practicum.playlistmaker.search.ui.models.TrackUi

sealed interface SearchState {

    object Loading : SearchState

    data class Content(val tracks: List<TrackUi>) : SearchState

    data class Error(val errorMessageId: Int) : SearchState

    data class Empty(val messageId: Int) : SearchState

    data class History(val tracks: List<TrackUi>) : SearchState

    object EmptyScreen : SearchState
}