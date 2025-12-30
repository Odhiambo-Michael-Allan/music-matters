package com.squad.musicmatters.core.data.songs.impl

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.squad.musicmatters.core.data.songs.MediaPermissionsManager
import com.squad.musicmatters.core.data.songs.SongsStore
import com.squad.musicmatters.core.data.songs.SongsStoreListener
import com.squad.musicmatters.core.data.utils.sortSongs
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongsStoreImpl @Inject constructor(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    applicationScope: CoroutineScope,
) : SongsStore {

    private val collectionUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    private val listeners = mutableSetOf<SongsStoreListener>()
    private val contentResolver: ContentResolver = context.contentResolver

    // Components to manage the ContentObserver lifecycle
    private val mediaStoreObserverHandlerThread: HandlerThread = HandlerThread(
        "MediaStoreObserverHandlerThread"
    )
    private val mediaStoreObserver: MediaStoreObserver

    // A flag to check if the store has been initialized/released
    private var mediaObserverIsRegistered = false

    init {
        // Start the thread and initialize the observer on a background looper
        mediaStoreObserverHandlerThread.start()
        mediaStoreObserver = MediaStoreObserver(
            Handler( mediaStoreObserverHandlerThread.looper )
        )
        contentResolver.registerContentObserver(
            collectionUri,
            true,
            mediaStoreObserver
        )
        mediaObserverIsRegistered = true

        applicationScope.launch {
            MediaPermissionsManager.hasAllRequiredPermissions.collect { granted ->
                if ( granted ) { listeners.forEach { it.onMediaStoreChanged() } }
            }
        }
    }

    @WorkerThread
    override suspend fun fetchSongs(
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean?
    ): List<Song> =
        withContext( ioDispatcher )  {
            val mediaItemList = mutableListOf<MediaItem>()
            try {
                Log.d( TAG, "FETCHING SONGS.." )
                context.contentResolver.query(
                    collectionUri,
                    projection,
                    MediaStore.Audio.Media.IS_MUSIC + " = 1",
                    null,
                    null,
                )?.use {
                    while ( it.moveToNext() ) {
                        kotlin.runCatching {
                            MediaItem.Builder().from( it )
                        }.getOrNull()?.also { mediaItemBuilder ->
                            mediaItemList.add( mediaItemBuilder.build() )
                        }
                    }
                }
                return@withContext mediaItemList
                    .map { it.toSong( artistTagSeparators ) }
                    .sortSongs(
                        sortSongsBy = sortSongsBy ?: DefaultPreferences.SORT_SONGS_BY,
                        reverse = sortSongsInReverse ?: false
                    )
            } catch ( exception: Exception ) {
                exception.message?.let { Log.e( TAG, it ) }
                return@withContext emptyList()
            }
        }

    override fun registerListener( listener: SongsStoreListener ) {
        listeners.add( listener )
    }

    override fun unregisterListener( listener: SongsStoreListener ) {
        listeners.remove( listener )
    }

    /**
     * Public method to clean up the ContentObserver and its HandlerThread.
     * This MUST be called by the hosting service (MediaLibraryService)
     * onDestroy()
     */
    fun release() {
        if ( mediaObserverIsRegistered ) {
            contentResolver.unregisterContentObserver( mediaStoreObserver )
            mediaStoreObserverHandlerThread.quitSafely()
            listeners.clear()
            mediaObserverIsRegistered = false
        }
    }

    // The custom ContentObserver
    private inner class MediaStoreObserver(
        private val handler: Handler
    ) : ContentObserver( handler ), Runnable {

        override fun onChange( selfChange: Boolean ) {
            // Debouncing logic: remove previously scheduled callback, then post new delayed callback.
            handler.removeCallbacks( this )
            handler.postDelayed( this, 500 )
        }

        override fun run() {
            listeners.forEach { it.onMediaStoreChanged() }
        }

    }

}

private fun MediaItem.Builder.from( cursor: Cursor ): MediaItem.Builder {
    val mediaUri = getMediaUriFrom( cursor )
    setMediaId( mediaUri.toString() )
    setUri( mediaUri )
    setMediaMetadata(
        MediaMetadata.Builder().from( cursor ).build()
    )
    return this
}

private fun getMediaUriFrom( cursor: Cursor ): Uri = ContentUris.withAppendedId(
    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLongFrom( AudioColumns._ID )
)

private fun MediaMetadata.Builder.from( cursor: Cursor ): MediaMetadata.Builder {

    val id = cursor.getLongFrom( AudioColumns._ID )
    val title = cursor.getStringFrom( AudioColumns.TITLE )
    val trackNumber = cursor.getNullableIntFrom( AudioColumns.TRACK ) ?: UNKNOWN_INT_VALUE
    val year = cursor.getNullableIntFrom( AudioColumns.YEAR ) ?: UNKNOWN_INT_VALUE
    val duration = cursor.getLongFrom( AudioColumns.DURATION )
    val albumTitle = cursor.getNullableStringFrom( AudioColumns.ALBUM ) ?: UNKNOWN_STRING_VALUE
    val artist = cursor.getNullableStringFrom( AudioColumns.ARTIST ) ?: UNKNOWN_STRING_VALUE
    val albumArtist = cursor.getNullableStringFrom( AudioColumns.ALBUM_ARTIST ) ?: UNKNOWN_STRING_VALUE
    val composer = cursor.getNullableStringFrom( AudioColumns.COMPOSER ) ?: UNKNOWN_STRING_VALUE
    val dateAdded = cursor.getLongFrom( AudioColumns.DATE_MODIFIED )
    val dateModified = cursor.getLongFrom( AudioColumns.DATE_MODIFIED )
    val size = cursor.getNullableLongFrom( AudioColumns.SIZE ) ?: UNKNOWN_LONG_VALUE
    val path = cursor.getNullableStringFrom( AudioColumns.DATA ) ?: UNKNOWN_STRING_VALUE

    setTitle( title )
    setDisplayTitle( title )
    setTrackNumber( trackNumber )
    setReleaseYear( year )
    setAlbumTitle( albumTitle )
    setArtist( artist )
    setAlbumArtist( albumArtist )
    setComposer( composer )
    setIsPlayable( true )
    setArtworkUri( getArtworkUriWith( cursor ) )
    setIsBrowsable( false )

    setExtras(
        Bundle().apply {
            putLong(SONG_DURATION, duration )
            putLong(DATE_KEY, if ( dateAdded == UNKNOWN_LONG_VALUE) dateModified else dateAdded )
            putLong(SIZE_KEY, size )
            putString(PATH_KEY, path )
            // I don't know why these 5 values are not sent to the UI so i will just add them again
            // here as part of the extras.
            putString(DISPLAY_TITLE_KEY, title )
            putInt(TRACK_NUMBER_KEY, trackNumber )
            putInt(RELEASE_YEAR_KEY, year )
            putString(ALBUM_TITLE_KEY, albumTitle )
            putString(ARTIST_KEY, artist )
            putLong(MEDIA_ITEM_ID_KEY, id )
        }
    )
    return this
}

