package com.squad.musicmatters.core.data.songs.impl

import android.content.Context
import android.media.MediaMetadataRetriever
import com.squad.musicmatters.core.data.songs.MetadataStore
import com.squad.musicmatters.core.model.Song
import javax.inject.Inject

class MetadataStoreImpl @Inject constructor(
    context: Context,
) : MetadataStore {

    private val metadataRetriever = MediaMetadataRetriever()

    override fun fetchBitrateFor( song: Song ): Long {
        TODO("Not yet implemented")
    }

    override fun fetchBitsPerSampleFor( song: Song ): Long {
        TODO("Not yet implemented")
    }

    override fun fetchCodecFor( song: Song ): String {
        TODO("Not yet implemented")
    }

    override fun fetchSamplingRateFor( song: Song ): Long {
        TODO("Not yet implemented")
    }

    override fun fetchGenreFor( song: Song ): String {
        TODO("Not yet implemented")
    }

}