package com.squad.musicmatters.core.data.songs

import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy

interface SongsStore {
    /**
     * Executes the query and returns the current list of songs sorted according to the provided
     * criteria.
     */
    suspend fun fetchSongs(
        sortSongsBy: SortSongsBy? = null,
        sortSongsInReverse: Boolean? = null,
    ): List<Song>

    suspend fun fetchLyricsFor( song: Song? ): List<Lyric>

    suspend fun searchSongsMatching(
        query: String,
        sortSongsBy: SortSongsBy?,
        sortSongsInReverse: Boolean?
    ): List<Song>

    fun registerListener( listener: SongsStoreListener )
    fun unregisterListener( listener: SongsStoreListener )
}