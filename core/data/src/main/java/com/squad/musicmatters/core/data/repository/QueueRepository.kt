package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.QueueEntry
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.flow.Flow

interface QueueRepository {

    fun fetchSongsSortedByCurrentPosition(): Flow<List<Song>>
    fun fetchSongsSortedByOriginalPosition(): Flow<List<Song>>
    suspend fun saveQueue( queue: List<QueueEntry> )
    suspend fun removeSongWithId( id: String )
    suspend fun clearQueue()

}