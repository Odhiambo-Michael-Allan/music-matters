package com.squad.musicmatters.core.model


data class Song(
    val id: String,
    val mediaStoreId: Long,
    val mediaUri: String,
    val title: String,
    val duration: Long,
    val artist: String,
    val size: Long,
    val dateModified: Long,
    val path: String,
    val trackNumber: Int?,
    val year: Int?,
    val albumTitle: String?,
    val albumId: Long,
    val artistId: Long,
    val composer: String?,
    val artworkUri: String?,
    val albumArtist: String? = null,
) {
    companion object {

        @JvmStatic
        val EMPTY = Song(
            id = "-1",
            mediaStoreId = -1,
            title = "",
            duration = -1,
            dateModified = -1,
            size = 0L,
            path = "",
            trackNumber = null,
            year = null,
            albumTitle = null,
            artworkUri = null,
            mediaUri = "",
            artist = "",
            composer = "",
            albumId = 0,
            artistId = 0,
        )
    }
}


