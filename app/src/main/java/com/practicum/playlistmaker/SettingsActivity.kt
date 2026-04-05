package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonBack = findViewById<ImageView>(R.id.back_from_settings)
        buttonBack.setOnClickListener {
            finish()
        }

        val buttonShare = findViewById<FrameLayout>(R.id.share_button)
        buttonShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.yp_course))
            startActivity(shareIntent)
        }

        val buttonSupport = findViewById<FrameLayout>(R.id.support_button)
        buttonSupport.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.developer_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
            startActivity(supportIntent)
        }

        val buttonAgreement = findViewById<FrameLayout>(R.id.agreement_button)
        buttonAgreement.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.agreement_url)))
            startActivity(agreementIntent)
        }
    }
}