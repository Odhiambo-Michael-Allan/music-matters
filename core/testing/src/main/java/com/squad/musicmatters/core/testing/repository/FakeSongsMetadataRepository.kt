package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.model.SongMetadata
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first

class FakeSongsMetadataRepository : SongsMetadataRepository {

    private val metadataFlow: MutableSharedFlow<List<SongMetadata>> =
        MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )

    override fun fetchMetadata(): Flow<List<SongMetadata>> = metadataFlow


    override suspend fun deleteEntryWithId( id: String ) {
        metadataFlow.first().let { currentMetadata ->
            val metadata = currentMetadata.toMutableList()
            metadata.removeIf { it.songId == id }
            metadataFlow.tryEmit( metadata )
        }

    }

    fun sendMetadata( metadata: List<SongMetadata> ) {
        metadataFlow.tryEmit( metadata )
    }
}