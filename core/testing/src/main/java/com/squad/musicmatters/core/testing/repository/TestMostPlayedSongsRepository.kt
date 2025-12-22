package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.repository.MostPlayedSongsRepository
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first

class TestMostPlayedSongsRepository : MostPlayedSongsRepository {

    private val songsFlow: MutableSharedFlow<List<Song>> =
        MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )

    override fun fetchSongsSortedByPlayCount(): Flow<List<Song>> = songsFlow

    override suspend fun addSongId( songId: String ) {
        println( "ADDING SONG WITH ID: $songId" )
        val currentSongs = songsFlow.first().toMutableList()
        currentSongs.add(
            Song(
                id = songId,
                mediaUri = "Uri.EMPTY",
                title = "",
                displayTitle = "",
                duration = 0L,
                artists = emptySet(),
                size = 0L,
                dateModified = 0L,
                path = "",
                trackNumber = null,
                year = null,
                albumTitle = null,
                composer = null,
                artworkUri = null,
            )
        )
        println( "EMITTING SONGS: $currentSongs" )
        songsFlow.tryEmit( currentSongs )
    }

    override suspend fun deleteSongWithId( songId: String ) {
        val currentSongs = songsFlow.first().toMutableList()
        currentSongs.removeIf { it.id == songId }
        songsFlow.tryEmit( currentSongs )
    }

    fun sendSongs( songs: List<Song> ) {
        songsFlow.tryEmit( songs )
    }
}