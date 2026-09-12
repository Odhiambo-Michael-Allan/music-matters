package com.squad.musicmatters.core.data.store

import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SortGenresBy
import kotlinx.coroutines.flow.Flow

interface GenresStore {

    fun fetchGenresFlow(
        sortGenresBy: SortGenresBy? = null,
        sortGenresInReverse: Boolean = false,
    ): Flow<List<Genre>>
    suspend fun fetchGenreWith( id: Long ): Genre?
    suspend fun searchGenresMatching(
        query: String,
        sortGenresBy: SortGenresBy? = null,
        sortGenresInReverse: Boolean = false,
    ): List<Genre>

    suspend fun fetchSongIdsInGenre( genreId: Long ): Set<Long>

    fun unregisterListener( listener: MediaStoreListener )
}