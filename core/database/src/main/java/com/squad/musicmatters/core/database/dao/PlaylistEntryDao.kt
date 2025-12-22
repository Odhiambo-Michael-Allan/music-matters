package com.squad.musicmatters.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.squad.musicmatters.core.database.model.PlaylistEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlaylistEntryDao : BaseDao<PlaylistEntryEntity> {

    @Query( "SELECT * FROM playlist_entries WHERE playlist_id = :id" )
    abstract fun fetchEntriesForPlaylistWithId( id: String ): Flow<List<PlaylistEntryEntity>>

    @Query( "DELETE FROM playlist_entries WHERE playlist_id = :playlistId AND song_id = :songId" )
    abstract suspend fun deleteEntry( playlistId: String, songId: String )

    @Query("DELETE FROM playlist_entries WHERE playlist_id = :id" )
    abstract suspend fun removeEntriesForPlaylistWithId( id: String )
}