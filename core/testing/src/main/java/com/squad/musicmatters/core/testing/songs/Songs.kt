package com.squad.musicmatters.core.testing.songs

import androidx.media3.common.MediaItem
import com.squad.musicmatters.core.model.Song
import java.util.UUID

fun testSong(
    id: String,
    artworkUri: String? = null,
) = Song(
    id = id,
    mediaUri = "Uri.EMPTY",
    title = "",
    displayTitle = "",
    duration = 0L,
    artists = emptySet(),
    size = 0L,
    dateModified = 0L,
    path = "",
    trackNumber = null,
    year = null,
    albumTitle = null,
    composer = null,
    artworkUri = artworkUri,
)

val testSongsForSorting = listOf(
    Song(
        id = "id1",
        mediaUri = "Uri.EMPTY",
        title = "song-1",
        albumTitle = "D",
        artists = setOf( "A", "Michael Jackson" ),
        artworkUri = null,
        composer = "A,B",
        dateModified = 354L,
        displayTitle = "song-1",
        duration = 60L,
        trackNumber = 324,
        year = 2022,
        size = 1L,
        path = "/path/to/song/7"
    ),
    Song(
        id = "id2",
        mediaUri = "Uri.EMPTY",
        title = "song-2",
        albumTitle = "C",
        artists = setOf( "B", "Michael Jackson" ),
        artworkUri = null,
        composer = "B,C",
        dateModified = 754L,
        displayTitle = "song-2",
        duration = 4L,
        trackNumber = 235,
        year = 2002,
        size = 2L,
        path = "/path/to/song/8"
    ),
    Song(
        id = "id3",
        mediaUri = "Uri.EMPTY",
        title = "song-3",
        albumTitle = "B",
        artists = setOf( "C", "Michael Jackson" ),
        artworkUri = null,
        composer = "C,D",
        dateModified = 7976L,
        displayTitle = "song-3",
        duration = 7L,
        trackNumber = 443,
        year = 2007,
        size = 3L,
        path = "/path/to/song/6"
    ),
    Song(
        id = "id4",
        mediaUri = "Uri.EMPTY",
        title = "song-4",
        albumTitle = "A",
        artists = setOf( "D", "Michael Jackson" ),
        artworkUri = null,
        composer = "D,E",
        dateModified = 200L,
        displayTitle = "song-4",
        duration = 4L,
        trackNumber = 234,
        year = 2004,
        size = 4L,
        path = "/path/to/song/1"
    ),
    Song(
        id = "id5",
        mediaUri = "Uri.EMPTY",
        title = "song-5",
        albumTitle = "<unknown>",
        artists = setOf( "E", "Michael Jackson" ),
        artworkUri = null,
        composer = null,
        dateModified = 34245L,
        displayTitle = "song-3",
        duration = 89L,
        trackNumber =134,
        year = 1990,
        size = 5L,
        path = "/path/to/song/5"
    ),
)

val id1 = UUID.randomUUID().toString()
val id2 = UUID.randomUUID().toString()
val id3 = UUID.randomUUID().toString()

val testSongMediaItemsForId: List<MediaItem> = listOf(
    MediaItem.Builder().setMediaId( id1 ).build(),
    MediaItem.Builder().setMediaId( id2 ).build(),
    MediaItem.Builder().setMediaId( id3 ).build()
)


//val testSongs = testSongMediaItems.map { it.toSong( artistTagSeparators ) }