package com.squad.musicmatters.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "song_play_count_entries",
)
data class SongPlayCountEntity(
    @PrimaryKey
    @ColumnInfo( name = "song_id" )
    val songId: String,

    @ColumnInfo( name = "number_of_times_played" )
    val numberOfTimesPlayed: Int = 1
)
