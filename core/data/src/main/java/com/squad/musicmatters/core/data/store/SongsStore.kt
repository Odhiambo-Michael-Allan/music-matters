package com.squad.musicmatters.core.data.store

import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy

interface SongsStore {

    suspend fun fetchSongs(
        sortSongsBy: SortSongsBy? = null,
        sortSongsInReverse: Boolean = false,
    ): List<Song>

    suspend fun fetchLyricsFor( song: Song? ): List<Lyric>

    suspend fun searchSongsMatching(
        query: String,
        sortSongsBy: SortSongsBy? = null,
        sortSongsInReverse: Boolean = false
    ): List<Song>

    suspend fun searchSongsInAlbumMatching(
        query: String
    ): List<Song>

    suspend fun searchSongsByArtistMatching(
        query: String
    ): List<Song>

    fun registerListener( listener: MediaStoreListener )
    fun unregisterListener( listener: MediaStoreListener )
}