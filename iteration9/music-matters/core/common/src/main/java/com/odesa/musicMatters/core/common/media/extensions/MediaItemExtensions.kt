package com.odesa.musicMatters.core.common.media.extensions

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.odesa.musicMatters.core.model.Album
import com.odesa.musicMatters.core.model.Artist
import com.odesa.musicMatters.core.model.Genre
import com.odesa.musicMatters.core.model.Song
import timber.log.Timber

fun MediaItem.Builder.from( cursor: Cursor, context: Context ): MediaItem.Builder {
    val mediaUri = getMediaUriFrom( cursor )
    setMediaId( mediaUri.toString() )
    setUri( mediaUri )
    setMediaMetadata(
        MediaMetadata.Builder().from( cursor, context, mediaUri ).build()
    )
    return this
}

fun getMediaUriFrom( cursor: Cursor ): Uri = ContentUris.withAppendedId(
    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLongFrom( AudioColumns._ID )
)

fun MediaMetadata.Builder.from( cursor: Cursor, context: Context, mediaUri: Uri ): MediaMetadata.Builder {
//    val mediaMetadataRetriever = MediaMetadataRetriever()
//    mediaMetadataRetriever.setDataSource( context, mediaUri )
    val genreUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI

    val id = cursor.getLongFrom( AudioColumns._ID )
    val title = cursor.getStringFrom( AudioColumns.TITLE )
    Timber.tag( TAG ).d( "Title: $title" )

    val trackNumber = cursor.getNullableIntFrom( AudioColumns.TRACK ) ?: UNKNOWN_INT_VALUE
    Timber.tag( TAG ).d( "Track Number: $trackNumber" )

    val year = cursor.getNullableIntFrom( AudioColumns.YEAR ) ?: UNKNOWN_INT_VALUE
    Timber.tag( TAG ).d( "Year: $year" )

    val duration = cursor.getLongFrom( AudioColumns.DURATION )
    Timber.tag( TAG ).d( "Duration: $duration" )

    val albumTitle = cursor.getNullableStringFrom( AudioColumns.ALBUM ) ?: UNKNOWN_STRING_VALUE
    Timber.tag( TAG ).d( "Album: $albumTitle" )

    val artist = cursor.getNullableStringFrom( AudioColumns.ARTIST ) ?: UNKNOWN_STRING_VALUE
    Timber.tag( TAG ).d( "Artist: $artist" )

    val albumArtist = cursor.getNullableStringFrom( AudioColumns.ALBUM_ARTIST ) ?: UNKNOWN_STRING_VALUE
    Timber.tag( TAG ).d( "Album Artist: $albumArtist" )

    val composer = cursor.getNullableStringFrom( AudioColumns.COMPOSER ) ?: UNKNOWN_STRING_VALUE
    Timber.tag( TAG ).d( "Composer: $composer" )

    val dateAdded = cursor.getLongFrom( AudioColumns.DATE_MODIFIED )
    Timber.tag( TAG ).d( "Date Added: $dateAdded" )

    val dateModified = cursor.getLongFrom( AudioColumns.DATE_MODIFIED )
    Timber.tag( TAG ).d( "Date Modified: $dateModified" )

    val size = cursor.getNullableLongFrom( AudioColumns.SIZE ) ?: UNKNOWN_LONG_VALUE
    Timber.tag( TAG ).d( "Size: $size" )
    val path = cursor.getNullableStringFrom( AudioColumns.DATA ) ?: UNKNOWN_STRING_VALUE
    Timber.tag( TAG ).d( "Path: $path" )

    // =============================== Genre ===============================

    var _genre: String? = null
    val genreAudioUri = MediaStore.Audio.Genres.getContentUriForAudioId(
        "external",
        id.toInt()
    )
    val genreCursor = context.contentResolver.query(
        genreAudioUri, null, null, null, null
    )
    if ( genreCursor != null && genreCursor.moveToFirst() ) {
        val genreIndex = genreCursor.getColumnIndex( MediaStore.Audio.GenresColumns.NAME )
        _genre = genreCursor.getString( genreIndex )
    }
    genreCursor?.close()
    val genre = _genre  ?: UNKNOWN_STRING_VALUE
    val finalGenre = genre.split( *genreTagSeparators.toTypedArray() ).first()
    Timber.tag( TAG ).d( "Genre: $finalGenre" )

    // =====================================================================

    Timber.tag( TAG ).d( "------------------------------------------------------------------" )

    setTitle( title )
    setDisplayTitle( title )
    setTrackNumber( trackNumber )
    setReleaseYear( year )
    setAlbumTitle( albumTitle )
    setArtist( artist )
    setAlbumArtist( albumArtist )
    setComposer( composer )
    setGenre( finalGenre )
    setIsPlayable( true )
    setArtworkUri( getArtworkUriWith( cursor ) )
    setIsBrowsable( false )

    setExtras(
        Bundle().apply {
            putLong( SONG_DURATION, duration )
            putLong( DATE_KEY, if ( dateAdded == UNKNOWN_LONG_VALUE ) dateModified else dateAdded )
            putLong( SIZE_KEY, size )
            putString( PATH_KEY, path )
            // I don't know why these 5 values are not sent to the UI so i will just add them again
            // here as part of the extras.
            putString( DISPLAY_TITLE_KEY, title )
            putInt( TRACK_NUMBER_KEY, trackNumber )
            putInt( RELEASE_YEAR_KEY, year )
            putString( ALBUM_TITLE_KEY, albumTitle )
            putString( ARTIST_KEY, artist )
        }
    )
    return this
}

