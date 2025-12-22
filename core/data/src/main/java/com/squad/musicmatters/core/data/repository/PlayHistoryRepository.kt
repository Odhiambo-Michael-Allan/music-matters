package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface PlayHistoryRepository {
    fun fetchSongsSortedByTimePlayed(): Flow<List<Song>>
    suspend fun upsertSongWithId( songId: String, timePlayed: Instant )
    suspend fun deleteSongWithId( songId: String )
}