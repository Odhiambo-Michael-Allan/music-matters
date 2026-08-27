package com.squad.musicmatters.core.testing.songs

import androidx.media3.common.MediaItem
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.Song
import java.time.Duration
import java.util.UUID

fun testSong(
    id: String,
    title: String = "",
    artworkUri: String? = null,
    albumId: Long? = null,
    albumTitle: String? = null,
    artistId: Long = 0,
    artistTitle: String = "",
    path: String = "",
    dateModified: Long = 0L,
) = Song(
    id = id,
    mediaStoreId = 0,
    mediaUri = "Uri.EMPTY",
    title = title,
    albumId = albumId ?: 0L,
    duration = 0L,
    artist = artistTitle,
    size = 0L,
    dateModified = dateModified,
    path = path,
    trackNumber = null,
    year = null,
    albumTitle = albumTitle,
    composer = null,
    artworkUri = artworkUri,
    artistId = artistId,
)

fun testLyric(
    content: String,
    timeStamp: Duration = Duration.ZERO
) = Lyric(
    content = content,
    timeStamp = timeStamp
)

val testSongsForSorting = listOf(
    Song(
        id = "id1",
        mediaStoreId = 0,
        mediaUri = "Uri.EMPTY",
        title = "song-1",
        albumTitle = "D",
        artist = "A - Michael Jackson",
        artworkUri = null,
        composer = "A,B",
        dateModified = 354L,
        albumId = 0L,
        duration = 60L,
        trackNumber = 324,
        year = 2022,
        size = 1L,
        path = "/path/to/song/7",
        artistId = 0,
    ),
    Song(
        id = "id2",
        mediaStoreId = 0,
        mediaUri = "Uri.EMPTY",
        title = "song-2",
        albumTitle = "C",
        artist = "B - Michael Jackson",
        artworkUri = null,
        composer = "B,C",
        dateModified = 754L,
        albumId = 0L,
        duration = 4L,
        trackNumber = 235,
        year = 2002,
        size = 2L,
        path = "/path/to/song/8",
        artistId = 0,
    ),
    Song(
        id = "id3",
        mediaStoreId = 0,
        mediaUri = "Uri.EMPTY",
        title = "song-3",
        albumTitle = "B",
        artist = "C - Michael Jackson",
        artworkUri = null,
        composer = "C,D",
        dateModified = 7976L,
        albumId = 0L,
        duration = 7L,
        trackNumber = 443,
        year = 2007,
        size = 3L,
        path = "/path/to/song/6",
        artistId = 0,
    ),
    Song(
        id = "id4",
        mediaStoreId = 0,
        mediaUri = "Uri.EMPTY",
        title = "song-4",
        albumTitle = "A",
        artist = "D - Michael Jackson",
        artworkUri = null,
        composer = "D,E",
        dateModified = 200L,
        albumId = 0L,
        duration = 4L,
        trackNumber = 234,
        year = 2004,
        size = 4L,
        path = "/path/to/song/1",
        artistId = 0,
    ),
    Song(
        id = "id5",
        mediaStoreId = 0,
        mediaUri = "Uri.EMPTY",
        title = "song-5",
        albumTitle = "<unknown>",
        artist = "E - Michael Jackson",
        artworkUri = null,
        composer = null,
        dateModified = 34245L,
        albumId = 0L,
        duration = 89L,
        trackNumber =134,
        year = 1990,
        size = 5L,
        path = "/path/to/song/5",
        artistId = 0,
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