package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.EmailData
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator

class ExternalNavigatorImpl(val context: Context) : ExternalNavigator {
    override fun shareLink(appLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, appLink)
        context.startActivity(shareIntent)
    }

    override fun openLink(termsLink: String) {
        val termsIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(termsLink))
        termsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(termsIntent)
    }

    override fun openEmail(supportEmailData: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, supportEmailData.text)
        context.startActivity(supportIntent)
    }

    override fun getShareAppLink(): String {
        return context.getString(R.string.yp_course)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            context.getString(R.string.developer_email),
            context.getString(R.string.email_subject),
            context.getString(R.string.email_text)
        )
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.agreement_url)
    }

}