private fun MediaItem.toSong( artistTagSeparators: Set<String> ) = Song(
    id = mediaId,
    mediaUri = mediaId,
    title = mediaMetadata.title.toString(),
    displayTitle = mediaMetadata.extras?.getString( DISPLAY_TITLE_KEY ) ?: UNKNOWN_STRING_VALUE,
    trackNumber = mediaMetadata.extras?.getInt( TRACK_NUMBER_KEY ),
    year = mediaMetadata.extras?.getInt( RELEASE_YEAR_KEY ),
    duration = mediaMetadata.extras?.getLong( SONG_DURATION ) ?: UNKNOWN_LONG_VALUE,
    albumTitle = mediaMetadata.extras?.getString( ALBUM_TITLE_KEY ),
    artists = parseArtistStringIntoIndividualArtists( artistTagSeparators ),
    composer = mediaMetadata.composer.toString(),
    dateModified = mediaMetadata.extras?.getLong( DATE_KEY ) ?: UNKNOWN_LONG_VALUE ,
    size = mediaMetadata.extras?.getLong( SIZE_KEY ) ?: UNKNOWN_LONG_VALUE,
    path = mediaMetadata.extras?.getString( PATH_KEY ) ?: UNKNOWN_STRING_VALUE,
    artworkUri = mediaMetadata.artworkUri.toString(),
)

private fun getArtworkUriWith(cursor: Cursor ): Uri? = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    .buildUpon()
    .run {
        appendPath( cursor.getLongFrom( AudioColumns._ID ).toString() )
        appendPath( "albumart" )
        build()
    }

private fun MediaItem.parseArtistStringIntoIndividualArtists( separators: Set<String> ): Set<String> {
    val artistsSet = mediaMetadata.extras?.getString(ARTIST_KEY)?.split( *separators.toTypedArray() )
        ?.mapNotNull { x -> x.trim().takeIf { it.isNotEmpty() } }
        ?.toSet() ?: setOf()
    return artistsSet
}

private fun Cursor.getLongFrom( columnName: String ): Long {
    val columnIndex = getColumnIndex( columnName )
    return getLong( columnIndex )
}

private fun Cursor.getNullableLongFrom(columnName: String ): Long? {
    val columnIndex = getColumnIndex( columnName )
    return getLongOrNull( columnIndex )
}

private fun Cursor.getNullableIntFrom( columnName: String ): Int? {
    val columnIndex = getColumnIndex( columnName )
    return getIntOrNull( columnIndex )
}

private fun Cursor.getNullableStringFrom( columnName: String ): String? {
    val columnIndex = getColumnIndex( columnName )
    return getStringOrNull( columnIndex )
}

private fun Cursor.getStringFrom( columnName: String ): String {
    val columnIndex = getColumnIndex( columnName );
    return getString( columnIndex )
}

const val UNKNOWN_LONG_VALUE = 0L
const val UNKNOWN_INT_VALUE = 0
const val UNKNOWN_STRING_VALUE = "<unknown>"
const val SONG_DURATION = "SONG-DURATION"
const val DATE_KEY = "DATE"
const val SIZE_KEY = "SIZE"
const val PATH_KEY = "PATH"
const val DISPLAY_TITLE_KEY = "DISPLAY-TITLE"
const val TRACK_NUMBER_KEY = "TRACK-NUMBER"
const val RELEASE_YEAR_KEY = "RELEASE-YEAR"
const val ALBUM_TITLE_KEY = "ALBUM-TITLE"
const val ARTIST_KEY = "ARTIST"
const val MEDIA_ITEM_ID_KEY = "--MEDIA-ITEM-ID-KEY--"

val projection = arrayOf(
    AudioColumns._ID,
    AudioColumns.DATE_ADDED,
    AudioColumns.DATE_MODIFIED,
    AudioColumns.TITLE,
    AudioColumns.TRACK,
    AudioColumns.YEAR,
    AudioColumns.DURATION,
    AudioColumns.ALBUM_ID,
    AudioColumns.ALBUM,
    AudioColumns.ARTIST_ID,
    AudioColumns.ARTIST,
    AudioColumns.COMPOSER,
    AudioColumns.SIZE,
    AudioColumns.DATA,
    AudioColumns.ALBUM_ID,
)

private val artistTagSeparators = setOf( "Feat", "feat.", "Ft", "ft", ",", "&" )

private const val TAG = "SONGS-STORE"