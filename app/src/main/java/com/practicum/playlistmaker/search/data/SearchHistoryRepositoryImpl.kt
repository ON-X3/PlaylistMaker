package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.core.db.AppDatabase
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class SearchHistoryRepositoryImpl(
    private val storageClient: StorageClient<List<TrackDto>>,
    private val db: AppDatabase
) :
    SearchHistoryRepository {
    override fun loadSearchHistory(): Flow<List<Track>> =
        combine(getSearchHistory(), getFavoritesId()) { listOfTrack, favoritesId ->
            listOfTrack.forEach { it.isFavorite = it.trackId in favoritesId }
            listOfTrack
        }

    private fun getSearchHistory(): Flow<List<Track>> = flow {
        val listOfTracks = storageClient.getData()
        if (listOfTracks?.isNotEmpty() == true) {
            emit(listOfTracks.map {
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
            })
        } else emit(emptyList())
    }

    private fun getFavoritesId(): Flow<List<Long>> = db.favoritesDao().getFavoritesId()


    override suspend fun addToSearchHistory(track: Track) {
        withContext(Dispatchers.IO) {
            val savedTracks = mutableListOf<TrackDto>()
            savedTracks.addAll(storageClient.getData() ?: emptyList())

            val addedTrackDto = TrackDto(
                track.trackId,
                track.trackName,
                track.artistName,
                track.trackTime,
                track.artworkUrl100,
                track.collectionName,
                track.releaseDate,
                track.primaryGenreName,
                track.country,
                track.previewUrl
            )

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
    }

    override fun clearHistory() {
        storageClient.deleteData()
    }

    companion object {
        private const val HISTORY_SIZE = 10
    }
}