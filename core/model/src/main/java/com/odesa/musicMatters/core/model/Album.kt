package com.odesa.musicMatters.core.model

import android.net.Uri

data class Album(
    val title: String,
    val artists: Set<String>,
    val trackCount: Int,
    val artworkUri: Uri?
)
