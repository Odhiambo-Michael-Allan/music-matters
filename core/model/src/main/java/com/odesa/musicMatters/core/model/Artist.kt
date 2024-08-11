package com.odesa.musicMatters.core.model

import android.net.Uri

data class Artist(
    val name: String,
    val artworkUri: Uri?,
    val albumCount: Int = 0,
    val trackCount: Int = 0,
)
