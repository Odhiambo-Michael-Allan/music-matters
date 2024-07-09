package com.odesa.musicMatters.core.datatesting.songs

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.odesa.musicMatters.core.common.media.extensions.ALBUM_TITLE_KEY
import com.odesa.musicMatters.core.common.media.extensions.ARTIST_KEY
import com.odesa.musicMatters.core.common.media.extensions.DATE_KEY
import com.odesa.musicMatters.core.common.media.extensions.PATH_KEY
import com.odesa.musicMatters.core.common.media.extensions.artistTagSeparators
import com.odesa.musicMatters.core.common.media.extensions.toSong
import com.odesa.musicMatters.core.model.Song
import java.util.Calendar
import java.util.UUID

val calendar: Calendar = Calendar.getInstance()

val testSongMediaItems = listOf(
    MediaItem.Builder().apply {
        setMediaId( "ich_hasse_dich" )
        setMediaMetadata( MediaMetadata.Builder().apply {
            setTitle( "Ich hasse dich" )
            setGenre( "Folk" )
            setExtras(
                Bundle().apply {
                    putLong( DATE_KEY, calendar.timeInMillis )
                    putString( ARTIST_KEY, "Jemand" )
                    putString( ALBUM_TITLE_KEY, "Speechless" )
                    putString( PATH_KEY, "path/to/Speechless/Ich hasse dich" )
                }
            )
        }.build() )
    }.build(),
    MediaItem.Builder().apply {
        setMediaId( "about_a_guy" )
        setMediaMetadata( MediaMetadata.Builder().apply {
            setTitle( "About a guy" )
            setGenre( "Folk" )
            setExtras(
                Bundle().apply {
                    calendar.add( Calendar.DAY_OF_WEEK, 1 )
                    putLong( DATE_KEY, calendar.timeInMillis )
                    putString( ARTIST_KEY, "7 Developers and a Pastry Chef" )
                    putString( ALBUM_TITLE_KEY, "Tales from the Render Farm" )
                    putString( PATH_KEY, "path/to/Tales from the Render Farm/About a guy" )
                }
            )
        }.build() )
    }.build(),
    MediaItem.Builder().apply {
        setMediaId( UUID.randomUUID().toString() )
        setMediaMetadata( MediaMetadata.Builder().apply {
            setTitle( "Beat it" )
            setGenre( "Pop" )
            setExtras(
                Bundle().apply {
                    calendar.add( Calendar.DAY_OF_WEEK, 1 )
                    putLong( DATE_KEY, calendar.timeInMillis )
                    putString( ARTIST_KEY, "Michael Jackson" )
                    putString( ALBUM_TITLE_KEY, "Thriller" )
                    putString( PATH_KEY, "path/to/Thriller/Beat it" )
                }
            )
        }.build() )
    }.build(),
    MediaItem.Builder().apply {
        setMediaId( UUID.randomUUID().toString() )
        setMediaMetadata( MediaMetadata.Builder().apply {
            setTitle( "Billie Jean" )
            setGenre( "Pop" )
            setExtras(
                Bundle().apply {
                    calendar.add( Calendar.DAY_OF_WEEK, 1 )
                    putLong( DATE_KEY, calendar.timeInMillis )
                    putString( ARTIST_KEY, "Michael Jackson" )
                    putString( ALBUM_TITLE_KEY, "Thriller" )
                    putString( PATH_KEY, "path/to/Thriller/Billie Jean" )
                }
            )
        }.build() )
    }.build(),
    MediaItem.Builder().apply {
        setMediaId( UUID.randomUUID().toString() )
        setMediaMetadata( MediaMetadata.Builder().apply {
            setTitle( "Human Nature" )
            setGenre( "Pop" )
            setExtras(
                Bundle().apply {
                    calendar.add( Calendar.DAY_OF_WEEK, 1 )
                    putLong( DATE_KEY, calendar.timeInMillis )
                    putString( ARTIST_KEY, "Michael Jackson" )
                    putString( ALBUM_TITLE_KEY, "Thriller" )
                    putString( PATH_KEY, "path/to/Thriller/Human Nature" )
                }
            )
        }.build() )
    }.build(),
    MediaItem.Builder().apply {
        setMediaId( UUID.randomUUID().toString() )
        setMediaMetadata( MediaMetadata.Builder().apply {
            setTitle( "Bring the Noise" )
            setGenre( "Hip Hop" )
            setExtras(
                Bundle().apply {
                    calendar.add( Calendar.DAY_OF_WEEK, 1 )
                    putLong( DATE_KEY, calendar.timeInMillis )
                    putString( ARTIST_KEY, "Public Enemy" )
                    putString( ALBUM_TITLE_KEY, "It Takes a Nation of Millions to Hold Us Back" )
                    putString( PATH_KEY, "path/to/It Takes a Nation of Millions to Hold Us Back/Bring the Noise" )
                }
            )
        }.build() )
    }.build(),
    MediaItem.Builder().apply {
        setMediaId( UUID.randomUUID().toString() )
        setMediaMetadata( MediaMetadata.Builder().apply {
            setTitle( "Don't Believe the Hype" )
            setGenre( "Hip Hop" )
            setExtras(
                Bundle().apply {
                    calendar.add( Calendar.DAY_OF_WEEK, 1 )
                    putLong( DATE_KEY, calendar.timeInMillis )
                    putString( ARTIST_KEY, "Public Enemy" )
                    putString( ALBUM_TITLE_KEY, "It Takes a Nation of Millions to Hold Us Back" )
                    putString( PATH_KEY, "path/to/It Takes a Nation of Millions to Hold Us Back/Don't Believe the Hype" )
                }
            )
        }.build() )
    }.build(),
    MediaItem.Builder().apply {
        setMediaId( UUID.randomUUID().toString() )
        setMediaMetadata( MediaMetadata.Builder().apply {
            setTitle( "Cold Lampin' with Flavor" )
            setGenre( "Hip Hop" )
            setExtras(
                Bundle().apply {
                    putString( ALBUM_TITLE_KEY, "It Takes a Nation of Millions to Hold Us Back" )
                    putString( ARTIST_KEY, "Public Enemy" )
                    putString( PATH_KEY, "path/to/It Takes a Nation of Millions to Hold Us Back/Cold Lampin' with Flavor" )
                }
            )
        }.build() )
    }.build(),
    MediaItem.Builder().apply {
        setMediaId( UUID.randomUUID().toString() )
        setMediaMetadata( MediaMetadata.Builder().apply {
            setTitle( "1969" )
            setGenre( "Rock" )
            setExtras(
                Bundle().apply {
                    putString( ALBUM_TITLE_KEY, "The Stooges" )
                    putString( ARTIST_KEY, "the stooges" )
                    putString( PATH_KEY, "path/to/The Stooges/1969" )
                }
            )
        }.build() )
    }.build(),
    MediaItem.Builder().apply {
        setMediaId( UUID.randomUUID().toString() )
        setMediaMetadata( MediaMetadata.Builder().apply {
            setTitle( "I Wanna Be Your Dog" )
            setGenre( "Rock" )
            setExtras(
                Bundle().apply {
                    putString( ALBUM_TITLE_KEY, "The Stooges" )
                    putString( ARTIST_KEY, "the stooges" )
                    putString( PATH_KEY, "path/to/The Stooges/I Wanna Be Your Dog" )
                }
            )
        }.build() )
    }.build(),
    MediaItem.Builder().apply {
        setMediaId( UUID.randomUUID().toString() )
        setMediaMetadata( MediaMetadata.Builder().apply {
            setTitle( "We Will Fall" )
            setGenre( "Rock" )
            setExtras(
                Bundle().apply {
                    putString( ALBUM_TITLE_KEY, "The Stooges" )
                    putString( ARTIST_KEY, "the stooges" )
                    putString( PATH_KEY, "path/to/The Stooges/We Will Fall" )
                }
            )
        }.build() )
    }.build()
)

val testSongsForSorting = listOf(
    Song(
        id = "id1",
        mediaUri = Uri.EMPTY,
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
        genre = "Rap",
        size = 1L,
        mediaItem = MediaItem.EMPTY,
        path = "/path/to/song/7"
    ),
    Song(
        id = "id2",
        mediaUri = Uri.EMPTY,
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
        genre = "Hip Hop",
        size = 2L,
        mediaItem = MediaItem.EMPTY,
        path = "/path/to/song/8"
    ),
    Song(
        id = "id3",
        mediaUri = Uri.EMPTY,
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
        genre = "RnB",
        size = 3L,
        mediaItem = MediaItem.EMPTY,
        path = "/path/to/song/6"
    ),
    Song(
        id = "id4",
        mediaUri = Uri.EMPTY,
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
        genre = "Dance",
        size = 4L,
        mediaItem = MediaItem.EMPTY,
        path = "/path/to/song/1"
    ),
    Song(
        id = "id5",
        mediaUri = Uri.EMPTY,
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
        genre = "<unknown>",
        size = 5L,
        mediaItem = MediaItem.EMPTY,
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


val testSongs = testSongMediaItems.map { it.toSong( artistTagSeparators ) }