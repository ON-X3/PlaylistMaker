package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    override fun sharePlaylist(
        playlistName: String,
        playlistDescription: String,
        tracks: List<Track>,
        formatter: SharingInteractor.PlaylistInfoFormatter
    ) {
        externalNavigator.sharePlaylist(
            buildString {
                appendLine(playlistName)
                if (playlistDescription.isNotBlank()) {
                    appendLine(playlistDescription)
                }
                appendLine(formatter.formatTrackCount(tracks.size))
                tracks.forEachIndexed { index, track ->
                    append("${index+1}. ")
                    append("${track.artistName} - ")
                    append(track.trackName+" ")
                    appendLine("(${formatter.formatTrackTime(track.trackTime)})")
                }
            }
        )
    }

    private fun getShareAppLink(): String {
        return externalNavigator.getShareAppLink()
    }

    private fun getSupportEmailData(): EmailData {
        return externalNavigator.getSupportEmailData()
    }

    private fun getTermsLink(): String {
        return externalNavigator.getTermsLink()
    }

}