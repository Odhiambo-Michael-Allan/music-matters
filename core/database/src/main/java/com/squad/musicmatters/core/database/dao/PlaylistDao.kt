package com.squad.musicmatters.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.squad.musicmatters.core.database.model.PlaylistEntity
import com.squad.musicmatters.core.database.model.PopulatedPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PlaylistDao : BaseDao<PlaylistEntity> {
    @Transaction
    @Query( "SELECT * FROM playlists" )
    abstract fun fetchPlaylists(): Flow<List<PopulatedPlaylistEntity>>

    @Transaction
    @Query( "SELECT * FROM playlists WHERE id = :id" )
    abstract fun fetchPlaylistWithId( id: String ): Flow<PopulatedPlaylistEntity?>

    @Query( "SELECT EXISTS( SELECT 1 FROM playlists WHERE id = :playlistId )" )
    abstract suspend fun playlistExists( playlistId: String ): Boolean

}



