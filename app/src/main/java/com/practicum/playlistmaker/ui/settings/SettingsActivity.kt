package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        val settingsInteractor = Creator.provideSettingsInteractor(applicationContext)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)

        settingsInteractor.loadSettings{settings ->
            themeSwitcher.isChecked = settings.darkTheme
        }

        themeSwitcher.setOnCheckedChangeListener {switcher, checked ->
            settingsInteractor.loadSettings{ settings ->
                settings.useSystemTheme = false
                settings.darkTheme = checked
                settingsInteractor.saveSettings(settings)
            }
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val buttonShare = findViewById<MaterialTextView>(R.id.share_button)
        buttonShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.yp_course))
            startActivity(shareIntent)
        }

        val buttonSupport = findViewById<MaterialTextView>(R.id.support_button)
        buttonSupport.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.developer_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text))
            startActivity(supportIntent)
        }

        val buttonAgreement = findViewById<MaterialTextView>(R.id.agreement_button)
        buttonAgreement.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.agreement_url)))
            startActivity(agreementIntent)
        }
    }
}