package com.squad.musicmatters.core.data.store.impl

import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns
import android.provider.MediaStore.Audio.AudioColumns.IS_MUSIC
import androidx.core.database.getLongOrNull
import com.squad.musicmatters.core.data.store.GenresStore
import com.squad.musicmatters.core.data.store.MediaStoreListener
import com.squad.musicmatters.core.data.store.MusicMattersMediaStore
import com.squad.musicmatters.core.data.store.getLongFrom
import com.squad.musicmatters.core.data.store.getNullableStringFrom
import com.squad.musicmatters.core.data.utils.sortGenres
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SortGenresBy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class GenresStoreImpl(
    context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    ioScope: CoroutineScope,
) : MusicMattersMediaStore(
    context = context,
    ioScope = ioScope,
), GenresStore {

    private var _cachedGenres = MutableSharedFlow<List<Genre>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        ioScope.launch { _cachedGenres.tryEmit( fetchGenres() ) }
        registerListener(
            object : MediaStoreListener {
                override fun onMediaStoreChanged() {
                    ioScope.launch { _cachedGenres.tryEmit( fetchGenres() ) }
                }
            }
        )
    }

    override fun fetchGenresFlow(
        sortGenresBy: SortGenresBy?,
        sortGenresInReverse: Boolean,
    ): Flow<List<Genre>> = _cachedGenres.map {
        it.sortGenres(
            by = sortGenresBy ?: DefaultPreferences.SORT_GENRES_BY,
            reverse = sortGenresInReverse
        )
    }.flowOn( ioDispatcher )

    private suspend fun fetchGenres(): List<Genre> =
        fetchGenresFromCursor( createGenreCursor() )

    override suspend fun fetchGenreWith( id: Long ): Genre? =
        withContext( ioDispatcher ) {
            createGenreCursor()?.let { cursor ->
                cursor.use { fetchGenreFromCursor( it ) }
            }
        }

    override suspend fun searchGenresMatching(
        query: String,
        sortGenresBy: SortGenresBy?,
        sortGenresInReverse: Boolean,
    ): List<Genre> =
        fetchGenresFromCursor(
            createGenreCursor(
                query = query,
//                sortOrder = sortGenresBy?.toMediaStoreSortFormat()
//                    ?: SortGenresBy.NAME.toMediaStoreSortFormat(),
            )
        )

    override suspend fun fetchSongIdsInGenre( genreId: Long ): Set<Long> =
        withContext( ioDispatcher ) {
            val ids = mutableSetOf<Long>()
            createGenreSongCursor( genreId )?.use { cursor ->
                if ( cursor.moveToFirst() ) {
                    do {
                        val columnIndex = cursor.getColumnIndex( AudioColumns._ID )
                        cursor.getLongOrNull( columnIndex )?.let { ids.add( it ) }
                    } while ( cursor.moveToNext() )
                }
            }
            Timber.tag( TAG ).d( "IDS OF SONGS IN GENRE: $ids" )
            ids
        }


    private suspend fun fetchGenresFromCursor( cursor: Cursor? ): List<Genre> =
        withContext( ioDispatcher ) {
            val genres = arrayListOf<Genre>()
            cursor?.use {
                if ( cursor.moveToFirst() ) {
                    do {
                        val genre = fetchGenreFromCursor( cursor )
                        if ( genre.numberOfTracks > 0 ) {
                            genres.add( genre )
                        }
                    } while ( cursor.moveToNext() )
                }
            }
            Timber.tag( TAG ).d( "GENRES FETCHED: $genres" )
            genres
        }

    private fun fetchGenreFromCursor( cursor: Cursor ): Genre {
        val id = cursor.getLongFrom( MediaStore.Audio.Genres._ID )
        val name = cursor.getNullableStringFrom( MediaStore.Audio.Genres.NAME )
        val trackCount = getTrackCount( id )
        return Genre(
            id = id,
            name = name ?: "",
            numberOfTracks = trackCount,
        )
    }

    private fun getTrackCount( genreId: Long ): Int {
        contentResolver.query(
            MediaStore.Audio.Genres.Members.getContentUri( "external", genreId ),
            null,
            null,
            null,
            null,
        ).use {
            return it?.count ?: 0
        }
    }

    private fun createGenreCursor(): Cursor? {
        val projection = arrayOf( MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME )
        return try {
            contentResolver.query(
                MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
            )
        } catch ( e: SecurityException ) {
            Timber.tag( TAG )
                .d( "ERROR OCCURRED WHILE CREATING GENRE CURSOR: ${e.stackTrace}" )
            return null
        }
    }

    private fun createGenreSongCursor( genreId: Long ): Cursor? {
        return try {
            val projection = arrayOf( BaseColumns._ID )
            contentResolver.query(
                MediaStore.Audio.Genres.Members.getContentUri( "external", genreId ),
                projection,
                IS_MUSIC,
                null,
                null,
            )
        } catch ( e: SecurityException ) {
            return null
        }
    }

    private fun createGenreCursor(
        query: String,
//        sortOrder: String,
    ): Cursor? {
        val projection = arrayOf( MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME )
        return try {
            contentResolver.query(
                MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Audio.Genres.NAME + " LIKE ?",
                arrayOf( query ),
                null,
            )
        } catch ( e: SecurityException ) {
            Timber.tag( TAG )
                .d( "ERROR OCCURRED WHILE FETCHING GENRES: ${e.stackTrace}" )
            return null
        }
    }

}

private fun SortGenresBy.toMediaStoreSortFormat() = when ( this ) {
    SortGenresBy.NAME -> MediaStore.Audio.Genres.NAME
    SortGenresBy.TRACK_COUNT -> MediaStore.Audio.Genres._COUNT
    SortGenresBy.CUSTOM -> MediaStore.Audio.Genres.DEFAULT_SORT_ORDER
}
private const val TAG = "GENRE-STORE"