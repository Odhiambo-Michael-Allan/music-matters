package com.odesa.musicMatters.core.data.repository

import com.odesa.musicMatters.core.data.database.model.SongAdditionalMetadata
import com.odesa.musicMatters.core.model.SongAdditionalMetadataInfo
import kotlinx.coroutines.flow.Flow

interface SongsAdditionalMetadataRepository {
    fun fetchAdditionalMetadataEntries(): Flow<List<SongAdditionalMetadata>>
    suspend fun fetchAdditionalMetadataForSongWithId( songId: String ): SongAdditionalMetadataInfo?
    suspend fun save( songAdditionalMetadata: SongAdditionalMetadata )
    suspend fun save( songAdditionalMetadata: List<SongAdditionalMetadata> )
    suspend fun deleteEntryWithId( id: String )
}