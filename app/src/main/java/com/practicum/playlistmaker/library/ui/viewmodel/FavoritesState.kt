package com.practicum.playlistmaker.library.ui.viewmodel

import com.practicum.playlistmaker.search.ui.models.TrackUi

sealed interface FavoritesState {

    object Empty: FavoritesState

    data class Content (val favorites: List<TrackUi>): FavoritesState
}