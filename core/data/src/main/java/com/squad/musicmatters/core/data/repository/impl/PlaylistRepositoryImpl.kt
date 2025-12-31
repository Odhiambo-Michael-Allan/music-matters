package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.database.dao.PlaylistDao
import com.squad.musicmatters.core.database.dao.PlaylistEntryDao
import com.squad.musicmatters.core.database.model.PlaylistEntity
import com.squad.musicmatters.core.database.model.PlaylistEntryEntity
import com.squad.musicmatters.core.database.model.PopulatedPlaylistEntity
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import kotlin.uuid.Uuid

const val FAVORITES_PLAYLIST_ID = "--MUSIC-MATTERS-FAVORITES-PLAYLIST-ID--"

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val playlistEntryDao: PlaylistEntryDao,
) : PlaylistRepository {

    override fun fetchFavorites() = fetchPlaylistWithId( FAVORITES_PLAYLIST_ID )

    override fun fetchPlaylistWithId( id: String ): Flow<Playlist?> =
        playlistDao
            .fetchPlaylistWithId( id )
            .map {
                it?.asPlaylistInfo()
            }

    override fun fetchPlaylists(): Flow<List<Playlist>> = playlistDao
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

    override suspend fun addToFavorites( song: Song ) {
        val playlistEntity = PlaylistEntryEntity(
            playlistId = FAVORITES_PLAYLIST_ID,
            songId = song.id,
            artworkUri = song.artworkUri,
        )
        playlistDao
            .fetchPlaylistWithId( id = FAVORITES_PLAYLIST_ID )
            .first()?.let {
                playlistEntryDao.insert( playlistEntity )
            } ?: savePlaylist(
                id = FAVORITES_PLAYLIST_ID,
                playlistName = "",
                songsInPlaylist = listOf( song )
            )

    }

    override suspend fun removeFromFavorites( songId: String ) {
        removeSongIdFromPlaylist(
            songId = songId,
            playlistId = FAVORITES_PLAYLIST_ID,
        )
    }

    override suspend fun savePlaylist( id: String, playlistName: String, songsInPlaylist: List<Song> ) {
        val playlist = Playlist(
            id = id,
            title = playlistName,
            songIds = songsInPlaylist.map { it.id }.toSet()
        )
        playlistDao.insert( playlist.asEntity() )
        songsInPlaylist.forEach {
            playlistEntryDao.insert(
                PlaylistEntryEntity(
                    playlistId = playlist.id,
                    artworkUri = it.artworkUri,
                    songId = it.id
                )
            )
        }
    }

    override suspend fun deletePlaylist( playlist: Playlist ) {
        playlistDao.delete( playlist.asEntity() )
    }

    override suspend fun addSongToPlaylist( song: Song, playlistId: String ) {
        playlistEntryDao.insert(
            PlaylistEntryEntity(
                playlistId = playlistId,
                songId = song.id,
                artworkUri = song.artworkUri
            )
        )
    }

    override suspend fun removeSongIdFromPlaylist( songId: String, playlistId: String ) {
        playlistEntryDao.deleteEntry( songId = songId, playlistId = playlistId )
    }

    override suspend fun renamePlaylist( playlist: Playlist, newTitle: String ) {
        if ( playlist.id == FAVORITES_PLAYLIST_ID ) return
        playlistDao.update(
            playlist.asEntity().copy(
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

private fun Playlist.asEntity() = PlaylistEntity(
    id = id,
    title = title
)

private fun PopulatedPlaylistEntity.asPlaylistInfo() = Playlist(
    id = playlistEntity.id,
    title = playlistEntity.title,
    artworkUri = entries.firstOrNull { it.artworkUri != null }?.artworkUri,
    songIds = entries.map { it.songId }.toSet()
)

const val PLAYLIST_REPOSITORY_TAG = "PLAYLIST-REPOSITORY-TAG"