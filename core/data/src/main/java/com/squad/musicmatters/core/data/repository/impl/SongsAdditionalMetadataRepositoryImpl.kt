package com.squad.musicmatters.core.data.repository.impl

import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import com.squad.musicmatters.core.database.dao.SongAdditionalMetadataDao
import com.squad.musicmatters.core.database.model.SongAdditionalMetadataEntity
import com.squad.musicmatters.core.database.model.asExternalModel
import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SongsAdditionalMetadataRepositoryImpl @Inject constructor(
    private val songAdditionalMetadataDao: SongAdditionalMetadataDao
) : SongsAdditionalMetadataRepository {

    override fun fetchAdditionalMetadataEntries(): Flow<List<SongAdditionalMetadataInfo>> =
        songAdditionalMetadataDao.observeEntries().map { entities -> entities.map { it.asExternalModel() } }

    override suspend fun fetchAdditionalMetadataForSongWithId( songId: String ) =
        songAdditionalMetadataDao
            .fetchAdditionalMetadataForSongWithId( songId )
            ?.asExternalModel()

    override suspend fun save( songAdditionalMetadata: SongAdditionalMetadataInfo ) {
        songAdditionalMetadataDao.insert( songAdditionalMetadata.asEntity() )
    }

    override suspend fun save( songAdditionalMetadata: List<SongAdditionalMetadataInfo> ) {
        songAdditionalMetadataDao.insertAll( songAdditionalMetadata.map { it.asEntity() } )
    }

    override suspend fun deleteEntryWithId( id: String ) {
        songAdditionalMetadataDao.deleteEntryWithId( id )
    }
}

private fun SongAdditionalMetadataInfo.asEntity() = SongAdditionalMetadataEntity(
    songId = songId,
    codec = codec,
    bitsPerSample = bitsPerSample.toLong(),
    bitrate = bitrate.toLong(),
    samplingRate = (samplingRate.toDouble() * 1000).toLong(),
    genre = genre,
)


