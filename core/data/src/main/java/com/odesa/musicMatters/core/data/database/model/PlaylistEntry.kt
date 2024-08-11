package com.odesa.musicMatters.core.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlist_entries",
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = [ "id" ],
            childColumns = [ "playlist_id" ],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class PlaylistEntry(
    @PrimaryKey( autoGenerate = true ) @ColumnInfo( name = "id" ) val id: Long = 0,
    @ColumnInfo( name = "playlist_id" ) val playlistId: String,
    @ColumnInfo( name = "song_id" ) val songId: String,
    @ColumnInfo( name = "date_added" ) val dateAdded: Long = 0
)
