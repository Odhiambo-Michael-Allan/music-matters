package com.squad.musicmatters.core.data.songs.impl

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.core.net.toUri
import com.squad.musicmatters.core.data.songs.MetadataStore
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata
import timber.log.Timber

private const val TAG = "METADATA-STORE"

class MetadataStoreImpl(
    private val context: Context,
) : MetadataStore {

    override fun fetchMetadataFor( songs: List<Song> ): List<SongMetadata> {
        if ( songs.isEmpty() ) return emptyList()
        val retriever = MediaMetadataRetriever()

        return retriever.use { localRetriever ->
            songs.mapNotNull { song ->
                fetchMetadataForSong( localRetriever, song )
            }
        }
    }

    private fun fetchMetadataForSong(
        retriever: MediaMetadataRetriever,
        song: Song
    ): SongMetadata? {
        return try {
            retriever.setDataSource( context, song.mediaUri.toUri() )
            SongMetadata(
                songId = song.id,
                bitrate = retriever.fetchBitrate().div( 1000 ),
                bitsPerSample = retriever.fetchBitsPerSample(),
                codec = retriever.fetchCodec(),
                samplingRate = retriever.fetchSamplingRate(),
                genre = retriever.fetchGenre()
            )
        } catch ( e: Exception ) {
            Timber.tag( TAG ).d(
                "ERROR WHILE FETCHING METADATA FOR SONG WITH TITLE: ${song.title} " +
                        "ERROR -> ${e.localizedMessage}"
            )
            null
        }
    }
}

private fun MediaMetadataRetriever.fetchBitrate() = runCatching {
    extractMetadata( MediaMetadataRetriever.METADATA_KEY_BITRATE )?.toLong()
}.getOrNull() ?: 0

private fun MediaMetadataRetriever.fetchBitsPerSample() =
    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
        runCatching {
            extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE
            )?.toLong()
        }.getOrNull() ?: 0L
    } else 0L

private fun MediaMetadataRetriever.fetchCodec() =
    runCatching {
        extractMetadata( MediaMetadataRetriever.METADATA_KEY_MIMETYPE )
    }.getOrNull() ?: ""

private fun MediaMetadataRetriever.fetchSamplingRate() =
    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
        runCatching {
            extractMetadata( MediaMetadataRetriever.METADATA_KEY_SAMPLERATE )?.toFloat()
        }.getOrNull() ?: 0f
    } else 0f

private fun MediaMetadataRetriever.fetchGenre() =
    runCatching {
        extractMetadata( MediaMetadataRetriever.METADATA_KEY_GENRE )
    }.getOrNull() ?: ""