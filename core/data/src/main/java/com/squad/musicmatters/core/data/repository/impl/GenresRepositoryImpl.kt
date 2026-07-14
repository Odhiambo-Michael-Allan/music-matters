package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.data.repository.GenresRepository
import com.squad.musicmatters.core.data.store.GenresStore
import com.squad.musicmatters.core.data.store.MediaStoreListener
import com.squad.musicmatters.core.data.utils.sortGenres
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SortGenresBy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

class GenresRepositoryImpl @Inject constructor(
    private val genresStore: GenresStore,
    @param:Dispatcher( MusicMattersDispatchers.IO )
    private val ioDispatcher: CoroutineDispatcher,
) : GenresRepository {

    override fun fetchGenres(
        sortGenresBy: SortGenresBy,
        reverse: Boolean
    ): Flow<List<Genre>> = callbackFlow {

        fun fetchAndEmit() {
            launch( ioDispatcher ) {
                runCatching { genresStore.fetchGenres() }
                    .onSuccess { send( it.sortGenres( sortGenresBy, reverse ) ) }
            }
        }

        val mediaStoreListener = object : MediaStoreListener {
            override fun onMediaStoreChanged() {
                fetchAndEmit()
            }
        }
        genresStore.registerListener( mediaStoreListener )
        fetchAndEmit()
        awaitClose {
            genresStore.unregisterListener( mediaStoreListener )
        }
    }.flowOn( ioDispatcher )

    override fun fetchGenreWithId( id: Long ): Flow<Genre?> =
        flow<Genre?> { genresStore.fetchGenreWith( id ) }.flowOn( ioDispatcher )

    override fun searchGenresMatching(
        query: String,
        sortGenresBy: SortGenresBy,
        reverse: Boolean
    ): Flow<List<Genre>> = callbackFlow {

        fun fetchAndEmit() {
            launch( ioDispatcher ) {
                runCatching { genresStore.searchGenresMatching( query ) }
                    .onSuccess { send( it.sortGenres( sortGenresBy, reverse ) ) }
            }
        }

        val mediaStoreListener = object : MediaStoreListener {
            override fun onMediaStoreChanged() {
                fetchAndEmit()
            }
        }
        genresStore.registerListener( mediaStoreListener )
        fetchAndEmit()
        awaitClose {
            genresStore.unregisterListener( mediaStoreListener )
        }
    }.flowOn( ioDispatcher )

}