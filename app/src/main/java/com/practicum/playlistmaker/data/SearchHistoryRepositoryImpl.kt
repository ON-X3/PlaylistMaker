package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class SearchHistoryRepositoryImpl(private val storageApi: StorageApi): SearchHistoryRepository {
    override fun loadSearchHistory(): List<Track> {
        val listOfTracks = (storageApi.getData(StorageApi.SEARCH_HISTORY)) as List<TrackDto>
        return if (listOfTracks.isNotEmpty()) {
            listOfTracks.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis),
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
        savedTracks.addAll(storageApi.getData(StorageApi.SEARCH_HISTORY) as List<TrackDto>)

        val addedTrackDto = TrackDto (track.trackId,
            track.trackName,
            track.artistName,
            stringToMs(track.trackTime),
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
        storageApi.saveData(StorageApi.SEARCH_HISTORY, savedTracks)
    }

    override fun clearHistory() {
        storageApi.deleteData(StorageApi.SEARCH_HISTORY)
    }

    private fun stringToMs(string: String): Long {
        val (min, sec) = string.split(":")
        val ms = (min.toLong()*60 + sec.toLong())*1000
        return ms
    }

    companion object {
        private const val HISTORY_SIZE = 10
    }
}