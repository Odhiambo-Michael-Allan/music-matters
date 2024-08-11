package com.odesa.musicMatters.core.data.repository

import com.odesa.musicMatters.core.model.PlaylistInfo
import kotlinx.coroutines.flow.StateFlow

interface PlaylistRepository {

    val favoritesPlaylistInfo: StateFlow<PlaylistInfo>
    val recentlyPlayedSongsPlaylistInfo: StateFlow<PlaylistInfo>
    val mostPlayedSongsPlaylistInfo: StateFlow<PlaylistInfo>

    val playlists: StateFlow<List<PlaylistInfo>>
    val currentPlayingQueuePlaylistInfo: StateFlow<PlaylistInfo>

    fun isFavorite( songId: String ): Boolean
    suspend fun addToFavorites( songId: String )
    suspend fun removeFromFavorites( songId: String )

    suspend fun addToRecentlyPlayedSongsPlaylist( songId: String )
    suspend fun addToMostPlayedPlaylist( songId: String )
    suspend fun removeSongIdFromMostPlayedPlaylist( songId: String )

    suspend fun savePlaylist( playlistInfo: PlaylistInfo )
    suspend fun deletePlaylist( playlistInfo: PlaylistInfo )
    suspend fun addSongIdToPlaylist( songId: String, playlistId: String )

    suspend fun removeSongIdFromPlaylist( songId: String, playlistId: String )
    suspend fun renamePlaylist( playlistInfo: PlaylistInfo, newTitle: String )
    suspend fun saveCurrentlyPlayingQueue(songIds: List<String> )

    suspend fun clearCurrentPlayingQueuePlaylist()
}