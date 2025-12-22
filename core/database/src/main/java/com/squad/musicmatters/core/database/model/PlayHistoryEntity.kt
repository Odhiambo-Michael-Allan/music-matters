package com.squad.musicmatters.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "play_history"
)
data class PlayHistoryEntity(
    @PrimaryKey
    @ColumnInfo( name = "song_id" )
    val songId: String,
    val timePlayed: Instant
)
