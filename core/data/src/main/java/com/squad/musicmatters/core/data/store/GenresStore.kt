package com.squad.musicmatters.core.data.store

import com.squad.musicmatters.core.model.Genre

interface GenresStore {

    suspend fun fetchGenres(): List<Genre>
    suspend fun fetchGenreWith( id: Long ): Genre?
    suspend fun searchGenresMatching( query: String ): List<Genre>
    fun registerListener( listener: MediaStoreListener )
    fun unregisterListener( listener: MediaStoreListener )
}