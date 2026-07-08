package com.squad.musicmatters.core.data.songs

import com.squad.musicmatters.core.model.Song

interface MetadataStore {
    fun fetchBitrateFor( song: Song): Long
    fun fetchBitsPerSampleFor( song: Song ): Long
    fun fetchCodecFor( song: Song ): String
    fun fetchSamplingRateFor( song: Song ): Long
    fun fetchGenreFor( song: Song ): String
}