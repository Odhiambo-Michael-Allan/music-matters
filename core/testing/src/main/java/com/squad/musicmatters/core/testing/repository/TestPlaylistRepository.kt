package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.repository.impl.FAVORITES_PLAYLIST_ID
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

class TestPlaylistRepository : PlaylistRepository {

    private val playlistsFlow: MutableSharedFlow<List<Playlist>> =
        MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )

    override fun fetchFavorites(): Flow<Playlist?> =
        playlistsFlow.map { playlists ->
            playlists.find { it.id == FAVORITES_PLAYLIST_ID }
        }

    override fun fetchPlaylists(): Flow<List<Playlist>> = playlistsFlow

    override fun fetchPlaylistWithId( id: String ): Flow<Playlist?> =
        playlistsFlow.map { playlists ->
            playlists.find { it.id == id }
        }

    override fun isFavorite( songId: String ): Flow<Boolean> =
        fetchFavorites().map { playlist ->
            playlist?.songIds?.contains( songId ) ?: false
        }

    override suspend fun addToFavorites( song: Song ) {
        fetchFavorites().first()?.let { favorites ->
            val currentSongIds = favorites.songIds.toMutableList()
            val currentPlaylists = playlistsFlow.first().toMutableList()
            currentSongIds.add( song.id )
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
                Playlist(
                    id = FAVORITES_PLAYLIST_ID,
                    title = "",
                    songIds = setOf( song.id )
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

    override suspend fun savePlaylist( id: String, playlistName: String, songsInPlaylist: List<Song> ) {
        val currentPlaylists = playlistsFlow.first().toMutableList()
        currentPlaylists.add(
            Playlist(
                id = id,
                title = playlistName,
                songIds = songsInPlaylist.map { it.id }.toSet()
            )
        )
        playlistsFlow.tryEmit( currentPlaylists )
    }

    override suspend fun deletePlaylist(playlist: Playlist ) {
        val currentPlaylists = playlistsFlow.first().toMutableList()
        currentPlaylists.removeIf { it.id == playlist.id }
        playlistsFlow.tryEmit( currentPlaylists )
    }

    override suspend fun addSongToPlaylist( song: Song, playlistId: String ) {
        fetchPlaylists().first().find { it.id == playlistId }?.let { playlist ->
            val currentSongIds = playlist.songIds.toMutableList()
            val currentPlaylists = playlistsFlow.first().toMutableList()
            currentSongIds.add( song.id )
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
        playlist: Playlist,
        newTitle: String
    ) {
        fetchPlaylists().first().find { it.id == playlist.id }?.let { playlist ->
            val currentPlaylists = playlistsFlow.first().toMutableList()
            currentPlaylists.removeIf { it.id == playlist.id }
            currentPlaylists.add(
                playlist.copy(
                    title = newTitle
                )
            )
            playlistsFlow.tryEmit( currentPlaylists )
        }
    }

    fun sendPlaylists( playlists: List<Playlist> ) {
        playlistsFlow.tryEmit( playlists )
    }

}