package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    fun fetchFavorites(): Flow<Playlist?>
    fun fetchPlaylists(): Flow<List<Playlist>>
    fun fetchPlaylistWithId( id: String ): Flow<Playlist?>

    fun isFavorite( songId: String ): Flow<Boolean>
    suspend fun addToFavorites( song: Song )
    suspend fun removeFromFavorites( songId: String )

    suspend fun savePlaylist( id: String, playlistName: String, songsInPlaylist: List<Song> )
    suspend fun deletePlaylist(playlist: Playlist )
    suspend fun addSongToPlaylist( song: Song, playlistId: String )

    suspend fun removeSongIdFromPlaylist( songId: String, playlistId: String )
    suspend fun renamePlaylist( playlist: Playlist, newTitle: String )

}