package com.odesa.musicMatters.core.data.playlists.impl

import com.odesa.musicMatters.core.data.playlists.PlaylistRepository
import com.odesa.musicMatters.core.data.playlists.PlaylistStore
import com.odesa.musicMatters.core.model.PlaylistInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber


class PlaylistRepositoryImpl(
    private val playlistStore: PlaylistStore,
    private val coroutineDispatcher: CoroutineDispatcher,
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

    private val coroutineScope = CoroutineScope( coroutineDispatcher )

    init {
        coroutineScope.launch {
            _favoritesPlaylist.value = playlistStore.fetchFavoritesPlaylist()
            _recentlyPlayedSongsPlaylist.value = playlistStore.fetchRecentlyPlayedSongsPlaylist()
            _mostPlayedSongsPlaylist.value = playlistStore.fetchMostPlayedSongsPlaylist()
            _playlists.value = playlistStore.fetchAllPlaylists()
            _currentPlayingQueuePlaylist.value = playlistStore.fetchCurrentPlayingQueue()
        }
    }

    override fun isFavorite( songId: String ) = favoritesPlaylistInfo.value.songIds.contains( songId )

    override suspend fun addToFavorites( songId: String ) {
        if ( isFavorite( songId ) ) playlistStore.removeSongIdFromFavoritesPlaylist( songId )
        else playlistStore.addSongIdToFavoritesPlaylist( songId )
        _favoritesPlaylist.value = playlistStore.fetchFavoritesPlaylist()
        _playlists.value = playlistStore.fetchAllPlaylists()
    }

    override suspend fun removeFromFavorites( songId: String ) {
        playlistStore.removeSongIdFromFavoritesPlaylist( songId )
        _favoritesPlaylist.value = playlistStore.fetchFavoritesPlaylist()
        _playlists.value = playlistStore.fetchAllPlaylists()
    }

    override suspend fun addToRecentlyPlayedSongsPlaylist( songId: String ) {
        Timber.tag( PLAYLIST_REPOSITORY_TAG ).d( "ADDING SONG TO RECENTLY PLAYED PLAYLIST. ID: $songId" )
        playlistStore.addSongIdToRecentlyPlayedSongsPlaylist( songId )
        val newSongIds = playlistStore.fetchRecentlyPlayedSongsPlaylist().songIds
        _recentlyPlayedSongsPlaylist.value = _recentlyPlayedSongsPlaylist.value.copy(
            songIds = newSongIds
        )
        _playlists.value = playlistStore.fetchAllPlaylists()
    }

    override suspend fun addToMostPlayedPlaylist( songId: String ) {
        Timber.tag( PLAYLIST_REPOSITORY_TAG ).d( "ADDING SONG TO MOST PLAYED PLAYLIST. ID: $songId" )
        playlistStore.addSongIdToMostPlayedSongsPlaylist( songId )
        _playlists.value = playlistStore.fetchAllPlaylists()
        _mostPlayedSongsPlaylist.value = playlistStore.fetchMostPlayedSongsPlaylist()
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
            if ( playlistId == _favoritesPlaylist.value.id )
                _favoritesPlaylist.value = playlistStore.fetchFavoritesPlaylist()
        }
    }

    override suspend fun renamePlaylist(playlistInfo: PlaylistInfo, newTitle: String ) {
        playlistStore.renamePlaylist( playlistInfo, newTitle )
        _playlists.value = playlistStore.fetchAllPlaylists()
    }

    override suspend fun saveCurrentQueue( songIds: List<String> ) {
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
            coroutineDispatcher: CoroutineDispatcher
        ): PlaylistRepository {
            return INSTANCE ?: synchronized( this ) {
                PlaylistRepositoryImpl(
                    playlistStore = playlistStore,
                    coroutineDispatcher = coroutineDispatcher
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