package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.PlaylistInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PlaylistRepository {

    fun fetchFavorites(): Flow<PlaylistInfo?>
    fun fetchPlaylists(): Flow<List<PlaylistInfo>>
    fun fetchPlaylistWithId( id: String ): Flow<PlaylistInfo?>

    fun isFavorite( songId: String ): Flow<Boolean>
    suspend fun addToFavorites( songId: String )
    suspend fun removeFromFavorites( songId: String )

    suspend fun savePlaylist( playlistInfo: PlaylistInfo )
    suspend fun deletePlaylist( playlistInfo: PlaylistInfo )
    suspend fun addSongIdToPlaylist( songId: String, playlistId: String )

    suspend fun removeSongIdFromPlaylist( songId: String, playlistId: String )
    suspend fun renamePlaylist( playlistInfo: PlaylistInfo, newTitle: String )

}