package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import kotlinx.coroutines.flow.Flow

interface SongsRepository {
    fun fetchSongs(
        sortSongsBy: SortSongsBy? = null,
        sortSongsInReverse: Boolean = false
    ): Flow<List<Song>>

    fun searchSongsMatching(
        query: String,
        sortSongsBy: SortSongsBy,
        sortSongsInReverse: Boolean = false,
    ): Flow<List<Song>>

    fun fetchLyricsForSong( song: Song? ): Flow<List<Lyric>>

    fun searchSongsInAlbumMatching(
        query: String
    ): Flow<List<Song>>

    fun searchSongsByArtistMatching(
        query: String
    ): Flow<List<Song>>
}
