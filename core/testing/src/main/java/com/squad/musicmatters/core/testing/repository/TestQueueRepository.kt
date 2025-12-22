package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first

class TestQueueRepository : QueueRepository {

    private val songsFlow: MutableSharedFlow<List<Song>> = MutableSharedFlow(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun fetchSongsInQueueSortedByPosition(): Flow<List<Song>> = songsFlow

    override suspend fun upsertSong(
        song: Song,
        posInQueue: Int
    ) {
        val currentSongs = songsFlow.first().toMutableList()
        currentSongs.add( posInQueue, song )
        songsFlow.tryEmit( currentSongs )
    }

    override suspend fun saveQueue( queue: List<Song> ) {
        songsFlow.tryEmit( queue )
    }

    override suspend fun removeSongWithId( id: String ) {
        val currentSongs = songsFlow.first().toMutableList()
        currentSongs.removeIf { it.id == id }
        songsFlow.tryEmit( currentSongs )
    }

    override suspend fun clearQueue() {
        songsFlow.tryEmit( emptyList() )
    }

    fun sendSongs( songs: List<Song> ) {
        songsFlow.tryEmit( songs )
    }
}