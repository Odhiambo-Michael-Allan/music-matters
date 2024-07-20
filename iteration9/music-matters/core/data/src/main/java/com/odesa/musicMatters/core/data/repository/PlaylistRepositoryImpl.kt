package com.odesa.musicMatters.core.data.repository

import com.odesa.musicMatters.core.data.playlists.PlaylistStore
import com.odesa.musicMatters.core.model.PlaylistInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PlaylistRepositoryImpl(
    private val playlistStore: PlaylistStore,
    coroutineScope: CoroutineScope,
) : PlaylistRepository {

    private val _favoritesPlaylist = MutableStateFlow( EMPTY_PLAYLIST )
    override val favoritesPlaylistInfo = _favoritesPlaylist.asStateFlow()

    private val _recentlyPlayedSongsPlaylist = MutableStateFlow( EMPTY_PLAYLIST )
    override val recentlyPlayedSongsPlaylistInfo = _recentlyPlayedSongsPlaylist.asStateFlow()

    private val _mostPlayedSongsPlaylist = MutableStateFlow( EMPTY_PLAYLIST )
    override val mostPlayedSongsPlaylistInfo = _mostPlayedSongsPlaylist.asStateFlow()

    private val _playlists = MutableStateFlow( emptyList<PlaylistInfo>() )
    override val playlists = _playlists.asStateFlow()

    private val _currentPlayingQueuePlaylist = MutableStateFlow( EMPTY_PLAYLIST )
    override val currentPlayingQueuePlaylistInfo = _currentPlayingQueuePlaylist.asStateFlow()

    init {
        coroutineScope.launch {
            _playlists.value = playlistStore.fetchAllPlaylists()
            _favoritesPlaylist.value = playlistStore.fetchFavoritesPlaylist()
            _recentlyPlayedSongsPlaylist.value = playlistStore.fetchRecentlyPlayedSongsPlaylist()
            _mostPlayedSongsPlaylist.value = playlistStore.fetchMostPlayedSongsPlaylist()
            _currentPlayingQueuePlaylist.value = playlistStore.fetchCurrentPlayingQueue()
        }
    }

    override fun isFavorite( songId: String ) = favoritesPlaylistInfo.value.songIds.contains( songId )

    override suspend fun addToFavorites( songId: String ) {
        if ( isFavorite( songId ) ) removeSongIdFromPlaylist( songId, favoritesPlaylistInfo.value.id )
        else addSongIdToPlaylist( songId, favoritesPlaylistInfo.value.id )
    }

    override suspend fun removeFromFavorites( songId: String ) {
        removeSongIdFromPlaylist( songId, favoritesPlaylistInfo.value.id )
    }

    override suspend fun addToRecentlyPlayedSongsPlaylist( songId: String ) {
        addSongIdToPlaylist( songId, recentlyPlayedSongsPlaylistInfo.value.id )
    }

    override suspend fun addToMostPlayedPlaylist( songId: String ) {
        addSongIdToPlaylist( songId, mostPlayedSongsPlaylistInfo.value.id )
    }

    override suspend fun removeSongIdFromMostPlayedPlaylist( songId: String ) {
        removeSongIdFromPlaylist( songId, mostPlayedSongsPlaylistInfo.value.id )
    }

    override suspend fun savePlaylist( playlistInfo: PlaylistInfo ) {
        playlistStore.savePlaylist( playlistInfo )
        _playlists.value = playlistStore.fetchAllPlaylists()
    }

    override suspend fun deletePlaylist( playlistInfo: PlaylistInfo ) {
        playlistStore.deletePlaylist( playlistInfo )
        _playlists.value = playlistStore.fetchAllPlaylists()
    }

    override suspend fun addSongIdToPlaylist( songId: String, playlistId: String ) {
        _playlists.value.find { it.id == playlistId }?.let {
            playlistStore.addSongIdToPlaylist( songId, it )
            _playlists.value = playlistStore.fetchAllPlaylists()
            _favoritesPlaylist.value = playlistStore.fetchFavoritesPlaylist()
            _mostPlayedSongsPlaylist.value = playlistStore.fetchMostPlayedSongsPlaylist()
            _recentlyPlayedSongsPlaylist.value = playlistStore.fetchRecentlyPlayedSongsPlaylist()

        }
    }

    override suspend fun removeSongIdFromPlaylist( songId: String, playlistId: String ) {
        _playlists.value.find { it.id == playlistId }?.let {
            playlistStore.removeSongIdFromPlaylist( songId, it )
            _playlists.value = playlistStore.fetchAllPlaylists()
            _favoritesPlaylist.value = playlistStore.fetchFavoritesPlaylist()
            _mostPlayedSongsPlaylist.value = playlistStore.fetchMostPlayedSongsPlaylist()
            _recentlyPlayedSongsPlaylist.value = playlistStore.fetchRecentlyPlayedSongsPlaylist()
        }
    }

    override suspend fun renamePlaylist(playlistInfo: PlaylistInfo, newTitle: String ) {
        playlistStore.renamePlaylist( playlistInfo, newTitle )
        _playlists.value = playlistStore.fetchAllPlaylists()
    }

    override suspend fun saveCurrentlyPlayingQueue(songIds: List<String> ) {
        songIds.forEach { playlistStore.addSongIdToCurrentPlayingQueue( it ) }
        _currentPlayingQueuePlaylist.value = playlistStore.fetchCurrentPlayingQueue()
    }

    override suspend fun clearCurrentPlayingQueuePlaylist() {
        playlistStore.clearCurrentPlayingQueuePlaylist()
        _currentPlayingQueuePlaylist.value = playlistStore.fetchCurrentPlayingQueue()
    }

    companion object {

        @Volatile
        private var INSTANCE: PlaylistRepository? = null

        fun getInstance(
            playlistStore: PlaylistStore,
            coroutineScope: CoroutineScope
        ): PlaylistRepository {
            return INSTANCE ?: synchronized( this ) {
                PlaylistRepositoryImpl(
                    playlistStore = playlistStore,
                    coroutineScope = coroutineScope
                ).also { INSTANCE = it }
            }

        }
    }
}

private val EMPTY_PLAYLIST = PlaylistInfo(
    id = "",
    title = "",
    songIds = emptyList()
)
const val PLAYLIST_REPOSITORY_TAG = "PLAYLIST-REPOSITORY-TAG"