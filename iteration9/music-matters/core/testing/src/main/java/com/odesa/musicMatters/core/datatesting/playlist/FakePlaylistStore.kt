package com.odesa.musicMatters.core.datatesting.playlist

import com.odesa.musicMatters.core.data.playlists.PlaylistStore
import com.odesa.musicMatters.core.model.PlaylistInfo

class FakePlaylistStore : PlaylistStore {

    private val mostPlayedSongsMap = mutableMapOf<String, Int>()
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

    private var customPlaylistInfos = mutableListOf<PlaylistInfo>()

    override suspend fun fetchAllPlaylists() = mutableListOf<PlaylistInfo>().apply {
        add( favoritePlaylistInfo )
        add( recentSongsPlaylistInfo )
        add( mostPlayedSongsPlaylistInfo )
        addAll( customPlaylistInfos )
    }

    override suspend fun fetchFavoritesPlaylist(): PlaylistInfo {
        return favoritePlaylistInfo
    }

    override suspend fun addSongIdToFavoritesPlaylist(songId: String ) {
        val currentSongIds = favoritePlaylistInfo.songIds.toMutableList()
        currentSongIds.add( songId )
        favoritePlaylistInfo = favoritePlaylistInfo.copy(
            songIds = currentSongIds
        )
    }

    override suspend fun removeSongIdFromFavoritesPlaylist(songId: String ) {
        val currentSongIds = favoritePlaylistInfo.songIds.toMutableList()
        currentSongIds.remove( songId )
        favoritePlaylistInfo = favoritePlaylistInfo.copy(
            songIds = currentSongIds
        )
    }

    override suspend fun fetchRecentlyPlayedSongsPlaylist(): PlaylistInfo {
        return recentSongsPlaylistInfo
    }

    override suspend fun addSongIdToRecentlyPlayedSongsPlaylist( songId: String ) {
        val currentSongIds = recentSongsPlaylistInfo.songIds.toMutableList()
        currentSongIds.remove( songId )
        currentSongIds.add( 0, songId )
        recentSongsPlaylistInfo = recentSongsPlaylistInfo.copy(
            songIds = currentSongIds
        )
    }

    override suspend fun fetchMostPlayedSongsPlaylist() = mostPlayedSongsPlaylistInfo

    override suspend fun addSongIdToMostPlayedSongsPlaylist(songId: String ) {
        val currentSongIds = mostPlayedSongsPlaylistInfo.songIds.toMutableList()
        currentSongIds.add( songId )
        if ( mostPlayedSongsMap.contains( songId ) ) mostPlayedSongsMap[ songId ] = mostPlayedSongsMap[ songId ]!!.plus( 1 )
        else mostPlayedSongsMap[ songId ] = 1
        mostPlayedSongsPlaylistInfo = mostPlayedSongsPlaylistInfo.copy(
            songIds = currentSongIds
        )
    }

    override suspend fun fetchEditablePlaylists() = customPlaylistInfos

    override suspend fun savePlaylist(playlistInfo: PlaylistInfo ) {
        println( "SAVING PLAYLIST WITH ID: ${playlistInfo.id} AND TITLE: ${playlistInfo.title}" )
        customPlaylistInfos.add( playlistInfo )
    }

    override suspend fun deletePlaylist(playlistInfo: PlaylistInfo ) {
        customPlaylistInfos.remove( playlistInfo )
    }

    override suspend fun addSongIdToPlaylist(songId: String, playlistInfo: PlaylistInfo ) {
        customPlaylistInfos.find { it.id == playlistInfo.id } ?. let {
            val currentSongIds = it.songIds.toMutableList()
            currentSongIds.add( songId )
            customPlaylistInfos.remove( it )
            customPlaylistInfos.add(
                it.copy(
                    songIds = currentSongIds
                )
            )
        }
    }

    override suspend fun renamePlaylist(playlistInfo: PlaylistInfo, newTitle: String ) {
        println( "RENAMING PLAYLIST TO $newTitle FROM: ${playlistInfo.title}" )
        customPlaylistInfos.find { it.id == playlistInfo.id }?.let {
            println( "RENAMING PLAYLIST TO $newTitle" )
            customPlaylistInfos.remove( it )
            customPlaylistInfos.add( it.copy( title = newTitle ) )
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
