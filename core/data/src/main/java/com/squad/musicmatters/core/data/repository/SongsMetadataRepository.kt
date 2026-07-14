package com.squad.musicmatters.core.data.repository

import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.SortGenresBy
import kotlinx.coroutines.flow.Flow

interface SongsMetadataRepository {
    fun fetchMetadata(): Flow<List<SongMetadata>>
    suspend fun deleteEntryWithId( id: String )
}
