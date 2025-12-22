package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.flow.Flow

interface QueueRepository {
    fun fetchSongsInQueueSortedByPosition(): Flow<List<Song>>
    suspend fun upsertSong( song: Song, posInQueue: Int )
    suspend fun saveQueue( queue: List<Song> )
    suspend fun removeSongWithId( id: String )
    suspend fun clearQueue()
}