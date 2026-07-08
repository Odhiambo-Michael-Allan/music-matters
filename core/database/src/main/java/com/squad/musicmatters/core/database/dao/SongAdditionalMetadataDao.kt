package com.squad.musicmatters.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.squad.musicmatters.core.database.model.SongMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SongAdditionalMetadataDao : BaseDao<SongMetadataEntity> {

    @Query( "SELECT * FROM songs_additional_metadata WHERE id = :songId" )
    abstract suspend fun fetchAdditionalMetadataForSongWithId( songId: String ): SongMetadataEntity?

    @Query( "SELECT * FROM songs_additional_metadata" )
    abstract fun fetchEntries(): Flow<List<SongMetadataEntity>>

    @Query( "DELETE FROM songs_additional_metadata WHERE id = :songId" )
    abstract fun deleteEntryWithId( songId: String )
}