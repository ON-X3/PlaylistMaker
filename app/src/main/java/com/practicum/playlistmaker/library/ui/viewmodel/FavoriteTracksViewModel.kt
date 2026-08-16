package com.practicum.playlistmaker.library.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.core.common.mapper.toUi
import com.practicum.playlistmaker.library.domain.api.FavoritesInteractor
import com.practicum.playlistmaker.library.ui.state.FavoritesState
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(favoritesInteractor: FavoritesInteractor): ViewModel() {

    init {
        viewModelScope.launch {
            favoritesInteractor.getFavorites().collect { favorites ->
                if (favorites.isNotEmpty()) {
                    stateLiveData.value = FavoritesState.Content(favorites.map { it.toUi() })
                } else {
                    stateLiveData.value = FavoritesState.Empty
                }
            }
        }
    }

    private val stateLiveData = MutableLiveData<FavoritesState>()
    fun observeState(): LiveData<FavoritesState> = stateLiveData


}