package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.flow.Flow

interface MostPlayedSongsRepository {

    fun fetchSongsSortedByPlayCount(): Flow<List<Song>>
    suspend fun addSongId( songId: String )
    suspend fun deleteSongWithId( songId: String )

}