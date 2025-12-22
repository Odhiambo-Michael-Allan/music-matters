package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.database.dao.PlaylistDao
import com.squad.musicmatters.core.database.dao.PlaylistEntryDao
import com.squad.musicmatters.core.database.dao.SongPlayCountEntryDao
import com.squad.musicmatters.core.database.model.PlaylistEntity
import com.squad.musicmatters.core.database.model.PlaylistEntryEntity
import com.squad.musicmatters.core.database.model.PopulatedPlaylistEntity
import com.squad.musicmatters.core.database.model.SongPlayCountEntity
import com.squad.musicmatters.core.model.PlaylistInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val FAVORITES_PLAYLIST_ID = "--MUSIC-MATTERS-FAVORITES-PLAYLIST-ID--"

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val playlistEntryDao: PlaylistEntryDao,
) : PlaylistRepository {

    override fun fetchFavorites() = fetchPlaylistWithId( FAVORITES_PLAYLIST_ID )

    override fun fetchPlaylistWithId( id: String ): Flow<PlaylistInfo?> =
        playlistDao
            .fetchPlaylistWithId( id )
            .map {
                it?.asPlaylistInfo()
            }

    override fun fetchPlaylists(): Flow<List<PlaylistInfo>> = playlistDao
        .fetchPlaylists()
        .map { playlists -> playlists.map { it.asPlaylistInfo() } }

    override fun isFavorite( songId: String ) =
        playlistDao.fetchPlaylistWithId(
            id = FAVORITES_PLAYLIST_ID
        ).map { favoritesPlaylist ->
            favoritesPlaylist?.let {
                favoritesPlaylist.entries.map { it.songId }.contains( songId )
            } ?: false
        }

    override suspend fun addToFavorites( songId: String ) {
        val playlistEntity = PlaylistEntryEntity(
            playlistId = FAVORITES_PLAYLIST_ID,
            songId = songId
        )
        playlistDao
            .fetchPlaylistWithId( id = FAVORITES_PLAYLIST_ID )
            .first()?.let {
                playlistEntryDao.insert( playlistEntity )
            } ?: savePlaylist(
                PlaylistInfo(
                    id = FAVORITES_PLAYLIST_ID,
                    title = "",
                    songIds = setOf( songId )
                )
            )

    }

    override suspend fun removeFromFavorites( songId: String ) {
        removeSongIdFromPlaylist(
            songId = songId,
            playlistId = FAVORITES_PLAYLIST_ID,
        )
    }

    override suspend fun savePlaylist( playlistInfo: PlaylistInfo ) {
        playlistDao.insert( playlistInfo.asEntity() )
        playlistInfo.songIds.forEach {
            playlistEntryDao.insert(
                PlaylistEntryEntity(
                    playlistId = playlistInfo.id,
                    songId = it
                )
            )
        }
    }

    override suspend fun deletePlaylist( playlistInfo: PlaylistInfo ) {
        playlistDao.delete( playlistInfo.asEntity() )
    }

    override suspend fun addSongIdToPlaylist( songId: String, playlistId: String ) {
        playlistEntryDao.insert(
            PlaylistEntryEntity(
                playlistId = playlistId,
                songId = songId
            )
        )
    }

    override suspend fun removeSongIdFromPlaylist( songId: String, playlistId: String ) {
        playlistEntryDao.deleteEntry( songId = songId, playlistId = playlistId )
    }

    override suspend fun renamePlaylist( playlistInfo: PlaylistInfo, newTitle: String ) {
        if ( playlistInfo.id == FAVORITES_PLAYLIST_ID ) return
        playlistDao.update(
            playlistInfo.asEntity().copy(
                title = newTitle
            )
        )
    }

    companion object {

        @Volatile
        private var INSTANCE: PlaylistRepository? = null

        fun getInstance(
            playlistDao: PlaylistDao,
            playlistEntryDao: PlaylistEntryDao,
        ): PlaylistRepository {
            return INSTANCE ?: synchronized( this ) {
                PlaylistRepositoryImpl(
                    playlistDao = playlistDao,
                    playlistEntryDao = playlistEntryDao,
                ).also { INSTANCE = it }
            }

        }
    }
}

private fun PlaylistInfo.asEntity() = PlaylistEntity(
    id = id,
    title = title
)

private fun PopulatedPlaylistEntity.asPlaylistInfo() = PlaylistInfo(
    id = playlistEntity.id,
    title = playlistEntity.title,
    songIds = entries.map { it.songId }.toSet()
)

const val PLAYLIST_REPOSITORY_TAG = "PLAYLIST-REPOSITORY-TAG"