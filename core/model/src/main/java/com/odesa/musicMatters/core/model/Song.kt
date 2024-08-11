package com.odesa.musicMatters.core.model

import android.net.Uri
import androidx.media3.common.MediaItem

data class Song(
    val id: String,
    val mediaUri: Uri,
    val title: String,
    val displayTitle: String,
    val duration: Long,
    val artists: Set<String>,
    val size: Long,
    val dateModified: Long,
    val path: String,
    val trackNumber: Int?,
    val year: Int?,
    val albumTitle: String?,
    val composer: String?,
    val artworkUri: Uri?,
    val mediaItem: MediaItem
)
