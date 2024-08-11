package com.odesa.musicMatters.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "songs_additional_metadata",
)
data class SongAdditionalMetadata(
    @PrimaryKey @ColumnInfo( name = "id" ) val songId: String,
    @ColumnInfo( name = "codec" ) val codec: String,
    @ColumnInfo( name = "bits_per_sample" ) val bitsPerSample: Long = 0L,
    @ColumnInfo( name = "bitrate" ) val bitrate: Long = 0L,
    @ColumnInfo( name = "sampling_rate" ) val samplingRate: Long = 0L,
    @ColumnInfo( name = "genre" ) val genre: String
)
