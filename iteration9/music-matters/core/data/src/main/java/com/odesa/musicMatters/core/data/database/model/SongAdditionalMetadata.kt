package com.odesa.musicMatters.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "songs_additional_metadata",
)
data class SongAdditionalMetadata(
    @PrimaryKey @ColumnInfo( name = "id" ) val id: String,
    @ColumnInfo( name = "codec" ) val codec: String,
    @ColumnInfo( name = "bits_per_sample" ) val bitsPerSample: Long,
    @ColumnInfo( name = "bitrate" ) val bitrate: Long,
    @ColumnInfo( name = "sampling_rate" ) val samplingRate: Long
)
