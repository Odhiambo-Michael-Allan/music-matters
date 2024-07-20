package com.odesa.musicMatters.core.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.odesa.musicMatters.core.data.database.model.SongAdditionalMetadata
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SongAdditionalMetadataDao : BaseDao<SongAdditionalMetadata> {

    @Query( "SELECT * FROM songs_additional_metadata WHERE id = :songId" )
    abstract suspend fun fetchAdditionalMetadataForSongWithId(songId: String ): SongAdditionalMetadata?

    @Query( "SELECT * FROM songs_additional_metadata" )
    abstract fun observeEntries(): Flow<List<SongAdditionalMetadata>>

    @Query( "DELETE FROM songs_additional_metadata WHERE id = :songId" )
    abstract fun deleteEntryWithId( songId: String )
}