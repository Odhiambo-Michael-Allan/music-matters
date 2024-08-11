package com.odesa.musicMatters.core.data.playlists

import com.odesa.musicMatters.core.model.PlaylistInfo

interface PlaylistStore {

    suspend fun fetchAllPlaylists(): List<PlaylistInfo>
    suspend fun fetchFavoritesPlaylist(): PlaylistInfo
    suspend fun addSongIdToFavoritesPlaylist(songId: String )

    suspend fun removeSongIdFromFavoritesPlaylist(songId: String )
    suspend fun fetchRecentlyPlayedSongsPlaylist(): PlaylistInfo
    suspend fun addSongIdToRecentlyPlayedSongsPlaylist( songId: String )

    suspend fun fetchMostPlayedSongsPlaylist(): PlaylistInfo
    suspend fun addSongIdToMostPlayedSongsPlaylist( songId: String )
    suspend fun removeSongIdFromMostPlayedSongsPlaylist( songId: String )

    suspend fun fetchEditablePlaylists(): List<PlaylistInfo>
    suspend fun savePlaylist( playlistInfo: PlaylistInfo )
    suspend fun deletePlaylist( playlistInfo: PlaylistInfo )

    suspend fun addSongIdToPlaylist( songId: String, playlistInfo: PlaylistInfo )
    suspend fun removeSongIdFromPlaylist( songId: String, playlistInfo: PlaylistInfo )
    suspend fun renamePlaylist( playlistInfo: PlaylistInfo, newTitle: String )

    suspend fun addSongIdToCurrentPlayingQueue( songId: String )
    suspend fun fetchCurrentPlayingQueue(): PlaylistInfo
    suspend fun clearCurrentPlayingQueuePlaylist()

}