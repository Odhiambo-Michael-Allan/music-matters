package com.squad.musicmatters.core.data.repository.impl

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.database.dao.SongAdditionalMetadataDao
import com.squad.musicmatters.core.database.model.SongAdditionalMetadataEntity
import com.squad.musicmatters.core.database.model.asExternalModel
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class SongsAdditionalMetadataRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val songsRepository: SongsRepository,
    private val songAdditionalMetadataDao: SongAdditionalMetadataDao,
    @Dispatcher( MusicMattersDispatchers.IO ) dispatcher: CoroutineDispatcher,
) : SongsAdditionalMetadataRepository {

    private val scope = CoroutineScope( dispatcher + SupervisorJob() )

    init {
        scope.launch {
            songsRepository.fetchSongs().collect { songs ->
                val metadataRetriever = MediaMetadataRetriever()
                songs.forEach {
                    try {
                        val uri = it.mediaUri.toUri()
                        metadataRetriever.setDataSource( context, uri )
                        val bitrate = extractBitrateUsing( metadataRetriever )
                        val bitsPerSample = extractBitsPerSampleUsing( metadataRetriever )
                        val codec = extractCodecUsing( metadataRetriever )
                        val samplingRate = extractSamplingRateUsing( metadataRetriever )
                        val genre = extractGenreUsing( metadataRetriever )
                        Log.d( TAG, "SAVING METADATA FOR: ${it.title}" )
                        save(
                            SongAdditionalMetadata(
                                songId = it.id,
                                bitrate = ( bitrate / 1000 ),
                                bitsPerSample = bitsPerSample,
                                codec = codec,
                                samplingRate = samplingRate.toFloat(),
                                genre = genre
                            )
                        )
                    } catch ( e: Exception ) {
                        Log.e(
                            TAG,
                            "ERROR OCCURRED WHILE FETCHING ADDITIONAL METADATA FOR: ${it.title}"
                        )
                    }
                }
                metadataRetriever.release()
            }
        }
    }

    override fun fetchAdditionalMetadataEntries(): Flow<List<SongAdditionalMetadata>> =
        songAdditionalMetadataDao.fetchEntries().map { entities -> entities.map { it.asExternalModel() } }

    override suspend fun fetchAdditionalMetadataForSongWithId( songId: String ) =
        songAdditionalMetadataDao
            .fetchAdditionalMetadataForSongWithId( songId )
            ?.asExternalModel()

    override suspend fun save( songAdditionalMetadata: SongAdditionalMetadata ) {
        songAdditionalMetadataDao.insert( songAdditionalMetadata.asEntity() )
    }

    override suspend fun save( songAdditionalMetadata: List<SongAdditionalMetadata> ) {
        songAdditionalMetadataDao.insertAll( songAdditionalMetadata.map { it.asEntity() } )
    }

    override suspend fun deleteEntryWithId( id: String ) {
        songAdditionalMetadataDao.deleteEntryWithId( id )
    }
}

private fun extractBitrateUsing( mediaMetadataRetriever: MediaMetadataRetriever ) =
    mediaMetadataRetriever.runCatching {
        extractMetadata( MediaMetadataRetriever.METADATA_KEY_BITRATE )?.toLong()
    }.getOrNull() ?: 0L

private fun extractBitsPerSampleUsing( mediaMetadataRetriever: MediaMetadataRetriever ) =
    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
        mediaMetadataRetriever.runCatching {
            extractMetadata( MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE )?.toLong()
        }.getOrNull() ?: 0L
    } else 0L

private fun extractCodecUsing( mediaMetadataRetriever: MediaMetadataRetriever ) =
    mediaMetadataRetriever.runCatching {
        extractMetadata( MediaMetadataRetriever.METADATA_KEY_MIMETYPE )
    }.getOrNull() ?: UNKNOWN_STRING_VALUE

private fun extractSamplingRateUsing( mediaMetadataRetriever: MediaMetadataRetriever ) =
    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
        mediaMetadataRetriever.runCatching {
            extractMetadata( MediaMetadataRetriever.METADATA_KEY_SAMPLERATE )?.toLong()
        }.getOrNull() ?: 0L
    } else 0L

private fun extractGenreUsing( mediaMetadataRetriever: MediaMetadataRetriever ): String {
    val genre = mediaMetadataRetriever.runCatching {
        extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
    }.getOrNull() ?: return UNKNOWN_STRING_VALUE

    val genreSplitRegex = Regex(
        """\s*(?:,|/|;|\\|&|and|//)\s*""",
        RegexOption.IGNORE_CASE
    )
    val genreList = genre.split( genreSplitRegex )

    // Return the first non-empty trimmed genre, or the unknown value if empty
    return genreList.firstOrNull { it.isNotBlank() }?.trim() ?: UNKNOWN_STRING_VALUE
}

private const val UNKNOWN_STRING_VALUE = "<unknown>"
private const val TAG = "ADD-METADATA-REPO"

private fun SongAdditionalMetadata.asEntity() = SongAdditionalMetadataEntity(
    songId = songId,
    codec = codec,
    bitsPerSample = bitsPerSample.toLong(),
    bitrate = bitrate.toLong(),
    samplingRate = (samplingRate.toDouble() * 1000).toLong(),
    genre = genre,
)




