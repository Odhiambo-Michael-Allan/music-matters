package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.SongAdditionalMetadata
import kotlinx.coroutines.flow.Flow

interface SongsAdditionalMetadataRepository {
    fun fetchAdditionalMetadataEntries(): Flow<List<SongAdditionalMetadata>>
    suspend fun fetchAdditionalMetadataForSongWithId( songId: String ): SongAdditionalMetadata?
    suspend fun save( songAdditionalMetadata: SongAdditionalMetadata )
    suspend fun save( songAdditionalMetadata: List<SongAdditionalMetadata> )
    suspend fun deleteEntryWithId( id: String )
}