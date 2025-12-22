package com.squad.musicmatters.core.media.media.library

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import com.squad.musicmatters.core.media.media.extensions.UNKNOWN_LONG_VALUE
import com.squad.musicmatters.core.media.media.extensions.UNKNOWN_STRING_VALUE
import com.squad.musicmatters.core.media.media.extensions.from
import com.squad.musicmatters.core.media.media.extensions.genreTagSeparators
import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Source of [MediaMetadataCompat] objects stored locally on the user's device.
 */
class LocalMusicSource(
    private val context: Context,
    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
) : AbstractMusicSource() {

    private var musicCatalog: List<MediaItem> = emptyList()

    init {
        Timber.tag( TAG ).d( "INITIALIZING LOCAL MUSIC SOURCE" )
        state = STATE_INITIALIZING
    }

    override fun iterator() = musicCatalog.iterator()

    override suspend fun load() {
        updateCatalog()?.let {
            musicCatalog = it
            state = STATE_INITIALIZED
            Timber.tag( TAG ).d( "MUSIC CATALOG INITIALIZED. STATE IS INITIALIZED" )
            fetchMediaItemsAdditionalMetadata()
        } ?: run {
            musicCatalog = emptyList()
            state = STATE_ERROR
            Timber.tag(TAG).d( "STATE IS ERROR.." )
        }
    }

    override fun delete( mediaItemId: String ) {
        musicCatalog = musicCatalog.filter { it.mediaId != mediaItemId }
    }

    private suspend fun updateCatalog(): List<MediaItem>? {
        return withContext( Dispatchers.IO ) {
            Timber.tag( TAG ).d( "READING MEDIA ITEMS FROM STORAGE" )
            val mediaItemList = mutableListOf<MediaItem>()
            try {
                context.contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Audio.Media.IS_MUSIC + " = 1",
                    null,
                    null
                )?.use {
                    while ( it.moveToNext() ) {
                        kotlin.runCatching {
                            MediaItem.Builder().from( it )
                        }.getOrNull() ?.also { mediaItemBuilder ->
                            mediaItemList.add( mediaItemBuilder.build() )
                        }
                    }
                }
            } catch ( exception: Exception ) {
                Timber.tag(TAG).e( exception,
                    "Error occurred while updating music catalog" )
                return@withContext null
            }
            Timber.tag(TAG).d( "NUMBER OF TRACKS RETRIEVED FROM STORAGE ${mediaItemList.size}" )
            mediaItemList
        }
    }

    private suspend fun fetchMediaItemsAdditionalMetadata() {
        withContext( Dispatchers.IO ) {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            val additionalMetadataList = mutableListOf<SongAdditionalMetadataInfo>()
            musicCatalog.forEach {
                try {
                    val uri = it.localConfiguration?.uri ?: Uri.EMPTY
//                Timber.tag(TAG).d( "FETCHING ADDITIONAL METADATA FOR SONG" +
//                        " WITH URI: $uri AND TITLE: ${it.mediaMetadata.title}" )
                    mediaMetadataRetriever.setDataSource( context, uri )
                    val bitrate = extractBitrateUsing( mediaMetadataRetriever )
//                Timber.tag( TAG ).d( "Bitrate: $bitrate" )
                    val bitsPerSample = extractBitsPerSampleUsing( mediaMetadataRetriever )
//                Timber.tag( TAG ).d( "Bits Per Sample: $bitsPerSample" )
                    val codec = extractCodecUsing( mediaMetadataRetriever )
//                Timber.tag( TAG ).d( "Codec: $codec" )
                    val samplingRate = extractSamplingRateUsing( mediaMetadataRetriever )
//                Timber.tag( TAG ).d( "Sampling Rate: $samplingRate" )
                    val genre = extractGenreUsing( mediaMetadataRetriever )
//                Timber.tag( TAG ).d( "Genre: $genre" )

                    additionalMetadataList.add(
                        SongAdditionalMetadataInfo(
                            songId = it.mediaId,
                            bitrate = (bitrate / 1000),
                            bitsPerSample = bitsPerSample,
                            codec = codec,
                            samplingRate = samplingRate.toFloat(),
                            genre = genre
                        )
                    )
                } catch ( e: Exception ) {
                    Timber.tag( TAG ).d( "ERROR OCCURRED WHILE FETCHING ADDITIONAL METADATA FOR: ${it.mediaMetadata.title}" )
                }
            }
            songsAdditionalMetadataRepository.save( additionalMetadataList )
            mediaMetadataRetriever.release()
        }
    }

    private fun extractBitrateUsing( mediaMetadataRetriever: MediaMetadataRetriever ) =
        mediaMetadataRetriever.runCatching {
            extractMetadata( MediaMetadataRetriever.METADATA_KEY_BITRATE )?.toLong()
        }.getOrNull() ?: UNKNOWN_LONG_VALUE

    private fun extractBitsPerSampleUsing( mediaMetadataRetriever: MediaMetadataRetriever ) =
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
            mediaMetadataRetriever.runCatching {
                extractMetadata( MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE )?.toLong()
            }.getOrNull() ?: UNKNOWN_LONG_VALUE
        } else UNKNOWN_LONG_VALUE

    private fun extractCodecUsing( mediaMetadataRetriever: MediaMetadataRetriever ) =
        mediaMetadataRetriever.runCatching {
            extractMetadata( MediaMetadataRetriever.METADATA_KEY_MIMETYPE )
        }.getOrNull() ?: UNKNOWN_STRING_VALUE

    private fun extractSamplingRateUsing( mediaMetadataRetriever: MediaMetadataRetriever ) =
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
            mediaMetadataRetriever.runCatching {
                extractMetadata( MediaMetadataRetriever.METADATA_KEY_SAMPLERATE )?.toLong()
            }.getOrNull() ?: UNKNOWN_LONG_VALUE
        } else UNKNOWN_LONG_VALUE

    private fun extractGenreUsing( mediaMetadataRetriever: MediaMetadataRetriever ): String {
        val genre = mediaMetadataRetriever.runCatching {
            extractMetadata( MediaMetadataRetriever.METADATA_KEY_GENRE )
        }.getOrNull() ?: UNKNOWN_STRING_VALUE
        return genre.split( *genreTagSeparators.toTypedArray() ).first()
    }

}

val projection = arrayOf(
    MediaStore.Audio.AudioColumns._ID,
    MediaStore.Audio.AudioColumns.DATE_ADDED,
    MediaStore.Audio.AudioColumns.DATE_MODIFIED,
    MediaStore.Audio.AudioColumns.TITLE,
    MediaStore.Audio.AudioColumns.TRACK,
    MediaStore.Audio.AudioColumns.YEAR,
    MediaStore.Audio.AudioColumns.DURATION,
    MediaStore.Audio.AudioColumns.ALBUM_ID,
    MediaStore.Audio.AudioColumns.ALBUM,
    MediaStore.Audio.AudioColumns.ARTIST_ID,
    MediaStore.Audio.AudioColumns.ARTIST,
    MediaStore.Audio.AudioColumns.COMPOSER,
    MediaStore.Audio.AudioColumns.SIZE,
    MediaStore.Audio.AudioColumns.DATA,
    MediaStore.Audio.AudioColumns.ALBUM_ID,
)

private const val TAG = "LOCAL MUSIC SOURCE"