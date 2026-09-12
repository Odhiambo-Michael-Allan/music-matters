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
import kotlinx.coroutines.flow.asFlow
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
    ): Flow<List<Genre>> = genresStore.fetchGenresFlow(
        sortGenresBy = sortGenresBy,
        sortGenresInReverse = reverse,
    )

    override fun fetchGenreWithId( id: Long ): Flow<Genre?> =
        flow {
            emit( genresStore.fetchGenreWith( id ) )
        }.flowOn( ioDispatcher )

    override fun fetchSongIdsInGenre( genreId: Long ): Flow<Set<Long>> = flow<Set<Long>> {
        emit( genresStore.fetchSongIdsInGenre( genreId ) )
    }.flowOn( ioDispatcher )

    override fun searchGenresMatching(
        query: String,
        sortGenresBy: SortGenresBy,
        reverse: Boolean
    ): Flow<List<Genre>> = flow<List<Genre>> {
        emit( genresStore.searchGenresMatching( query ) )
    }.flowOn( ioDispatcher )

}