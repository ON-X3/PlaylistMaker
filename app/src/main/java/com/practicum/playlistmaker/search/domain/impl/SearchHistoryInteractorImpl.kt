package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {


    override fun loadSearchHistory(): Flow<List<Track>> = repository.loadSearchHistory()


    override suspend fun addToSearchHistory(track: Track) {
        repository.addToSearchHistory(track)
    }

    override fun clearSearchHistory() {
        val executor = Executors.newCachedThreadPool()
        executor.execute { repository.clearHistory() }
    }


}