package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
import kotlinx.coroutines.flow.Flow

interface SongsAdditionalMetadataRepository {
    fun fetchAdditionalMetadataEntries(): Flow<List<SongAdditionalMetadataInfo>>
    suspend fun fetchAdditionalMetadataForSongWithId( songId: String ): SongAdditionalMetadataInfo?
    suspend fun save( songAdditionalMetadata: SongAdditionalMetadataInfo )
    suspend fun save( songAdditionalMetadata: List<SongAdditionalMetadataInfo> )
    suspend fun deleteEntryWithId( id: String )
}