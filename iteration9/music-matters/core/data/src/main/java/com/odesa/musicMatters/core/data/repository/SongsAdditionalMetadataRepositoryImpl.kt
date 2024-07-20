package com.odesa.musicMatters.core.data.repository

import com.odesa.musicMatters.core.data.database.dao.SongAdditionalMetadataDao
import com.odesa.musicMatters.core.data.database.model.SongAdditionalMetadata
import com.odesa.musicMatters.core.model.SongAdditionalMetadataInfo
import kotlinx.coroutines.flow.Flow

class SongsAdditionalMetadataRepositoryImpl(
    private val songAdditionalMetadataDao: SongAdditionalMetadataDao
) : SongsAdditionalMetadataRepository {

    override fun fetchAdditionalMetadataEntries(): Flow<List<SongAdditionalMetadata>> =
        songAdditionalMetadataDao.observeEntries()

    override suspend fun fetchAdditionalMetadataForSongWithId( songId: String ) =
        songAdditionalMetadataDao
            .fetchAdditionalMetadataForSongWithId( songId )
            .asDomain()

    override suspend fun save( songAdditionalMetadata: SongAdditionalMetadata ) {
        songAdditionalMetadataDao.insert( songAdditionalMetadata )
    }

    override suspend fun save( songAdditionalMetadata: List<SongAdditionalMetadata> ) {
        songAdditionalMetadataDao.insertAll( songAdditionalMetadata )
    }

    override suspend fun deleteEntryWithId( id: String ) {
        songAdditionalMetadataDao.deleteEntryWithId( id )
    }
}

fun SongAdditionalMetadata?.asDomain(): SongAdditionalMetadataInfo? =
    if ( this == null ) null
    else
        SongAdditionalMetadataInfo(
            id = songId,
            codec = codec,
            bitsPerSample = bitsPerSample.toString(),
            bitrate = bitrate.toString(),
            samplingRate = samplingRate.toFloat().div( 1000 ).toString(),
            genre = genre
        )
