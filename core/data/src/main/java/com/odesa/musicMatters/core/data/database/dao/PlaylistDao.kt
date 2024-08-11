package com.odesa.musicMatters.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.odesa.musicMatters.core.data.database.model.Playlist
import com.odesa.musicMatters.core.data.database.model.PlaylistWithEntries

@Dao
abstract class PlaylistDao : BaseDao<Playlist> {
    @Transaction
    @Query( "SELECT * FROM playlists" )
    abstract suspend fun fetchPlaylists(): List<PlaylistWithEntries>

    @Transaction
    @Query( "SELECT * FROM playlists WHERE id = :id" )
    abstract suspend fun fetchPlaylistWithId( id: String ): PlaylistWithEntries

    @Query( "SELECT EXISTS( SELECT 1 FROM playlists WHERE id = :playlistId )" )
    abstract suspend fun playlistExists( playlistId: String ): Boolean

}



