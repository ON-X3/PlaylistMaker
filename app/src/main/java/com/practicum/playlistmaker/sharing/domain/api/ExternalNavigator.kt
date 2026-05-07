package com.practicum.playlistmaker.sharing.domain.api

import com.practicum.playlistmaker.sharing.domain.EmailData

interface ExternalNavigator {
    fun shareLink(appLink: String)
    fun openLink(termsLink: String)
    fun openEmail(supportEmailData: EmailData)

    fun getShareAppLink(): String
    fun getSupportEmailData(): EmailData
    fun getTermsLink(): String
}