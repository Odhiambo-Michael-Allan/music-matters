package com.odesa.musicMatters.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "song_play_count_entries",
    indices = [
        Index( "song_id", unique = true )
    ]
)
data class SongPlayCountEntry(
    @PrimaryKey( autoGenerate = true ) @ColumnInfo( name = "id" ) val id: Long = 0,
    @ColumnInfo( name = "song_id" ) val songId: String,
    @ColumnInfo( name = "number_of_times_played" ) val numberOfTimesPlayed: Int = 1
)
