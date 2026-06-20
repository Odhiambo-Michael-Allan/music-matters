package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.model.QueueEntry
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FakeQueueRepository : QueueRepository {

    private val songsFlow: MutableSharedFlow<List<Song>> = MutableSharedFlow(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val queue = mutableListOf<QueueEntry>()

    override fun fetchSongsSortedByCurrentPosition(): Flow<List<Song>> = songsFlow.map {
        val songs = mutableListOf<Song>()
        queue.sortedBy { it.currentPositionInQueue }.forEach { queueEntry ->
            songsFlow.first().firstOrNull { it.id == queueEntry.songId }?.let { songs.add( it ) }
        }
        songs
    }

    override fun fetchSongsSortedByOriginalPosition(): Flow<List<Song>> = songsFlow.map {
        val songs = mutableListOf<Song>()
        queue.sortedBy { it.originalPositionInQueue }.forEach { queueEntry ->
            songsFlow.first().firstOrNull { it.id == queueEntry.songId }?.let { songs.add( it ) }
        }
        songs
    }

    override suspend fun saveQueue( queue: List<QueueEntry> ) {
        this.queue.clear()
        this.queue.addAll( queue )
    }

    override suspend fun removeSongWithId( id: String ) {
        queue.removeIf { it.songId == id }
        val currentSongs = songsFlow.first().toMutableList()
        currentSongs.filter { it.id != id }
        songsFlow.tryEmit( currentSongs )
    }

    override suspend fun clearQueue() {
        this.queue.clear()
        songsFlow.tryEmit( emptyList() )
    }

    fun sendSongs( songs: List<Song> ) {
        songsFlow.tryEmit( songs )
    }

}