package com.odesa.musicMatters.core.model

import com.odesa.musicMatters.core.i8n.Language


data class SongAdditionalMetadataInfo(
    val id: String,
    val codec: String,
    val bitsPerSample: String,
    val bitrate: String,
    val samplingRate: String,
    val genre: String
) {
    fun toSamplingInfoString( language: Language ): String {
        val values = mutableListOf<String>()
        values.apply {
            add( codec )
            add( language.xBit( bitsPerSample ) )
            add( language.xKbps( bitrate ) )
            add( language.xKHZ( samplingRate ) )
        }
        return values.joinToString( ", " )
    }
}