fun getArtworkUriWith(cursor: Cursor ): Uri? = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    .buildUpon()
    .run {
        appendPath( cursor.getLongFrom( AudioColumns._ID ).toString() )
        appendPath( "albumart" )
        build()
    }


fun MediaItem.toSong( artistTagSeparators: Set<String> ) = Song(
    id = mediaId,
    mediaUri = Uri.parse( mediaId ),
    title = mediaMetadata.title.toString(),
    displayTitle = mediaMetadata.extras?.getString( DISPLAY_TITLE_KEY ) ?: UNKNOWN_STRING_VALUE,
    trackNumber = mediaMetadata.extras?.getInt( TRACK_NUMBER_KEY ),
    year = mediaMetadata.extras?.getInt( RELEASE_YEAR_KEY ),
    duration = mediaMetadata.extras?.getLong( SONG_DURATION ) ?: UNKNOWN_LONG_VALUE,
    albumTitle = mediaMetadata.extras?.getString( ALBUM_TITLE_KEY ),
    genre = mediaMetadata.genre?.toString(),
    artists = parseArtistStringIntoIndividualArtists( artistTagSeparators ),
    composer = mediaMetadata.composer.toString(),
    dateModified = mediaMetadata.extras?.getLong( DATE_KEY ) ?: UNKNOWN_LONG_VALUE ,
    size = mediaMetadata.extras?.getLong( SIZE_KEY ) ?: UNKNOWN_LONG_VALUE,
    path = mediaMetadata.extras?.getString( PATH_KEY ) ?: UNKNOWN_STRING_VALUE,
    artworkUri = mediaMetadata.artworkUri,
    mediaItem = this
)

fun MediaItem.toAlbum( songs: List<Song> ) = Album(
    title = mediaMetadata.title.toString(),
    artists = parseAlbumArtistsStringIntoIndividualArtists( artistTagSeparators ),
    trackCount = songs.count { it.albumTitle == mediaMetadata.title.toString() },
    artworkUri = mediaMetadata.artworkUri
)

fun MediaItem.toArtist( songs: List<Song>, albums: List<Album> ) = Artist(
    name = mediaMetadata.title.toString(),
    artworkUri = mediaMetadata.artworkUri,
    trackCount = songs.count { it.artists.contains( this.mediaMetadata.title ) },
    albumCount = albums.count { it.artists.contains( mediaMetadata.title ) }
)

