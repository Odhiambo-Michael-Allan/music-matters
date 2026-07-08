package com.squad.musicmatters.core.data.repository.impl

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.core.net.toUri
import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.database.dao.SongAdditionalMetadataDao
import com.squad.musicmatters.core.database.model.SongAdditionalMetadataEntity
import com.squad.musicmatters.core.database.model.asExternalModel
import com.squad.musicmatters.core.model.SongMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SongsMetadataRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val songsRepository: SongsRepository,
    private val songAdditionalMetadataDao: SongAdditionalMetadataDao,
    @Dispatcher( MusicMattersDispatchers.IO ) dispatcher: CoroutineDispatcher,
) : SongsMetadataRepository {

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
                        Timber.tag( TAG ).d( "SAVING METADATA FOR: ${it.title}" )
                        save(
                            SongMetadata(
                                songId = it.id,
                                bitrate = ( bitrate / 1000 ),
                                bitsPerSample = bitsPerSample,
                                codec = codec,
                                samplingRate = samplingRate.toFloat(),
                                genre = genre
                            )
                        )
                    } catch ( e: Exception ) {
                        Timber.tag( TAG )
                            .e( "ERROR OCCURRED WHILE FETCHING ADDITIONAL METADATA FOR: ${it.title}" )
                    }
                }
                metadataRetriever.release()
            }
        }
    }

    override fun fetchMetadata(): Flow<List<SongMetadata>> =
        songAdditionalMetadataDao.fetchEntries().map { entities -> entities.map { it.asExternalModel() } }

    override suspend fun fetchMetadataForSongWithId(songId: String ) =
        songAdditionalMetadataDao
            .fetchAdditionalMetadataForSongWithId( songId )
            ?.asExternalModel()

    override suspend fun save(songMetadata: SongMetadata ) {
        songAdditionalMetadataDao.insert( songMetadata.asEntity() )
    }

    override suspend fun save(songMetadata: List<SongMetadata> ) {
        songAdditionalMetadataDao.insertAll( songMetadata.map { it.asEntity() } )
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
    }.getOrNull() ?: ""

private fun extractSamplingRateUsing( mediaMetadataRetriever: MediaMetadataRetriever ) =
    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
        mediaMetadataRetriever.runCatching {
            extractMetadata( MediaMetadataRetriever.METADATA_KEY_SAMPLERATE )?.toLong()
        }.getOrNull() ?: 0L
    } else 0L

private fun extractGenreUsing( mediaMetadataRetriever: MediaMetadataRetriever ): String {
    val genre = mediaMetadataRetriever.runCatching {
        extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
    }.getOrNull() ?: return ""

    val genreSplitRegex = Regex(
        """\s*(?:,|/|;|\\|&|and|//)\s*""",
        RegexOption.IGNORE_CASE
    )
    val genreList = genre.split( genreSplitRegex )

    // Return the first non-empty trimmed genre, or the unknown value if empty
    return genreList.firstOrNull { it.isNotBlank() }?.trim() ?: ""
}

private const val TAG = "ADD-METADATA-REPO"

private fun SongMetadata.asEntity() = SongAdditionalMetadataEntity(
    songId = songId,
    codec = codec,
    bitsPerSample = bitsPerSample,
    bitrate = bitrate,
    samplingRate = (samplingRate.toDouble() * 1000).toLong(),
    genre = genre,
)




