package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository): SearchHistoryInteractor {


    override fun loadSearchHistory(consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
        val executor = Executors.newCachedThreadPool()
        executor.execute { consumer.consume(repository.loadSearchHistory()) }
    }

    override fun addToSearchHistory(track: Track) {
        val executor = Executors.newCachedThreadPool()
        executor.execute { repository.addToSearchHistory(track) }
    }

    override fun clearSearchHistory() {
        val executor = Executors.newCachedThreadPool()
        executor.execute { repository.clearHistory() }
    }


}