fun MediaItem.toGenre( songs: List<Song> ) = Genre(
    name = mediaMetadata.title.toString(),
    numberOfTracks = songs.count { it.genre == mediaMetadata.title.toString() }
)

fun MediaItem.parseArtistStringIntoIndividualArtists( separators: Set<String> ): Set<String> {
    val artistsSet = mediaMetadata.extras?.getString( ARTIST_KEY )?.split( *separators.toTypedArray() )
        ?.mapNotNull { x -> x.trim().takeIf { it.isNotEmpty() } }
        ?.toSet() ?: setOf()
    return artistsSet
}

fun MediaItem.parseAlbumArtistsStringIntoIndividualArtists( separators: Set<String> ) =
    mediaMetadata.extras?.getString( ARTIST_KEY )?.split( *separators.toTypedArray() )
    ?.mapNotNull { x -> x.trim().takeIf { it.isNotEmpty() } }
    ?.toSet() ?: setOf()

fun MediaItem.stringRep() = StringBuilder().apply {
    appendWithLineBreak( "Media Id: $mediaId" )
    appendWithLineBreak( "Media Uri: ${Uri.parse( mediaId ) }" )
    appendWithLineBreak( "Title: ${mediaMetadata.title}\n" )
    appendWithLineBreak( "Display Title: ${mediaMetadata.extras?.getString( DISPLAY_TITLE_KEY)}" )
    appendWithLineBreak( "Track Number: ${mediaMetadata.extras?.getInt( TRACK_NUMBER_KEY)}" )
    appendWithLineBreak( "Release Year: ${mediaMetadata.extras?.getInt( RELEASE_YEAR_KEY)}" )
    appendWithLineBreak( "Album title: ${mediaMetadata.extras?.getString( ALBUM_TITLE_KEY)}" )
    appendWithLineBreak( "Artist: ${mediaMetadata.extras?.getString( ARTIST_KEY)}" )
    appendWithLineBreak( "Duration: ${mediaMetadata.extras?.getLong( SONG_DURATION )}" )
    appendWithLineBreak( "Date Modified: ${mediaMetadata.extras?.getLong( DATE_KEY )}" )
    appendWithLineBreak( "Size: ${mediaMetadata.extras?.getLong( SIZE_KEY )}" )
    appendWithLineBreak( "Path: ${mediaMetadata.extras?.getString( PATH_KEY )}" )
    appendWithLineBreak( "Bitrate: ${mediaMetadata.extras?.getLong( BITRATE_KEY )}" )
    appendWithLineBreak( "Bits per sample: ${mediaMetadata.extras?.getLong( BITS_PER_SAMPLE_KEY )}" )
    appendWithLineBreak( "Codec: ${mediaMetadata.extras?.getString( CODEC_KEY )}" )
    appendWithLineBreak( "Sampling rate: ${mediaMetadata.extras?.getLong( SAMPLING_RATE_KEY )}" )
}.toString()

fun StringBuilder.appendWithLineBreak( content: String ): StringBuilder = append( content + "\n" )

val genreTagSeparators = setOf( "/", "," )

const val UNKNOWN_LONG_VALUE = 0L
const val UNKNOWN_INT_VALUE = 0
const val UNKNOWN_STRING_VALUE = "<unknown>"
const val SONG_DURATION = "SONG-DURATION"
const val DATE_KEY = "DATE"
const val SIZE_KEY = "SIZE"
const val PATH_KEY = "PATH"
const val BITRATE_KEY = "BITRATE"
const val BITS_PER_SAMPLE_KEY = "BITS_PER_SAMPLE"
const val CODEC_KEY = "CODEC"
const val SAMPLING_RATE_KEY = "SAMPLING_RATE"
const val DISPLAY_TITLE_KEY = "DISPLAY-TITLE"
const val TRACK_NUMBER_KEY = "TRACK-NUMBER"
const val RELEASE_YEAR_KEY = "RELEASE-YEAR"
const val ALBUM_TITLE_KEY = "ALBUM-TITLE"
const val ARTIST_KEY = "ARTIST"
const val TAG = "MEDIA-ITEM-BUILDER-FROM"