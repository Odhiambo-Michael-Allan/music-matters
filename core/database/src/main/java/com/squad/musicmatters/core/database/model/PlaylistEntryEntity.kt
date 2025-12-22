package com.squad.musicmatters.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_entries",
    primaryKeys = [ "playlist_id", "song_id" ],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = [ "id" ],
            childColumns = [ "playlist_id" ],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class PlaylistEntryEntity(
    @ColumnInfo( name = "playlist_id" ) val playlistId: String,
    @ColumnInfo( name = "song_id" ) val songId: String,
)
