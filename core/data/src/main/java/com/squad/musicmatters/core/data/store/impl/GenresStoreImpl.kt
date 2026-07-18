package com.squad.musicmatters.core.data.store.impl

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.squad.musicmatters.core.data.store.GenresStore
import com.squad.musicmatters.core.data.store.MusicMattersMediaStore
import com.squad.musicmatters.core.data.store.getLongFrom
import com.squad.musicmatters.core.data.store.getNullableStringFrom
import com.squad.musicmatters.core.model.Genre
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
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

    override suspend fun fetchGenres(): List<Genre> =
        withContext( ioDispatcher ) { fetchGenresFromCursor( createGenreCursor() ) }

    override suspend fun fetchGenreWith( id: Long ): Genre? =
        withContext( ioDispatcher ) {
            createGenreCursor()?.let {
                fetchGenreFromCursor( it )
            }
        }

    override suspend fun searchGenresMatching( query: String ): List<Genre> =
        fetchGenresFromCursor( createGenreCursor( query ) )


    private fun fetchGenresFromCursor( cursor: Cursor? ): List<Genre> {
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
        return genres
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

    private fun createGenreCursor( query: String ): Cursor? {
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

private const val TAG = "GENRE-STORE"