package com.odesa.musicMatters.core.datatesting.repository

import com.odesa.musicMatters.core.data.database.model.SongAdditionalMetadata
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSongsAdditionalMetadataRepository : SongsAdditionalMetadataRepository {

    private val _songsAdditionalMetadataList = MutableStateFlow( emptyList<SongAdditionalMetadata>() )
    private val songsAdditionalMetadataList = _songsAdditionalMetadataList.asStateFlow()

    override fun fetchAdditionalMetadataEntries() = songsAdditionalMetadataList

    override suspend fun fetchAdditionalMetadataForSongWithId( songId: String ) = null

    override suspend fun save( songAdditionalMetadata: SongAdditionalMetadata ) {
        TODO("Not yet implemented")
    }
}