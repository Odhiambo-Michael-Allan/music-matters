package com.odesa.musicMatters.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.odesa.musicMatters.core.data.database.model.PlaylistEntry

@Dao
abstract class PlaylistEntryDao : BaseDao<PlaylistEntry> {

    @Query( "SELECT * FROM playlist_entries WHERE playlist_id = :id" )
    abstract suspend fun fetchEntriesForPlaylistWithId( id: String ): List<PlaylistEntry>

    @Query( "DELETE FROM playlist_entries WHERE playlist_id = :playlistId AND song_id = :songId" )
    abstract suspend fun deleteEntry( playlistId: String, songId: String )

    @Query( "DELETE FROM playlist_entries WHERE playlist_id = :playlistId" )
    abstract suspend fun removeEntriesForPlaylistWithId( playlistId: String )
}