package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.model.SongMetadata
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FakeSongsMetadataRepository : SongsMetadataRepository {

    private val metadataFlow: MutableSharedFlow<List<SongMetadata>> =
        MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )

    override fun fetchMetadata(): Flow<List<SongMetadata>> =
        metadataFlow

    override suspend fun deleteEntryWithId( id: String ) {
        val currentMetadata = metadataFlow.first().toMutableList()
        currentMetadata.removeIf { it.songId == id }
        metadataFlow.tryEmit( currentMetadata )
    }

    fun sendMetadata( metadata: List<SongMetadata> ) {
        metadataFlow.tryEmit( metadata )
    }
}