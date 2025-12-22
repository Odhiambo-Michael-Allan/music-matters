package com.squad.musicmatters.core.model

import com.squad.musicmatters.core.i8n.Language


data class SongAdditionalMetadataInfo(
    val songId: String,
    val codec: String,
    val bitsPerSample: Long,
    val bitrate: Long,
    val samplingRate: Float,
    val genre: String
) {
    fun toSamplingInfoString( language: Language ): String {
        val values = mutableListOf<String>()
        values.apply {
            add( codec )
            add( language.xBit( bitsPerSample.toString() ) )
            add( language.xKbps( bitrate.toString() ) )
            add( language.xKHZ( samplingRate.toString() ) )
        }
        return values.joinToString( ", " )
    }
}
