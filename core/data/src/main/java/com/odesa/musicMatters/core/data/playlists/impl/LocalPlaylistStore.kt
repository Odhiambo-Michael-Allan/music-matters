package com.odesa.musicMatters.core.data.playlists.impl

import com.odesa.musicMatters.core.data.database.CURRENT_PLAYING_QUEUE_PLAYLIST_ID
import com.odesa.musicMatters.core.data.database.FAVORITES_PLAYLIST_ID
import com.odesa.musicMatters.core.data.database.RECENTLY_PLAYED_SONGS_PLAYLIST_ID
import com.odesa.musicMatters.core.data.database.dao.PlaylistDao
import com.odesa.musicMatters.core.data.database.dao.PlaylistEntryDao
import com.odesa.musicMatters.core.data.database.dao.SongPlayCountEntryDao
import com.odesa.musicMatters.core.data.database.model.Playlist
import com.odesa.musicMatters.core.data.database.model.PlaylistEntry
import com.odesa.musicMatters.core.data.database.model.PlaylistWithEntries
import com.odesa.musicMatters.core.data.database.model.SongPlayCountEntry
import com.odesa.musicMatters.core.data.playlists.PlaylistStore
import com.odesa.musicMatters.core.model.PlaylistInfo
import timber.log.Timber

