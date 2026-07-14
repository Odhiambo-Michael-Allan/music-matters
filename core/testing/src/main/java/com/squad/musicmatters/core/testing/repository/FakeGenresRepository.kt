package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.repository.GenresRepository
import com.squad.musicmatters.core.data.utils.sortGenres
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SortGenresBy
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class FakeGenresRepository : GenresRepository {

    private val genresFlow: MutableSharedFlow<List<Genre>> =
        MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )

    override fun fetchGenres(
        sortGenresBy: SortGenresBy,
        reverse: Boolean
    ): Flow<List<Genre>> = genresFlow.map { genres ->
        genres.sortGenres(
            by = sortGenresBy,
            reverse = reverse,
        )
    }

    override fun fetchGenreWithId( id: Long ): Flow<Genre?> = genresFlow.map { genres ->
        genres.find { it.id == id }
    }

    override fun searchGenresMatching(
        query: String,
        sortGenresBy: SortGenresBy,
        reverse: Boolean
    ): Flow<List<Genre>> = genresFlow.map { genres ->
        genres.sortGenres(
            by = sortGenresBy,
            reverse = reverse,
        )
    }

    fun sendGenres( genres: List<Genre> ) {
        genresFlow.tryEmit( genres )
    }

}