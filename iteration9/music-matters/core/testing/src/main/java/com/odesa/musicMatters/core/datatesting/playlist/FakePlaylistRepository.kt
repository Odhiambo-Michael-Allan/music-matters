package com.odesa.musicMatters.core.datatesting.playlist

import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.model.PlaylistInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class FakePlaylistRepository : PlaylistRepository {

    private val _favoritePlaylistInfo = MutableStateFlow(
        PlaylistInfo(
            id = UUID.randomUUID().toString(),
            title = "Favorites",
            songIds = emptyList(),
        )
    )
    override val favoritesPlaylistInfo = _favoritePlaylistInfo.asStateFlow()

    private val _recentlyPlayedSongsPlaylistInfo = MutableStateFlow(
        PlaylistInfo(
            id = UUID.randomUUID().toString(),
            title = "Recently Played Songs",
            songIds = emptyList()
        )
    )
    override val recentlyPlayedSongsPlaylistInfo = _recentlyPlayedSongsPlaylistInfo.asStateFlow()

    private val _mostPlayedSongsPlaylistInfo = MutableStateFlow(
        PlaylistInfo(
            id = UUID.randomUUID().toString(),
            title = "Most Played Songs",
            songIds = emptyList()
        )
    )
    override val mostPlayedSongsPlaylistInfo = _mostPlayedSongsPlaylistInfo.asStateFlow()

    private val _playlists = MutableStateFlow(
        listOf(
            _favoritePlaylistInfo.value,
            _recentlyPlayedSongsPlaylistInfo.value,
            _mostPlayedSongsPlaylistInfo.value
        )
    )
    override val playlists = _playlists.asStateFlow()

    private val _currentPlayingQueuePlaylistInfo = MutableStateFlow(
        PlaylistInfo(
            id = UUID.randomUUID().toString(),
            title = "",
            songIds = emptyList()
        )
    )
    override val currentPlayingQueuePlaylistInfo = _currentPlayingQueuePlaylistInfo.asStateFlow()

    override fun isFavorite( songId: String ): Boolean {
        return _favoritePlaylistInfo.value.songIds.contains( songId )
    }

    override suspend fun addToFavorites( songId: String ) {
        val currentFavoritesIds = _favoritePlaylistInfo.value.songIds.toMutableList()
        if ( isFavorite( songId ) ) currentFavoritesIds.remove( songId )
        else currentFavoritesIds.add( songId )
        _favoritePlaylistInfo.value = _favoritePlaylistInfo.value.copy(
            songIds = currentFavoritesIds
        )
        val updatedListOfPlaylists = _playlists.value.filter { it.id != _favoritePlaylistInfo.value.id }.toMutableList()
        updatedListOfPlaylists.add( _favoritePlaylistInfo.value )
        _playlists.value = updatedListOfPlaylists
    }

    override suspend fun removeFromFavorites( songId: String ) {
        val currentFavoritesIds = _favoritePlaylistInfo.value.songIds.toMutableList()
        currentFavoritesIds.remove( songId )
        _favoritePlaylistInfo.value = _favoritePlaylistInfo.value.copy(
            songIds = currentFavoritesIds
        )
    }

    override suspend fun addToRecentlyPlayedSongsPlaylist( songId: String ) {
        val currentSongIds = _recentlyPlayedSongsPlaylistInfo.value.songIds.toMutableList()
        currentSongIds.remove( songId )
        currentSongIds.add( 0, songId )
        _recentlyPlayedSongsPlaylistInfo.value = _recentlyPlayedSongsPlaylistInfo.value.copy(
            songIds = currentSongIds
        )
    }

    override suspend fun addToMostPlayedPlaylist( songId: String ) {
        val songIdsInMostPlayedSongsPlaylist = _mostPlayedSongsPlaylistInfo.value.songIds.toMutableList()
        songIdsInMostPlayedSongsPlaylist.add( songId )
        _mostPlayedSongsPlaylistInfo.value = _mostPlayedSongsPlaylistInfo.value.copy(
            songIds = songIdsInMostPlayedSongsPlaylist
        )
    }

    override suspend fun removeSongIdFromMostPlayedPlaylist( songId: String ) {
        val songIdsInMostPlayedSongsPlaylist = _mostPlayedSongsPlaylistInfo.value.songIds.toMutableList()
        songIdsInMostPlayedSongsPlaylist.remove( songId )
        _mostPlayedSongsPlaylistInfo.value = _mostPlayedSongsPlaylistInfo.value.copy(
            songIds = songIdsInMostPlayedSongsPlaylist
        )
    }

    override suspend fun savePlaylist( playlistInfo: PlaylistInfo ) {
        val mutablePlaylist = _playlists.value.toMutableList()
        mutablePlaylist.add( playlistInfo )
        _playlists.value = mutablePlaylist
    }

    override suspend fun deletePlaylist( playlistInfo: PlaylistInfo ) {
        _playlists.value = _playlists.value.filter { it.id != playlistInfo.id }
    }

    override suspend fun addSongIdToPlaylist( songId: String, playlistId: String ) {
        _playlists.value.find { it.id == playlistId }?.let {
            val currentPlaylists = _playlists.value.toMutableList()
            currentPlaylists.remove( it )
            val songIds = it.songIds.toMutableList()
            songIds.add( songId )
            val modifiedPlaylist = it.copy(
                songIds = songIds
            )
            currentPlaylists.add( modifiedPlaylist )
            _playlists.value = currentPlaylists
        }
    }

    override suspend fun removeSongIdFromPlaylist( songId: String, playlistId: String) {
        _playlists.value.find { it.id == playlistId }?.let {
            val currentPlaylists = _playlists.value.toMutableList()
            currentPlaylists.remove( it )
            val songIds = it.songIds.toMutableList()
            songIds.remove( songId )
            val modifiedPlaylist = it.copy(
                songIds = songIds
            )
            currentPlaylists.add( modifiedPlaylist )
            _playlists.value = currentPlaylists
        }
    }

    override suspend fun renamePlaylist( playlistInfo: PlaylistInfo, newTitle: String ) {
        _playlists.value.find { it.id == playlistInfo.id }?.let {
            val currentPlaylists = _playlists.value.toMutableList()
            val renamedPlaylist = it.copy( title = newTitle )
            currentPlaylists.remove( it )
            currentPlaylists.add( renamedPlaylist )
            _playlists.value = currentPlaylists
        }
    }

    override suspend fun saveCurrentlyPlayingQueue(songIds: List<String> ) {
        val currentSongsIds = _currentPlayingQueuePlaylistInfo.value.songIds.toMutableList()
        songIds.forEach { currentSongsIds.add( it ) }
        _currentPlayingQueuePlaylistInfo.value = _currentPlayingQueuePlaylistInfo.value.copy(
            songIds = currentSongsIds
        )
    }

    override suspend fun clearCurrentPlayingQueuePlaylist() {
        _currentPlayingQueuePlaylistInfo.value = _currentPlayingQueuePlaylistInfo.value.copy(
            songIds = emptyList()
        )
    }
}