package com.squad.musicmatters.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.squad.musicmatters.core.database.model.SongAdditionalMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SongAdditionalMetadataDao : BaseDao<SongAdditionalMetadataEntity> {

    @Query( "SELECT * FROM songs_additional_metadata WHERE id = :songId" )
    abstract suspend fun fetchAdditionalMetadataForSongWithId( songId: String ): SongAdditionalMetadataEntity?

    @Query( "SELECT * FROM songs_additional_metadata" )
    abstract fun fetchEntries(): Flow<List<SongAdditionalMetadataEntity>>

    @Query( "DELETE FROM songs_additional_metadata WHERE id = :songId" )
    abstract fun deleteEntryWithId( songId: String )
}