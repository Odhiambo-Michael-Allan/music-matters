package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.SortGenresBy
import kotlinx.coroutines.flow.Flow

interface SongsMetadataRepository {
    fun fetchMetadata(): Flow<List<SongMetadata>>
    fun fetchGenres(
        sortGenresBy: SortGenresBy,
        reverse: Boolean,
    ): Flow<GenreResult>
    suspend fun deleteEntryWithId( id: String )
}

sealed interface MetadataResult {
    data object Loading: MetadataResult
    data class Success( val metadata: List<SongMetadata> ): MetadataResult
}

sealed interface GenreResult {
    data object Loading: GenreResult
    data class Success( val genres: List<Genre> ): GenreResult
}