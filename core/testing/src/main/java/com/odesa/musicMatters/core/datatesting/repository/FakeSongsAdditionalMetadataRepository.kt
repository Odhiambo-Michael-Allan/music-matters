package com.odesa.musicMatters.core.datatesting.repository

import com.odesa.musicMatters.core.data.database.model.SongAdditionalMetadata
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.datatesting.metadata.testSongsAdditionalMetadata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSongsAdditionalMetadataRepository : SongsAdditionalMetadataRepository {

    private val _songsAdditionalMetadataList = MutableStateFlow( testSongsAdditionalMetadata )
    private val songsAdditionalMetadataList = _songsAdditionalMetadataList.asStateFlow()

    override fun fetchAdditionalMetadataEntries() = songsAdditionalMetadataList

    override suspend fun fetchAdditionalMetadataForSongWithId( songId: String ) = null

    override suspend fun save( songAdditionalMetadata: SongAdditionalMetadata ) {
        val newList = _songsAdditionalMetadataList.value.toMutableList()
        newList.add( songAdditionalMetadata )
        _songsAdditionalMetadataList.value = newList
    }

    override suspend fun save( songAdditionalMetadata: List<SongAdditionalMetadata> ) {
        val newList = _songsAdditionalMetadataList.value.toMutableList()
        newList.addAll( songAdditionalMetadata )
        _songsAdditionalMetadataList.value = newList
    }

    override suspend fun deleteEntryWithId( id: String ) {
        val newList = _songsAdditionalMetadataList.value.toMutableList()
        newList.removeIf { it.songId == id }
        _songsAdditionalMetadataList.value = newList
    }
}