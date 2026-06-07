package com.squad.musicmatters.core.model


data class Song(
    val id: String,
    val mediaUri: String,
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
    val artworkUri: String?,
) {
    companion object {

        @JvmStatic
        val EMPTY = Song(
            id = "-1",
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
            displayTitle = "",
            artists = emptySet(),
            composer = ""
        )
    }
}


