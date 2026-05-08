package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class SearchHistoryRepositoryImpl(private val storageClient: StorageClient<List<TrackDto>>): SearchHistoryRepository {
    override fun loadSearchHistory(): List<Track> {
        val listOfTracks = storageClient.getData()
        return if (listOfTracks?.isNotEmpty() == true) {
            listOfTracks.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }
        } else emptyList()
    }

    override fun addToSearchHistory(track: Track) {
        val savedTracks = mutableListOf<TrackDto>()
        savedTracks.addAll(storageClient.getData() ?: emptyList())

        val addedTrackDto = TrackDto (track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl)

        if (savedTracks.isEmpty()) {
            savedTracks.add(addedTrackDto)
        } else {
            val iterator = savedTracks.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (addedTrackDto.trackId == item.trackId) {
                    iterator.remove()
                }
            }
            savedTracks.add(0, addedTrackDto)
            if (savedTracks.size > HISTORY_SIZE) {
                savedTracks.removeAt(HISTORY_SIZE)
            }
        }
        storageClient.saveData(savedTracks)
    }

    override fun clearHistory() {
        storageClient.deleteData()
    }

    companion object {
        private const val HISTORY_SIZE = 10
    }
}