package com.squad.musicmatters.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "queue"
)
data class QueueEntity(
    @PrimaryKey
    @ColumnInfo( "song_id" )
    val songId: String,
    @ColumnInfo( name = "position_in_queue" )
    val positionInQueue: Int
)
