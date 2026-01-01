package com.squad.musicmatters.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squad.musicmatters.core.model.SongAdditionalMetadata

@Entity(
    tableName = "songs_additional_metadata",
)
data class SongAdditionalMetadataEntity(
    @PrimaryKey @ColumnInfo( name = "id" ) val songId: String,
    @ColumnInfo( name = "codec" ) val codec: String,
    @ColumnInfo( name = "bits_per_sample" ) val bitsPerSample: Long = 0L,
    @ColumnInfo( name = "bitrate" ) val bitrate: Long = 0L,
    @ColumnInfo( name = "sampling_rate" ) val samplingRate: Long = 0L,
    @ColumnInfo( name = "genre" ) val genre: String
)

fun SongAdditionalMetadataEntity.asExternalModel(): SongAdditionalMetadata =
    SongAdditionalMetadata(
        songId = songId,
        codec = codec,
        bitsPerSample = bitsPerSample,
        bitrate = bitrate,
        samplingRate = samplingRate.toFloat().div( 1000 ),
        genre = genre
    )

