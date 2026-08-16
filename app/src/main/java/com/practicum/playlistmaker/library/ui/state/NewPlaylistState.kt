package com.practicum.playlistmaker.library.ui.state

import android.net.Uri

data class NewPlaylistState(
    val imageUri: Uri? = null,
    val name: String = "",
    val description: String = "",
    val isButtonEnabled: Boolean = false
) {
}