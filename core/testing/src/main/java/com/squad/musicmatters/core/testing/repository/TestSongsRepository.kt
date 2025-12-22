package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.utils.sortSongs
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestSongsRepository : SongsRepository {

    private val songsFlow: MutableSharedFlow<List<Song>> =
        MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )

    override fun fetchSongs(
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean?
    ): Flow<List<Song>> = songsFlow.map { songs ->
        songs.sortSongs(
            sortSongsBy = sortSongsBy ?: DefaultPreferences.SORT_SONGS_BY,
            reverse = sortSongsInReverse ?: false
        )
    }

    /**
     * A test-only API to allow controlling the list of songs from tests.
     */
    fun sendSongs( songs: List<Song> ) {
        songsFlow.tryEmit( songs )
    }

}