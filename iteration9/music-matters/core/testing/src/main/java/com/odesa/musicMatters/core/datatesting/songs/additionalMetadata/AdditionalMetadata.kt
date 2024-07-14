package com.odesa.musicMatters.core.datatesting.songs.additionalMetadata

import com.odesa.musicMatters.core.data.database.model.SongAdditionalMetadata
import com.odesa.musicMatters.core.datatesting.songs.testSongs

val songsAdditionalMetadataList = listOf(
    SongAdditionalMetadata(
        id = testSongs.first().id,
        codec = "mp3",
        bitrate = 0L,
        bitsPerSample = 0L,
        samplingRate = 0L,
        genre = "Pop"
    ),
    SongAdditionalMetadata(
        id = testSongs.last().id,
        codec = "mp3",
        bitrate = 0L,
        bitsPerSample = 0L,
        samplingRate = 0L,
        genre = "RnB"
    )
)