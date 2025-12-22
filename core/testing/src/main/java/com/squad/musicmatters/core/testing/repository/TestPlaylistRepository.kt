package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.repository.impl.FAVORITES_PLAYLIST_ID
import com.squad.musicmatters.core.model.PlaylistInfo
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TestPlaylistRepository : PlaylistRepository {

    private val playlistsFlow: MutableSharedFlow<List<PlaylistInfo>> =
        MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )

    override fun fetchFavorites(): Flow<PlaylistInfo?> =
        playlistsFlow.map { playlists ->
            playlists.find { it.id == FAVORITES_PLAYLIST_ID }
        }

    override fun fetchPlaylists(): Flow<List<PlaylistInfo>> = playlistsFlow

    override fun fetchPlaylistWithId( id: String ): Flow<PlaylistInfo?> =
        playlistsFlow.map { playlists ->
            playlists.find { it.id == id }
        }

    override fun isFavorite( songId: String ): Flow<Boolean> =
        fetchFavorites().map { playlist ->
            playlist?.songIds?.contains( songId ) ?: false
        }

    override suspend fun addToFavorites( songId: String ) {
        fetchFavorites().first()?.let { favorites ->
            val currentSongIds = favorites.songIds.toMutableList()
            val currentPlaylists = playlistsFlow.first().toMutableList()
            currentSongIds.add( songId )
            currentPlaylists.removeIf { it.id == FAVORITES_PLAYLIST_ID }
            currentPlaylists.add(
                favorites.copy(
                    songIds = currentSongIds.toSet()
                )
            )
            playlistsFlow.tryEmit( currentPlaylists )
        } ?: run {
            val currentPlaylists = playlistsFlow.first().toMutableList()
            currentPlaylists.add(
                PlaylistInfo(
                    id = FAVORITES_PLAYLIST_ID,
                    title = "",
                    songIds = setOf( songId )
                )
            )
            playlistsFlow.tryEmit( currentPlaylists )
        }
    }

    override suspend fun removeFromFavorites( songId: String ) {
        fetchFavorites().first()?.let { favorites ->
            val currentSongIds = favorites.songIds.toMutableList()
            val currentPlaylists = playlistsFlow.first().toMutableList()
            currentSongIds.remove( songId )
            currentPlaylists.removeIf { it.id == FAVORITES_PLAYLIST_ID }
            currentPlaylists.add(
                favorites.copy(
                    songIds = currentSongIds.toSet()
                )
            )
            playlistsFlow.tryEmit( currentPlaylists )
        }
    }

    override suspend fun savePlaylist( playlistInfo: PlaylistInfo ) {
        val currentPlaylists = playlistsFlow.first().toMutableList()
        currentPlaylists.add( playlistInfo )
        playlistsFlow.tryEmit( currentPlaylists )
    }

    override suspend fun deletePlaylist( playlistInfo: PlaylistInfo ) {
        val currentPlaylists = playlistsFlow.first().toMutableList()
        currentPlaylists.removeIf { it.id == playlistInfo.id }
        playlistsFlow.tryEmit( currentPlaylists )
    }

    override suspend fun addSongIdToPlaylist( songId: String, playlistId: String ) {
        fetchPlaylists().first().find { it.id == playlistId }?.let { playlist ->
            val currentSongIds = playlist.songIds.toMutableList()
            val currentPlaylists = playlistsFlow.first().toMutableList()
            currentSongIds.add( songId )
            currentPlaylists.removeIf { it.id == playlistId }
            currentPlaylists.add(
                playlist.copy(
                    songIds = currentSongIds.toSet()
                )
            )
            playlistsFlow.tryEmit( currentPlaylists )
        }
    }

    override suspend fun removeSongIdFromPlaylist(
        songId: String,
        playlistId: String
    ) {
        fetchPlaylists().first().find { it.id == playlistId }?.let { playlist ->
            val currentSongIds = playlist.songIds.toMutableList()
            val currentPlaylists = playlistsFlow.first().toMutableList()
            currentSongIds.remove( songId )
            currentPlaylists.removeIf { it.id == playlistId }
            currentPlaylists.add(
                playlist.copy(
                    songIds = currentSongIds.toSet()
                )
            )
            playlistsFlow.tryEmit( currentPlaylists )
        }
    }

    override suspend fun renamePlaylist(
        playlistInfo: PlaylistInfo,
        newTitle: String
    ) {
        fetchPlaylists().first().find { it.id == playlistInfo.id }?.let { playlist ->
            val currentPlaylists = playlistsFlow.first().toMutableList()
            currentPlaylists.removeIf { it.id == playlistInfo.id }
            currentPlaylists.add(
                playlist.copy(
                    title = newTitle
                )
            )
            playlistsFlow.tryEmit( currentPlaylists )
        }
    }

    fun sendPlaylists( playlists: List<PlaylistInfo> ) {
        playlistsFlow.tryEmit( playlists )
    }

}