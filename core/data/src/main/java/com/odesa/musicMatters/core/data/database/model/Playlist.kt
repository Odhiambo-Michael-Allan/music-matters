package com.odesa.musicMatters.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlists",
    indices = [
        Index( "id", unique = true )
    ]
)
data class Playlist(
    @PrimaryKey @ColumnInfo( name = "id" ) val id: String,
    @ColumnInfo( name = "title" ) val title: String
)