class LocalPlaylistStore(
    private val playlistDao: PlaylistDao,
    private val playlistEntryDao: PlaylistEntryDao,
    private val songPlayCountEntryDao: SongPlayCountEntryDao,
    private val currentTimeInMillis: () -> Long
) : PlaylistStore {

    override suspend fun fetchAllPlaylists() = playlistDao
        .fetchPlaylists()
        .filter { it.playlist.id != CURRENT_PLAYING_QUEUE_PLAYLIST_ID }
        .asDomain()
        .toMutableList()
        .apply {
            add( fetchMostPlayedSongsPlaylist() )
        }

    override suspend fun fetchFavoritesPlaylist() = playlistDao
        .fetchPlaylistWithId( FAVORITES_PLAYLIST_ID )
        .asDomain()

    override suspend fun addSongIdToFavoritesPlaylist( songId: String ) {
        playlistEntryDao.insert(
            PlaylistEntry(
                playlistId = FAVORITES_PLAYLIST_ID,
                songId = songId
            )
        )
    }

    override suspend fun removeSongIdFromFavoritesPlaylist( songId: String ) {
        removeSongIdFromPlaylist( songId, FAVORITES_PLAYLIST_ID )
    }

    override suspend fun fetchRecentlyPlayedSongsPlaylist(): PlaylistInfo {
        val entity = playlistDao
            .fetchPlaylistWithId( RECENTLY_PLAYED_SONGS_PLAYLIST_ID )
        val sortedSongIds = entity.entries.sortedByDescending { it.dateAdded }.map { it.songId }
        return entity
            .asDomain()
            .copy( songIds = sortedSongIds )
    }

    override suspend fun addSongIdToRecentlyPlayedSongsPlaylist( songId: String ) {
        playlistEntryDao.deleteEntry( RECENTLY_PLAYED_SONGS_PLAYLIST_ID, songId )
        val currentTime = currentTimeInMillis()
        Timber.tag( "-LOCAL-PLAYLIST-STORE-TAG-" ).d( "ADDING RECENTLY PLAYED SONG ID AT TIME: $currentTime" )
        playlistEntryDao.insert(
            PlaylistEntry(
                playlistId = RECENTLY_PLAYED_SONGS_PLAYLIST_ID,
                songId = songId,
                dateAdded = currentTimeInMillis()
            )
        )
    }

    override suspend fun fetchMostPlayedSongsPlaylist() = songPlayCountEntryDao
        .fetchEntries()
        .asPlaylist()

    override suspend fun addSongIdToMostPlayedSongsPlaylist( songId: String ) {
        songPlayCountEntryDao.getPlayCountBySongId( songId )?.let {
            songPlayCountEntryDao.incrementPlayCount( songId )
        } ?: run {
            Timber.tag( TAG ).d( "ADDING SONG ID: $songId TO MOST PLAYED SONGS PLAYLIST" )
            songPlayCountEntryDao.insert(
                SongPlayCountEntry(
                    songId = songId
                )
            )
        }
    }

    override suspend fun removeSongIdFromMostPlayedSongsPlaylist( songId: String ) {
        songPlayCountEntryDao.getPlayCountBySongId( songId )?.let {
            songPlayCountEntryDao.deleteEntryWithSongId( songId )
        }
    }

    override suspend fun fetchEditablePlaylists() = playlistDao
        .fetchPlaylists()
        .asDomain()
        .filter {
            it.id != FAVORITES_PLAYLIST_ID && it.id != RECENTLY_PLAYED_SONGS_PLAYLIST_ID
                    && it.id != CURRENT_PLAYING_QUEUE_PLAYLIST_ID
        }

    override suspend fun savePlaylist( playlistInfo: PlaylistInfo ) {
        playlistDao.insert( playlistInfo.asEntity() )
        playlistInfo.songIds.forEach {
            playlistEntryDao.insert(
                PlaylistEntry(
                    playlistId = playlistInfo.id,
                    songId = it
                )
            )
        }
    }

    override suspend fun deletePlaylist( playlistInfo: PlaylistInfo ) {
        playlistDao.delete( playlistInfo.asEntity() )
    }

    override suspend fun addSongIdToPlaylist( songId: String, playlistInfo: PlaylistInfo ) {
        playlistEntryDao.insert(
            PlaylistEntry(
                playlistId = playlistInfo.id,
                songId = songId
            )
        )
    }

    override suspend fun removeSongIdFromPlaylist( songId: String, playlistInfo: PlaylistInfo ) {
        removeSongIdFromPlaylist( songId, playlistInfo.id )
    }

    override suspend fun renamePlaylist( playlistInfo: PlaylistInfo, newTitle: String ) {
        playlistDao.update(
            playlistInfo.asEntity().copy(
                title = newTitle
            )
        )
    }

    override suspend fun addSongIdToCurrentPlayingQueue( songId: String ) {
        playlistEntryDao.insert(
            PlaylistEntry(
                playlistId = CURRENT_PLAYING_QUEUE_PLAYLIST_ID,
                songId = songId
            )
        )
    }

    override suspend fun fetchCurrentPlayingQueue() = playlistDao
        .fetchPlaylistWithId( CURRENT_PLAYING_QUEUE_PLAYLIST_ID )
        .asDomain()

    override suspend fun clearCurrentPlayingQueuePlaylist() {
        playlistEntryDao.removeEntriesForPlaylistWithId( CURRENT_PLAYING_QUEUE_PLAYLIST_ID )
    }

    private suspend fun removeSongIdFromPlaylist( songId: String, playlistId: String ) {
        playlistEntryDao.fetchEntriesForPlaylistWithId( playlistId ).find {
            it.songId == songId
        }?.let {
            playlistEntryDao.deleteEntry( playlistId, songId )
        }
    }
}

private fun List<PlaylistWithEntries>.asDomain() = map { it.asDomain() }

private fun PlaylistWithEntries.asDomain() =
    PlaylistInfo(
        id = playlist.id,
        title = playlist.title,
        songIds = entries.map { it.songId }
    )

private fun List<SongPlayCountEntry>.asPlaylist() =
    PlaylistInfo(
        id = MOST_PLAYED_SONGS_PLAYLIST_ID,
        title = "Most Played Songs",
        songIds = sortedByDescending { it.numberOfTimesPlayed }.map { it.songId }
    )

private fun PlaylistInfo.asEntity() =
    Playlist(
        id = id,
        title = title
    )

private const val MOST_PLAYED_SONGS_PLAYLIST_ID = "--MUSIC-MATTERS-MOST-PLAYED-SONGS-PLAYLIST-ID"
private const val TAG = "--LOCAL PLAYLIST STORE TAG--"