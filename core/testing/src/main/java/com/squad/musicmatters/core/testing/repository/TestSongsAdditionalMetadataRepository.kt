package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TestSongsAdditionalMetadataRepository : SongsAdditionalMetadataRepository {

    private val metadataFlow: MutableSharedFlow<List<SongAdditionalMetadataInfo>> =
        MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )

    override fun fetchAdditionalMetadataEntries(): Flow<List<SongAdditionalMetadataInfo>> =
        metadataFlow

    override suspend fun fetchAdditionalMetadataForSongWithId(
        songId: String
    ): SongAdditionalMetadataInfo? = metadataFlow.map { metadata ->
        metadata.find { it.songId == songId }
    }.first()

    override suspend fun save( songAdditionalMetadata: SongAdditionalMetadataInfo ) {
        val currentMetadata = metadataFlow.first().toMutableList()
        currentMetadata.add( songAdditionalMetadata )
        metadataFlow.tryEmit( currentMetadata )
    }

    override suspend fun save( songAdditionalMetadata: List<SongAdditionalMetadataInfo> ) {
        val currentMetadata = metadataFlow.first().toMutableList()
        currentMetadata.addAll( songAdditionalMetadata )
        metadataFlow.tryEmit( currentMetadata )
    }

    override suspend fun deleteEntryWithId( id: String ) {
        val currentMetadata = metadataFlow.first().toMutableList()
        currentMetadata.removeIf { it.songId == id }
        metadataFlow.tryEmit( currentMetadata )
    }

    fun sendMetadata( metadata: List<SongAdditionalMetadataInfo> ) {
        metadataFlow.tryEmit( metadata )
    }
}