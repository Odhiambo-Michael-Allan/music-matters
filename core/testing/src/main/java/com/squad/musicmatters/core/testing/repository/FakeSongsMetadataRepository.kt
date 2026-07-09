package com.squad.musicmatters.core.testing.repository

import com.squad.musicmatters.core.data.repository.GenreResult
import com.squad.musicmatters.core.data.repository.MetadataResult
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.utils.sortGenres
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.SortGenresBy
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FakeSongsMetadataRepository : SongsMetadataRepository {

    private val metadataFlow: MutableSharedFlow<MetadataResult> =
        MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )

    override fun fetchMetadata(): Flow<List<SongMetadata>> =
        metadataFlow.map { result ->
            ( result as? MetadataResult.Success )?.metadata ?: emptyList()
        }

    override fun fetchGenres(
        sortGenresBy: SortGenresBy,
        reverse: Boolean
    ): Flow<GenreResult> = metadataFlow.map { result ->
        when ( result ) {
            MetadataResult.Loading -> GenreResult.Loading
            is MetadataResult.Success -> {
                val genres = result.metadata.groupBy { it.genre }.map { ( genre, metadataList ) ->
                    Genre(
                        name = genre,
                        numberOfTracks = metadataList.size
                    )
                }
                GenreResult.Success( genres.sortGenres( by = sortGenresBy, reverse = reverse ) )
            }
        }

    }

    override suspend fun deleteEntryWithId( id: String ) {
        ( metadataFlow.first() as? MetadataResult.Success )?.let { currentMetadata ->
            val metadata = currentMetadata.metadata.toMutableList()
            metadata.removeIf { it.songId == id }
            metadataFlow.tryEmit( MetadataResult.Success( metadata ) )
        }

    }

    fun sendMetadata( metadata: List<SongMetadata> ) {
        metadataFlow.tryEmit( MetadataResult.Success( metadata ) )
    }
}