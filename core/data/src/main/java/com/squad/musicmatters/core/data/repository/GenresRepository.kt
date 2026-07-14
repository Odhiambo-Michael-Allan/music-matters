package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SortGenresBy
import kotlinx.coroutines.flow.Flow

interface GenresRepository {

    fun fetchGenres(
        sortGenresBy: SortGenresBy,
        reverse: Boolean = false,
    ): Flow<List<Genre>>

    fun fetchGenreWithId( id: Long ): Flow<Genre?>

    fun searchGenresMatching(
        query: String,
        sortGenresBy: SortGenresBy,
        reverse: Boolean
    ): Flow<List<Genre>>

}