package com.odesa.musicMatters.core.datatesting.playlist

import com.odesa.musicMatters.core.data.playlists.PlaylistStore
import com.odesa.musicMatters.core.model.PlaylistInfo

class FakePlaylistStore : PlaylistStore {

    private val FAVORITES_PLAYLIST_ID = "--FAVORITES-PLAYLIST-ID--"
    private val RECENTLY_PLAYED_SONGS_PLAYLIST_ID = "--RECENTLY-PLAYED-SONGS-PLAYLIST-ID--"
    private val MOST_PLAYED_SONGS_PLAYLIST_ID = "--MOST-PLAYED-SONGS-PLAYLIST-ID--"
    private val CURRENT_QUEUE_PLAYLIST_ID = "--CURRENT-QUEUE-PLAYLIST-ID--"

    private var favoritePlaylistInfo = PlaylistInfo(
        id = FAVORITES_PLAYLIST_ID,
        title = "Favorites",
        songIds = emptyList(),
    )

    private var recentSongsPlaylistInfo = PlaylistInfo(
        id = RECENTLY_PLAYED_SONGS_PLAYLIST_ID,
        title = "Recently Played Songs",
        songIds = emptyList()
    )

    private var mostPlayedSongsPlaylistInfo = PlaylistInfo(
        id = MOST_PLAYED_SONGS_PLAYLIST_ID,
        title = "Most played songs",
        songIds = emptyList()
    )

    private var currentQueuePlaylistInfo = PlaylistInfo(
        id = CURRENT_QUEUE_PLAYLIST_ID,
        title = "",
        songIds = emptyList()
    )

    private var playlists = mutableListOf(
        favoritePlaylistInfo,
        recentSongsPlaylistInfo,
        mostPlayedSongsPlaylistInfo
    )

    override suspend fun fetchAllPlaylists() = playlists

    override suspend fun fetchFavoritesPlaylist(): PlaylistInfo {
        return playlists.find { it.id == FAVORITES_PLAYLIST_ID }!!
    }

    override suspend fun addSongIdToFavoritesPlaylist( songId: String ) {
        addSongIdToPlaylist( songId, favoritePlaylistInfo )
    }

    override suspend fun removeSongIdFromFavoritesPlaylist( songId: String ) {
        removeSongIdFromPlaylist( songId, favoritePlaylistInfo )
    }

    override suspend fun fetchRecentlyPlayedSongsPlaylist(): PlaylistInfo {
        return playlists.find { it.id == RECENTLY_PLAYED_SONGS_PLAYLIST_ID }!!
    }

    override suspend fun addSongIdToRecentlyPlayedSongsPlaylist( songId: String ) {
        removeSongIdFromPlaylist( songId, recentSongsPlaylistInfo )
        addSongIdToPlaylist( songId, recentSongsPlaylistInfo )
    }

    override suspend fun fetchMostPlayedSongsPlaylist(): PlaylistInfo {
        return playlists.find { it.id == MOST_PLAYED_SONGS_PLAYLIST_ID }!!
    }

    override suspend fun addSongIdToMostPlayedSongsPlaylist( songId: String ) {
        addSongIdToPlaylist( songId, mostPlayedSongsPlaylistInfo )
    }

    override suspend fun removeSongIdFromMostPlayedSongsPlaylist( songId: String ) {
        removeSongIdFromPlaylist( songId, mostPlayedSongsPlaylistInfo )
    }

    override suspend fun fetchEditablePlaylists() =
        playlists.filter {
            it.id != FAVORITES_PLAYLIST_ID && it.id != RECENTLY_PLAYED_SONGS_PLAYLIST_ID
                    && it.id != CURRENT_QUEUE_PLAYLIST_ID
        }

    override suspend fun savePlaylist( playlistInfo: PlaylistInfo ) {
        println( "SAVING PLAYLIST WITH ID: ${playlistInfo.id} AND TITLE: ${playlistInfo.title}" )
        playlists.add( playlistInfo )
    }

    override suspend fun deletePlaylist( playlistInfo: PlaylistInfo ) {
        playlists.remove( playlistInfo )
    }

    override suspend fun addSongIdToPlaylist( songId: String, playlistInfo: PlaylistInfo ) {
        playlists.find { it.id == playlistInfo.id }?.let {
            playlists.remove( it )
            val currentSongIds = it.songIds.toMutableList()
            currentSongIds.add( songId )
            playlists.add(
                it.copy( songIds = currentSongIds )
            )
        }
    }

    override suspend fun removeSongIdFromPlaylist( songId: String, playlistInfo: PlaylistInfo ) {
        playlists.find { it.id == playlistInfo.id }?.let {
            playlists.remove( it )
            val currentSongIds = it.songIds.toMutableList()
            currentSongIds.remove( songId )
            playlists.add(
                it.copy( songIds = currentSongIds )
            )
        }
    }

    override suspend fun renamePlaylist(playlistInfo: PlaylistInfo, newTitle: String ) {
        println( "RENAMING PLAYLIST TO $newTitle FROM: ${playlistInfo.title}" )
        playlists.find { it.id == playlistInfo.id }?.let {
            println( "RENAMING PLAYLIST TO $newTitle" )
            playlists.remove( it )
            playlists.add( it.copy( title = newTitle ) )
        }
    }

    override suspend fun addSongIdToCurrentPlayingQueue( songId: String ) {
        val currentSongIds = currentQueuePlaylistInfo.songIds.toMutableList()
        currentSongIds.add( songId )
        currentQueuePlaylistInfo = currentQueuePlaylistInfo.copy(
            songIds = currentSongIds
        )
    }

    override suspend fun fetchCurrentPlayingQueue() = currentQueuePlaylistInfo

    override suspend fun clearCurrentPlayingQueuePlaylist() {
        currentQueuePlaylistInfo = currentQueuePlaylistInfo.copy(
            songIds = emptyList()
        )
    }
}
